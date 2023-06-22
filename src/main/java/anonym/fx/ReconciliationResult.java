package anonym.fx;

import org.graalvm.collections.Pair;

import java.util.List;

public class ReconciliationResult {
    public List<Pair<YnabTransaction, BankTransaction>> matchingTransactions;
    public List<YnabTransaction> unmatchedYnabTransactions;
    public List<BankTransaction> unmachedBankTransactions;

    public ReconciliationResult(List<Pair<YnabTransaction, BankTransaction>> matchingTransactions, List<YnabTransaction> unmatchedYnabTransactions, List<BankTransaction> unmachedBankTransactions) {
        this.matchingTransactions = matchingTransactions;
        this.unmatchedYnabTransactions = unmatchedYnabTransactions;
        this.unmachedBankTransactions = unmachedBankTransactions;
    }
}
