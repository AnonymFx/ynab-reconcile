package anonym.fx;

import com.opencsv.bean.CsvToBeanBuilder;
import org.graalvm.collections.Pair;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class YnabReconcile implements Callable<Integer> {


    @CommandLine.Parameters(index = "0", description = "The CSV file exported from YNAB (c.f. https://support.ynab.com/en_us/how-to-export-budget-data-Sy_CouWA9)")
    private File ynabCsv;

    @CommandLine.Parameters(index = "1", description = "The CSV file exported from your bank")
    private File dkbCsv;

    @CommandLine.Option(names = {"-d", "--debug"})
    private boolean debug = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new YnabReconcile()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        List<YnabTransaction> ynabTransactions;
        List<BankTransaction> bankTransactions;
        try (FileReader fileReader = new FileReader(ynabCsv)) {
            ynabTransactions = new CsvToBeanBuilder<YnabTransaction>(fileReader).withType(YnabTransaction.class).build().parse();
        }
        try (FileReader fileReader = new FileReader(dkbCsv)) {
            bankTransactions = new CsvToBeanBuilder<BankTransaction>(fileReader).withType(BankTransaction.class).withSkipLines(4).withSeparator(';').build().parse();
        }

        ReconciliationResult reconciliationResult = matchTransactions(ynabTransactions, bankTransactions);
        printResults(reconciliationResult);

        return 0;
    }

    private ReconciliationResult matchTransactions(List<YnabTransaction> ynabTransactions, List<BankTransaction> bankTransactions) {
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

    private void printResults(ReconciliationResult reconciliationResult) {
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