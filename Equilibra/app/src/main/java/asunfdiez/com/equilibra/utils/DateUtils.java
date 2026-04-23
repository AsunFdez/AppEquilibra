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

    public static long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static boolean isNewWeek(long lastTimestamp) {
        Calendar lastSave = Calendar.getInstance();
        lastSave.setTimeInMillis(lastTimestamp);

        Calendar now = Calendar.getInstance();
        return now.get(Calendar.WEEK_OF_YEAR) != lastSave.get(Calendar.WEEK_OF_YEAR)
                || now.get(Calendar.YEAR) != lastSave.get(Calendar.YEAR);
    }

    public static boolean isNewDay(long lastTimestamp) {
        Calendar lastSave = Calendar.getInstance();
        lastSave.setTimeInMillis(lastTimestamp);

        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_YEAR) != lastSave.get(Calendar.DAY_OF_YEAR)
                || now.get(Calendar.YEAR) != lastSave.get(Calendar.YEAR);
    }

}