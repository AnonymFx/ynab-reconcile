package anonym.fx;

import com.opencsv.bean.CsvBindByName;

public class BankTransaction {
    @CsvBindByName(column = "Buchungsdatum")
    private String bookingDate;
    @CsvBindByName(column = "Wertstellung")
    private String billingDate;
    @CsvBindByName(column = "Status")
    private String status;
    @CsvBindByName(column = "Zahlungspflichtige*r")
    private String payer;
    @CsvBindByName(column = "Zahlungsempfänger*in")
    private String payee;
    @CsvBindByName(column = "Verwendungszweck")
    private String note;
    @CsvBindByName(column = "Umsatztyp")
    private String transactionType;
    @CsvBindByName(column = "Betrag", capture = "(.*)€", locale = "de-DE")
    private float amount;
    @CsvBindByName(column = "Gläubiger-ID")
    private String creditorId;
    @CsvBindByName(column = "Mandatsreferenz")
    private String mandateReference;
    @CsvBindByName(column = "Kundenreferenz")
    private String customerReference;

    @Override
    public String toString() {
        return "DkbTransaction{" +
                "bookingDate='" + bookingDate + '\'' +
                ", billingDate='" + billingDate + '\'' +
                ", status='" + status + '\'' +
                ", payer='" + payer + '\'' +
                ", payee='" + payee + '\'' +
                ", note='" + note + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", amount='" + amount + '\'' +
                ", creditorId='" + creditorId + '\'' +
                ", mandateReference='" + mandateReference + '\'' +
                ", customerReference='" + customerReference + '\'' +
                '}';
    }
}
