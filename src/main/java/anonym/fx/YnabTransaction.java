package anonym.fx;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class YnabTransaction {
    @CsvBindByName(column = "Account")
    public String account;
    @CsvBindByName(column = "Flag")
    public String flag;
    @CsvBindByName(column = "Date")
    @CsvDate("dd.MM.yyyy")
    public Date date;
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
        return String.format("%s, %s, %s:%s, %.2f€", DateUtils.formatDate(date), payee, category, memo, getAmount());
    }
}
