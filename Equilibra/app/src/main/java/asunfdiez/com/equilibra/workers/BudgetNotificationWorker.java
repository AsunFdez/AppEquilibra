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
import java.util.Locale;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.activities.MainActivity;
import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.repositories.BudgetRepository;
import asunfdiez.com.equilibra.utils.Constants;

public class BudgetNotificationWorker extends Worker {

    private static final String NOTIFICATION_CHANNEL_ID = "equilibra_budget_summary";
    private static final int NOTIFICATION_ID = 1002;
    private static final String TAG = "BudgetNotificationWorker";

    public BudgetNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        boolean budgetNotifsEnabled = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(Constants.PREF_BUDGET_NOTIFICATIONS, true);

        if (!budgetNotifsEnabled) {
            return Result.success();
        }

        try {
            BudgetRepository budgetRepo = new BudgetRepository(context);
            Budget activeBudget = budgetRepo.getActiveBudgetSync();
            if (activeBudget == null || !activeBudget.isEnabled()) {
                return Result.success();
            }

            ExpenseController expenseController = new ExpenseController(context);
            double monthlyExpenses = expenseController.getTotalExpensesThisMonth();

            Calendar todayStart = Calendar.getInstance();
            todayStart.set(Calendar.HOUR_OF_DAY, 0);
            todayStart.set(Calendar.MINUTE, 0);
            todayStart.set(Calendar.SECOND, 0);
            double todayExpenses = expenseController.getTotalExpensesInRange(todayStart.getTimeInMillis(), System.currentTimeMillis());

            Calendar weekStart = Calendar.getInstance();
            weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());
            weekStart.set(Calendar.HOUR_OF_DAY, 0);
            weekStart.set(Calendar.MINUTE, 0);
            weekStart.set(Calendar.SECOND, 0);
            double weeklyExpenses = expenseController.getTotalExpensesInRange(weekStart.getTimeInMillis(), System.currentTimeMillis());

            String message = String.format(Locale.getDefault(), "Hoy: %.2f€ / Diario: %.2f€\nSemanal: %.2f€ / %.2f€\nMensual: %.2f€ / %.2f€",
                    todayExpenses, activeBudget.getDailyBudget(),
                    weeklyExpenses, activeBudget.getWeeklyBudget(),
                    monthlyExpenses, activeBudget.getMonthlyBudget());

            sendNotification(context, "📊 Resumen diario de presupuesto", message);
            return Result.success();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error in BudgetNotificationWorker", e);
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
                .setSmallIcon(R.drawable.ic_fea_receipt_euro)
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
                "Resumen de Presupuesto",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Resumen diario del consumo de presupuesto a las 21:30");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
