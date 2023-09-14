package anonym.fx;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class YnabReconcileTest {

    private static final String BANK_CSV_PATH = "src/test/resources/bank.csv";
    private static final String YNAB_CSV_PATH = "src/test/resources/ynab.csv";

    @Test
    void testYnabCsvParsing() throws IOException {
        List<YnabTransaction> ynabTransactions = YnabReconcile.parseYnabCsv(new File(YNAB_CSV_PATH));
        assertThat(ynabTransactions).hasSize(4);
    }

    @Test
    void testBankCsvParsing() throws IOException {
        List<BankTransaction> bankTransactions = YnabReconcile.parseBankCsv(new File(BANK_CSV_PATH));
        assertThat(bankTransactions).hasSize(2);
    }

    @Test
    void testMatching() throws Exception {
        List<YnabTransaction> ynabTransactions = YnabReconcile.parseYnabCsv(new File(YNAB_CSV_PATH));
        List<YnabTransaction> consolidatedYnabTransactions = YnabReconcile.consolidateYnabSplitTransactions(ynabTransactions);
        consolidatedYnabTransactions.sort(Comparator.comparing(trans -> trans.date));

        List<BankTransaction> bankTransactions = YnabReconcile.parseBankCsv(new File(BANK_CSV_PATH));
        bankTransactions.sort(Comparator.comparing(trans -> trans.bookingDate));

        ReconciliationResult reconciliationResult = YnabReconcile.matchTransactions(consolidatedYnabTransactions, bankTransactions, false);

        assertThat(reconciliationResult.matchingTransactions).hasSize(1);
        assertThat(reconciliationResult.unmatchedYnabTransactions).hasSize(2);
        assertThat(reconciliationResult.unmachedBankTransactions).hasSize(1);
    }
}