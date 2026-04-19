package asunfdiez.com.equilibra.workers;

import android.content.Context;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.activities.MainActivity;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.utils.AdviceManager;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * NotificationWorker - Tarea programada para enviar notificaciones diarias
 *
 * Responsabilidad:
 * - Ejecutarse diariamente a una hora específica (8:00 AM)
 * - Obtener el consejo del día
 * - Enviar notificación con el consejo
 * - Deep link a MainActivity cuando se toca la notificación
 *
 * Tecnología: WorkManager API (scheduling flexible)
 * Frecuencia: Una vez al día
 */
public class NotificationWorker extends Worker {

    private static final String NOTIFICATION_CHANNEL_ID = "equilibra_daily_advice";
    private static final int NOTIFICATION_ID = 1001;
    private static final String TAG = "NotificationWorker";

    /**
     * Constructor
     */
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * Ejecutar la tarea (llamado por WorkManager)
     * @return Result.success() si la tarea se completó
     */
    @NonNull
    @Override
    public Result doWork() {
        try {
            // Obtener el consejo del día actual
            Advice dailyAdvice = AdviceManager.getDailyAdvice(getApplicationContext());

            if (dailyAdvice == null) {
                return Result.retry(); // Reintentar si no hay consejo
            }

            // Enviar la notificación
            sendNotification(dailyAdvice);

            android.util.Log.d(TAG, "Notificación enviada: " + dailyAdvice.title());
            return Result.success();

        } catch (Exception e) {
            android.util.Log.e(TAG, "Error al enviar notificación", e);
            return Result.retry(); // Reintentar en caso de error
        }
    }

    /**
     * Enviar la notificación al usuario
     * @param advice Consejo del día a mostrar
     */
    private void sendNotification(@NonNull Advice advice) {
        Context context = getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent para abrir MainActivity cuando se toca la notificación
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fea_lightbulb) // Icono de consejo
                .setContentTitle("💡 " + advice.title())
                .setContentText(advice.description())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(advice.description()))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getResources().getColor(R.color.primary, null));

        // Enviar la notificación
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Obtener el canal de notificación para Android 8.0+
     * @param context Contexto
     * @return ID del canal
     */
    public static String getNotificationChannelId() {
        return NOTIFICATION_CHANNEL_ID;
    }

    /**
     * Crear el canal de notificación (Android 8.0+)
     * Debe llamarse desde la aplicación en onCreate()
     * @param context Contexto
     */
    public static void createNotificationChannel(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Consejos Diarios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Recibe un consejo financiero diferente cada día");

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
