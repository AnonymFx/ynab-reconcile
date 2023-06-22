package anonym.fx;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class BankTransaction {
    public static final String DATE_FORMAT = "dd.MM.yy";
    @CsvBindByName(column = "Buchungsdatum")
    @CsvDate(DATE_FORMAT)
    public Date bookingDate;
    @CsvBindByName(column = "Wertstellung")
    @CsvDate(DATE_FORMAT)
    public Date billingDate;
    @CsvBindByName(column = "Status")
    public String status;
    @CsvBindByName(column = "Zahlungspflichtige*r")
    public String payer;
    @CsvBindByName(column = "Zahlungsempfänger*in")
    public String payee;
    @CsvBindByName(column = "Verwendungszweck")
    public String note;
    @CsvBindByName(column = "Umsatztyp")
    public String transactionType;
    @CsvBindByName(column = "Betrag", capture = "(.*)€", locale = "de-DE")
    public float amount;
    @CsvBindByName(column = "Gläubiger-ID")
    public String creditorId;
    @CsvBindByName(column = "Mandatsreferenz")
    public String mandateReference;
    @CsvBindByName(column = "Kundenreferenz")
    public String customerReference;

    public String getPayee() {
        if (payer.equals("ISSUER") || payer.equals("Thomas Pettinger")) {
            return payee;
        }
        return payer;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %.2f€", DateUtils.formatDate(bookingDate), getPayee(), amount);
    }
}
