package anonym.fx;

import com.opencsv.bean.CsvToBeanBuilder;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class YnabReconcile implements Callable<Integer> {


    @CommandLine.Parameters(index = "0", description = "The CSV file exported from YNAB (c.f. https://support.ynab.com/en_us/how-to-export-budget-data-Sy_CouWA9)")
    private File ynabCsv;

    @CommandLine.Parameters(index = "1", description = "The CSV file exported from DKB")
    private File dkbCsv;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new YnabReconcile()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        List<YnabTransaction> ynabTransactions = Collections.emptyList();
        List<DkbTransaction> dkbTransactions = Collections.emptyList();
        try (FileReader fileReader = new FileReader(ynabCsv)) {
            ynabTransactions = new CsvToBeanBuilder<YnabTransaction>(fileReader).withType(YnabTransaction.class).build().parse();
        }
        try (FileReader fileReader = new FileReader(dkbCsv)) {
            dkbTransactions = new CsvToBeanBuilder<DkbTransaction>(fileReader).withType(DkbTransaction.class).withSkipLines(4).withSeparator(';').build().parse();
        }

        return 0;
    }
}