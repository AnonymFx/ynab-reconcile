package anonym.fx;

import com.opencsv.bean.CsvBindByName;

public class YnabTransaction {
    @CsvBindByName(column = "Account")
    public String account;
    @CsvBindByName(column = "Flag")
    public String flag;
    @CsvBindByName(column = "Date")
    public String date;
    @CsvBindByName(column = "Payee")
    public String payee;
    @CsvBindByName(column = "Category Group/Category")
    public String categoryAndGroup;
    @CsvBindByName(column = "Category Group")
    public String categoryGroup;
    @CsvBindByName(column = "Category")
    public String category;
    @CsvBindByName(column = "Memo")
    public String memo;
    @CsvBindByName(column = "Outflow", capture = "(.*)€")
    public float outflow;
    @CsvBindByName(column = "Inflow", capture = "(.*)€")
    public float inflow;
    @CsvBindByName(column = "Cleared")
    public String cleared;

    public float getAmount() {
        if (outflow != 0) {
            return -outflow;
        }
        return inflow;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %.2f€", date, payee, getAmount());
    }
}
