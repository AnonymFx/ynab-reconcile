package anonym.fx;

import com.opencsv.bean.CsvBindByName;

public class YnabTransaction {
    @CsvBindByName(column = "Account")
    private String account;
    @CsvBindByName(column = "Flag")
    private String flag;
    @CsvBindByName(column = "Date")
    private String date;
    @CsvBindByName(column = "Payee")
    private String payee;
    @CsvBindByName(column = "Category Group/Category")
    private String categoryAndGroup;
    @CsvBindByName(column = "Category Group")
    private String categoryGroup;
    @CsvBindByName(column = "Category")
    private String category;
    @CsvBindByName(column = "Memo")
    private String memo;
    @CsvBindByName(column = "Outflow", capture = "(.*)€")
    private float outflow;
    @CsvBindByName(column = "Inflow", capture = "(.*)€")
    private float inflow;
    @CsvBindByName(column = "Cleared")
    private String cleared;

    @Override
    public String toString() {
        return "YnabTransaction{" +
                "account='" + account + '\'' +
                ", flag='" + flag + '\'' +
                ", date='" + date + '\'' +
                ", payee='" + payee + '\'' +
                ", categoryAndGroup='" + categoryAndGroup + '\'' +
                ", categoryGroup='" + categoryGroup + '\'' +
                ", category='" + category + '\'' +
                ", memo='" + memo + '\'' +
                ", outflow='" + outflow + '\'' +
                ", inflow='" + inflow + '\'' +
                ", cleared='" + cleared + '\'' +
                '}';
    }
}
