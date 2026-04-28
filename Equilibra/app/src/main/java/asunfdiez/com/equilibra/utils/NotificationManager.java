package asunfdiez.com.equilibra.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import asunfdiez.com.equilibra.controllers.NotificationWorker;

/**
 * Gestor encargado de la programación y administración de notificaciones del sistema.
 * 
 * <p>Utiliza {@link WorkManager} para asegurar que las notificaciones (como los 
 * consejos diarios o recordatorios de presupuesto) se entreguen de forma fiable, 
 * incluso tras reinicios del dispositivo. Centraliza la configuración de canales 
 * y horarios de entrega.</p>
 */
public class NotificationManager {

    private static final String TAG = "NotificationManager";
    
    // Nombre único para la tarea periódica de consejos diarios
    private static final String DAILY_ADVICE_WORK_NAME = "daily_advice_notification";
    
    // Hora programada por defecto para la notificación matutina (8:00 AM)
    private static final int NOTIFICATION_HOUR = 8; 
    private static final int NOTIFICATION_MINUTE = 0;

    /**
     * Programa una tarea periódica para mostrar notificaciones diarias de consejos.
     * La tarea se configura para ejecutarse cada 24 horas.
     *
     * @param context Contexto de la aplicación.
     */
    public static void scheduleDailyNotifications(@NonNull Context context) {
        // Calcular tiempo hasta la próxima ejecución
        long initialDelay = calculateInitialDelay();

        // Crear la tarea periódica (cada 24 horas)
        PeriodicWorkRequest dailyNotificationWork =
                new PeriodicWorkRequest.Builder(
                        NotificationWorker.class,
                        1, // Cada 1
                        TimeUnit.DAYS  // día
                )
                .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();

        // Programar con REPLACE policy
        // Si ya existe, la reemplaza (para evitar duplicados)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DAILY_ADVICE_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyNotificationWork
        );

        android.util.Log.d(TAG, "Notificaciones programadas. " +
                "Primera ejecución en " + initialDelay + " minutos");
    }

    /**
     * Cancelar todas las notificaciones programadas
     * @param context Contexto
     */
    public static void cancelDailyNotifications(@NonNull Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_ADVICE_WORK_NAME);
        android.util.Log.d(TAG, "Notificaciones canceladas");
    }

    /**
     * Verificar si las notificaciones están programadas
     * @param context Contexto
     * @return true si hay trabajo programado
     */
    public static boolean isDailyNotificationScheduled(@NonNull Context context) {
        try {
            java.util.List<androidx.work.WorkInfo> workInfos = WorkManager.getInstance(context)
                    .getWorkInfosForUniqueWork(DAILY_ADVICE_WORK_NAME)
                    .get();

            if (workInfos == null || workInfos.isEmpty()) {
                return false;
            }

            androidx.work.WorkInfo workInfo = workInfos.get(0);
            return workInfo.getState() == androidx.work.WorkInfo.State.ENQUEUED ||
                   workInfo.getState() == androidx.work.WorkInfo.State.RUNNING;
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error al verificar estado", e);
            return false;
        }
    }

    /**
     * Calcular el retraso inicial hasta la siguiente ejecución a las 8:00 AM
     * Si ya pasó las 8:00 AM hoy, programa para mañana a las 8:00 AM
     *
     * @return Tiempo en minutos hasta la próxima ejecución
     */
    private static long calculateInitialDelay() {
        Calendar now = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();

        // Establecer la hora objetivo (8:00 AM)
        scheduledTime.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        scheduledTime.set(Calendar.MINUTE, NOTIFICATION_MINUTE);
        scheduledTime.set(Calendar.SECOND, 0);

        // Si ya pasó la hora hoy, programar para mañana
        if (now.after(scheduledTime)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Calcular diferencia en minutos
        long differenceMillis = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
        return differenceMillis / (1000 * 60); // Convertir a minutos
    }

    /**
     * Obtener la hora programada
     * @return String con formato "HH:mm"
     */
    public static String getScheduledTime() {
        return String.format("%02d:%02d", NOTIFICATION_HOUR, NOTIFICATION_MINUTE);
    }
}
