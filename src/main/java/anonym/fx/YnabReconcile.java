package anonym.fx;

import com.opencsv.bean.CsvToBeanBuilder;
import org.graalvm.collections.Pair;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class YnabReconcile implements Callable<Integer> {

    private static final String SPLIT_REGEX = ".*Split \\((\\d+)/(\\d+)\\).*";

    @CommandLine.Parameters(index = "0", description = "The CSV file exported from YNAB (c.f. https://support.ynab.com/en_us/how-to-export-budget-data-Sy_CouWA9)")
    private File ynabCsv;

    @CommandLine.Parameters(index = "1", description = "The CSV file exported from your bank")
    private File bankCsv;

    @CommandLine.Option(names = {"-d", "--debug"})
    private boolean debug = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new YnabReconcile()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        List<YnabTransaction> ynabTransactions = parseYnabCsv(ynabCsv);
        List<YnabTransaction> consolidatedYnabTransactions = consolidateYnabSplitTransactions(ynabTransactions);
        consolidatedYnabTransactions.sort(Comparator.comparing(trans -> trans.date));

        List<BankTransaction> bankTransactions = parseBankCsv(bankCsv);
        bankTransactions.sort(Comparator.comparing(trans -> trans.bookingDate));

        ReconciliationResult reconciliationResult = matchTransactions(consolidatedYnabTransactions, bankTransactions, debug);

        printResults(reconciliationResult);

        return 0;
    }

    static List<BankTransaction> parseBankCsv(File bankCsv) throws IOException {
        List<BankTransaction> bankTransactions = Collections.emptyList();
        try (FileReader fileReader = new FileReader(bankCsv)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            System.out.println("!!! Skipping the first 4 lines in the bank CSV to remove headers in the DKB export format !!!");
            // Skip first 4 (header) lines, see bank.csv
            bufferedReader.readLine();
            bufferedReader.readLine();
            bufferedReader.readLine();
            bufferedReader.readLine();
            bankTransactions = new CsvToBeanBuilder<BankTransaction>(bufferedReader).withType(BankTransaction.class).withSeparator(';').build().parse();
        } catch (RuntimeException e) {
            System.err.println();
            System.err.println("Could not read the Bank CSV file. Did you forget to remove the first couple of lines (\"header\" from DKB CSV export)?");
        }
        return bankTransactions;
    }

    static List<YnabTransaction> parseYnabCsv(File ynabCsv) throws IOException {
        List<YnabTransaction> ynabTransactions;
        try (FileReader fileReader = new FileReader(ynabCsv)) {
            ynabTransactions = new CsvToBeanBuilder<YnabTransaction>(fileReader).withType(YnabTransaction.class).build().parse();
        }
        return ynabTransactions;
    }

    static List<YnabTransaction> consolidateYnabSplitTransactions(List<YnabTransaction> ynabTransactions) throws Exception {
        ArrayList<YnabTransaction> consolidatedYnabTransactions = new ArrayList<>();

        for (int i = 0; i < ynabTransactions.size(); i++) {
            YnabTransaction currentTransaction = ynabTransactions.get(i);
            Pattern splitPattern = Pattern.compile(SPLIT_REGEX);
            Matcher currentTransactionSplitMatcher = splitPattern.matcher(currentTransaction.memo);
            if (!currentTransactionSplitMatcher.matches()) {
                consolidatedYnabTransactions.add(currentTransaction);
                continue;
            }

            int currentTransactionSplitIndex = Integer.parseInt(currentTransactionSplitMatcher.group(1));
            int numberOfSplitsForTransaction = Integer.parseInt(currentTransactionSplitMatcher.group(2));
            List<YnabTransaction> splitTransactions = ynabTransactions.subList(i, i + numberOfSplitsForTransaction);
            boolean sanityCheckLastSplitTransactionIsActuallyLast =
                    splitPattern.matcher(splitTransactions.get(splitTransactions.size() - 1).memo).matches();

            if (currentTransactionSplitIndex != 1 || !sanityCheckLastSplitTransactionIsActuallyLast) {
                throw new Exception("Could not merge splits, please check that they are in the correct order one after " +
                        "another in the CSV.");
            }

            String consolidateSplitFillerText = "Consolidated from " + numberOfSplitsForTransaction + " splits";
            float sumInflow = splitTransactions.stream().map(transaction -> transaction.inflow).reduce(0f, Float::sum);
            float sumOutflow = splitTransactions.stream().map(transaction -> transaction.outflow).reduce(0f, Float::sum);
            YnabTransaction consolidatedTransaction = new YnabTransaction(currentTransaction.account,
                    currentTransaction.flag, currentTransaction.date, currentTransaction.payee, consolidateSplitFillerText,
                    consolidateSplitFillerText, consolidateSplitFillerText, consolidateSplitFillerText, sumOutflow,
                    sumInflow, consolidateSplitFillerText);

            consolidatedYnabTransactions.add(consolidatedTransaction);
            i = i + numberOfSplitsForTransaction - 1;
        }

        return consolidatedYnabTransactions;
    }

    static ReconciliationResult matchTransactions(List<YnabTransaction> ynabTransactions, List<BankTransaction> bankTransactions, boolean debug) {
        List<Pair<YnabTransaction, BankTransaction>> matchingTransactions = new ArrayList<>();
        ArrayList<YnabTransaction> remainingYnabTransactions = new ArrayList<>(ynabTransactions);
        ArrayList<BankTransaction> remainingBankTransactions = new ArrayList<>(bankTransactions);

        bankTransactions.forEach(bankTransaction -> {
            List<YnabTransaction> matching = remainingYnabTransactions.stream().filter(ynabTransaction -> ynabTransaction.getAmount() == bankTransaction.amount).toList();

            Optional<YnabTransaction> firstMatch = matching.stream().findFirst();
            if (firstMatch.isEmpty()) {
                return;
            }

            if (matching.size() > 1 && debug) {
                System.out.printf("Found multiple YNAB transactions matching bank transaction %s: %s\n", bankTransaction, matching);
            }

            YnabTransaction ynabTransaction = firstMatch.get();
            remainingYnabTransactions.remove(ynabTransaction);
            remainingBankTransactions.remove(bankTransaction);
            matchingTransactions.add(Pair.create(ynabTransaction, bankTransaction));
        });

        return new ReconciliationResult(matchingTransactions, remainingYnabTransactions, remainingBankTransactions);
    }

    private static void printResults(ReconciliationResult reconciliationResult) {
        System.out.println("Transactions only found in Bank");
        reconciliationResult.unmachedBankTransactions.forEach(System.out::println);

        System.out.println();

        System.out.println("Transactions only found in YNAB");
        reconciliationResult.unmatchedYnabTransactions.forEach(System.out::println);

        System.out.println();

        System.out.println("Matching transactions (YNAB <----> Bank)");
        Integer maxStringLength = reconciliationResult.matchingTransactions.stream().map(pair -> pair.getLeft().toString().length()).max(Comparator.naturalOrder()).orElse(0);
        reconciliationResult.matchingTransactions.forEach(pair -> System.out.printf("%" + maxStringLength + "s <----> %s%n", pair.getLeft(), pair.getRight()));
    }
}