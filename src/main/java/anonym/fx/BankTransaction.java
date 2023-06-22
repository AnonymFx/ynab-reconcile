package anonym.fx;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class BankTransaction {
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    @CsvBindByName(column = "Buchungstag")
    @CsvDate(DATE_FORMAT)
    public Date bookingDate;
    @CsvBindByName(column = "Auftraggeber / Beg�nstigter")
    public String payee;
    @CsvBindByName(column = "Verwendungszweck")
    public String note;
    @CsvBindByName(column = "Betrag (EUR)", locale = "de-DE")
    public float amount;

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %.2f€", DateUtils.formatDate(bookingDate), payee, note, amount);
    }
}
