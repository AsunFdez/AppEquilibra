package asunfdiez.com.equilibra.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.activities.MainActivity;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.Constants;

public class InactivityNotificationWorker extends Worker {

    private static final String NOTIFICATION_CHANNEL_ID = "equilibra_inactivity_reminder";
    private static final int NOTIFICATION_ID = 1003;
    private static final String TAG = "InactivityNotificationWorker";

    public InactivityNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        boolean notifsEnabled = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, true);

        if (!notifsEnabled) {
            return Result.success();
        }

        try {
            UserProfileRepository userRepo = new UserProfileRepository(context);
            UserProfile profile = userRepo.getUserProfileSync();
            if (profile != null) {
                long lastConn = profile.getLastConnectionTimestamp();
                Calendar lastCal = Calendar.getInstance();
                lastCal.setTimeInMillis(lastConn);

                Calendar todayCal = Calendar.getInstance();
                boolean openedToday = lastCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        lastCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR);

                if (!openedToday) {
                    sendNotification(context, "👋 ¡Te echamos de menos en Equilibra!", 
                            "Recuerda registrar tus gastos de hoy y revisar tus finanzas.");
                }
            }
            return Result.success();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error in InactivityNotificationWorker", e);
            return Result.retry();
        }
    }

    private void sendNotification(Context context, String title, String text) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_piggy_bank)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public static void createNotificationChannel(@NonNull Context context) {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Recordatorios de Actividad",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Recordatorio a las 19:00 si no has abierto la app hoy");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
