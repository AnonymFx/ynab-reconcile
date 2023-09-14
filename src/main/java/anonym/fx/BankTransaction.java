package anonym.fx;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class BankTransaction {
    private static final String DATE_FORMAT = "dd.MM.yy";

    @CsvBindByName(column = "Wertstellung")
    @CsvDate(DATE_FORMAT)
    public Date bookingDate;
    @CsvBindByName(column = "Zahlungsempfänger*in")
    public String payee;

    @CsvBindByName(column = "Zahlungspflichtige*r")
    public String payer;

    @CsvBindByName(column = "Verwendungszweck")
    public String note;

    @CsvBindByName(column = "Betrag", capture = "(.*) €", locale = "de-DE")
    public float amount;

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %.2f€", DateUtils.formatDate(bookingDate),
                payee.replaceAll("\\s+", " ") + " - " + payer.replaceAll("\\s+", " "),
                note.replaceAll("\\s+", " "), amount);
    }
}
