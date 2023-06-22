package anonym.fx;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static String formatDate(Date date) {
        return OUTPUT_DATE_FORMAT.format(date);
    }
}
