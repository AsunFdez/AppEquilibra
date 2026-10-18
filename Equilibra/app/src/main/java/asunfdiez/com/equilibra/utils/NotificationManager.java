package asunfdiez.com.equilibra.utils;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.activities.AchievementsActivity;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.workers.BudgetNotificationWorker;
import asunfdiez.com.equilibra.workers.InactivityNotificationWorker;
import asunfdiez.com.equilibra.workers.NotificationWorker;

public class NotificationManager {

    private static final String TAG = "NotificationManager";
    
    private static final String DAILY_ADVICE_WORK_NAME = "daily_advice_notification";
    private static final String BUDGET_WORK_NAME = "budget_summary_notification";
    private static final String INACTIVITY_WORK_NAME = "inactivity_reminder_notification";

    public static void scheduleDailyNotifications(@NonNull Context context) {
        NotificationWorker.createNotificationChannel(context);
        BudgetNotificationWorker.createNotificationChannel(context);
        InactivityNotificationWorker.createNotificationChannel(context);
        createAchievementChannel(context);

        // 1. Consejos diarios a las 08:00
        long delayAdvice = calculateInitialDelay(8, 0);
        PeriodicWorkRequest adviceWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delayAdvice, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(DAILY_ADVICE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, adviceWork);

        // 2. Inactividad a las 19:00
        long delayInactivity = calculateInitialDelay(19, 0);
        PeriodicWorkRequest inactivityWork = new PeriodicWorkRequest.Builder(InactivityNotificationWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delayInactivity, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(INACTIVITY_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, inactivityWork);

        // 3. Resumen de presupuesto a las 21:30
        long delayBudget = calculateInitialDelay(21, 30);
        PeriodicWorkRequest budgetWork = new PeriodicWorkRequest.Builder(BudgetNotificationWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delayBudget, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(BUDGET_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, budgetWork);

        android.util.Log.d(TAG, "Todas las notificaciones diarias programadas.");
    }

    public static void cancelDailyNotifications(@NonNull Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_ADVICE_WORK_NAME);
        WorkManager.getInstance(context).cancelUniqueWork(BUDGET_WORK_NAME);
        WorkManager.getInstance(context).cancelUniqueWork(INACTIVITY_WORK_NAME);
        android.util.Log.d(TAG, "Notificaciones canceladas");
    }

    public static void sendAchievementNotification(@NonNull Context context, @NonNull Achievement achievement) {
        boolean achievementNotifsEnabled = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(Constants.PREF_ACHIEVEMENT_NOTIFICATIONS, true);

        if (!achievementNotifsEnabled) return;

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, AchievementsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "equilibra_achievement_unlock")
                .setSmallIcon(R.drawable.ic_achievement_unlocked)
                .setContentTitle("🏆 ¡Nueva medalla conseguida!")
                .setContentText(achievement.getTitle() + " (+" + achievement.getPoints() + " pts)")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(achievement.getDescription() + "\n\n+" + achievement.getPoints() + " pts"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (notificationManager != null) {
            createAchievementChannel(context);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private static void createAchievementChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                "equilibra_achievement_unlock",
                "Logros y Medallas",
                android.app.NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notificaciones al conseguir nuevas medallas y logros");
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static long calculateInitialDelay(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();

        scheduledTime.set(Calendar.HOUR_OF_DAY, hour);
        scheduledTime.set(Calendar.MINUTE, minute);
        scheduledTime.set(Calendar.SECOND, 0);

        if (now.after(scheduledTime)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long differenceMillis = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
        return differenceMillis / (1000 * 60);
    }

    public static String getScheduledTime() {
        return "08:00, 19:00 y 21:30";
    }
}
