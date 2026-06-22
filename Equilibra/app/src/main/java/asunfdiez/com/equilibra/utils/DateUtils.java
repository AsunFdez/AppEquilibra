package asunfdiez.com.equilibra.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Clase de utilidad para el procesamiento y formateo de fechas y horas.
 * 
 * <p>Centraliza la lógica de conversión entre objetos Calendar, marcas de tiempo 
 * (timestamps) y representaciones en cadena de texto legibles, asegurando que 
 * se utilice el formato y la zona horaria consistentes en toda la aplicación.</p>
 */
public class DateUtils {


    /**
     * Convierte un objeto Calendar a una cadena de fecha formateada según Constants.DATE_FORMAT.
     */
    public static String formatDate(Calendar calendar) {
        if (calendar == null) return "";

        // Forzamos el uso de la zona horaria local y el locale del sistema
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(calendar.getTime());
    }

    /**
     * Convierte una marca de tiempo en milisegundos a una cadena de fecha formateada.
     */
    public static String formatDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return formatDate(calendar);
    }

    /**
     * Convierte un objeto Calendar a una cadena de hora formateada según Constants.TIME_FORMAT.
     */
    public static String formatTime(Calendar calendar) {
        if (calendar == null) return "";

        // Forzamos el uso de la zona horaria local
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(calendar.getTime());
    }

    /**
     * Convierte una marca de tiempo en milisegundos a una cadena de hora formateada.
     */
    public static String formatTime(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return formatTime(calendar);
    }

    /**
     * Convierte una marca de tiempo en milisegundos a una cadena de fecha y hora formateada.
     */
    public static String formatDateTime(long timestamp) {
        if (timestamp <= 0) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(calendar.getTime());
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