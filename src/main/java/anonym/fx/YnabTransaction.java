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
    @CsvBindByName(column = "Outflow", capture = "(.*)€", locale = "en-US")
    public float outflow;
    @CsvBindByName(column = "Inflow", capture = "(.*)€", locale = "en-US")
    public float inflow;
    @CsvBindByName(column = "Cleared")
    public String cleared;

    public YnabTransaction(String account, String flag, Date date, String payee, String categoryAndGroup, String categoryGroup, String category, String memo, float outflow, float inflow, String cleared) {
        this.account = account;
        this.flag = flag;
        this.date = date;
        this.payee = payee;
        this.categoryAndGroup = categoryAndGroup;
        this.categoryGroup = categoryGroup;
        this.category = category;
        this.memo = memo;
        this.outflow = outflow;
        this.inflow = inflow;
        this.cleared = cleared;
    }

    public YnabTransaction() {
    }

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
