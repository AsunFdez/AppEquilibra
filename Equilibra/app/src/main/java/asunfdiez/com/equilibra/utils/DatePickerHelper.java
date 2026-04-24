
package asunfdiez.com.equilibra.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Clase de utilidad para gestionar la selección de fechas y horas mediante diálogos nativos.
 * 
 * <p>Simplifica la visualización de DatePickerDialog y TimePickerDialog, vinculando 
 * automáticamente la selección con campos de texto (TextInputEditText) y proporcionando 
 * interfaces de escucha para gestionar los resultados de la selección.</p>
 */
public class DatePickerHelper {
    private final Context context;

    /**
     * Interfaz para recibir la notificación de una fecha seleccionada.
     */
    public interface OnDateSelectedListener {
        void onSelected(Calendar calendar);
    }

    /**
     * Interfaz para recibir la notificación de una hora seleccionada.
     */
    public interface OnTimeSelectedListener {
        void onSelected(Calendar calendar);
    }

    public DatePickerHelper(Context context) {
        this.context = context;
    }

    /**
     * Muestra un selector de fecha y actualiza el campo de texto con el resultado formateado.
     */
    public void showDatePicker(final TextInputEditText dateInput, Calendar calendar) {

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    dateInput.setText(formatDate(selectedDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }


    /**
     * Muestra el diálogo de selección de fecha con callback.
     */
    public void showDatePicker(final TextInputEditText input, Calendar calendar, OnDateSelectedListener listener) {
        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    input.setText(formatDate(calendar));
                    if (listener != null) listener.onSelected(calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    /**
     * Muestra el diálogo de selección de hora con callback.
     */
    public void showTimePicker(final TextInputEditText input, Calendar calendar, OnTimeSelectedListener listener) {
        TimePickerDialog dialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    input.setText(formatTime(calendar));
                    if (listener != null) listener.onSelected(calendar);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Formato 24h
        );
        dialog.show();
    }

    public String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public String formatTime(Calendar calendar) {
        return new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault()).format(calendar.getTime());
    }


}
