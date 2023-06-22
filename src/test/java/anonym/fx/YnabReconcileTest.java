package anonym.fx;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class YnabReconcileTest {

    private static final String BANK_CSV_PATH = "src/test/resources/bank.csv";
    private static final String YNAB_CSV_PATH = "src/test/resources/ynab.csv";

    @Test
    void testYnabCsvParsing() throws IOException {
        List<YnabTransaction> ynabTransactions = YnabReconcile.parseYnabCsv(new File(YNAB_CSV_PATH));
        assertThat(ynabTransactions).hasSize(2);
    }

    @Test
    void testBankCsvParsing() throws IOException {
        List<BankTransaction> bankTransactions = YnabReconcile.parseBankCsv(new File(BANK_CSV_PATH));
        assertThat(bankTransactions).hasSize(2);
    }

    @Test
    void testMatching() throws IOException {
        List<YnabTransaction> ynabTransactions = YnabReconcile.parseYnabCsv(new File(YNAB_CSV_PATH));
        List<BankTransaction> bankTransactions = YnabReconcile.parseBankCsv(new File(BANK_CSV_PATH));
        ReconciliationResult reconciliationResult = YnabReconcile.matchTransactions(ynabTransactions, bankTransactions, false);
        assertThat(reconciliationResult.matchingTransactions).hasSize(1);
        assertThat(reconciliationResult.unmatchedYnabTransactions).hasSize(1);
        assertThat(reconciliationResult.unmachedBankTransactions).hasSize(1);
    }
}