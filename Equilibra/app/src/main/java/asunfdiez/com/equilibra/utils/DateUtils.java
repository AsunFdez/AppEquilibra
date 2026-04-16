package asunfdiez.com.equilibra.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {


    public static String formatDate(Calendar calendar) {
        if (calendar == null) return "";

        // Forzamos el uso de la zona horaria local y el locale del sistema
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(calendar.getTime());
    }

    public static String formatDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return formatDate(calendar);
    }

    public static String formatTime(Calendar calendar) {
        if (calendar == null) return "";

        // Forzamos el uso de la zona horaria local
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(calendar.getTime());
    }

    public static String formatTime(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return formatTime(calendar);
    }
}