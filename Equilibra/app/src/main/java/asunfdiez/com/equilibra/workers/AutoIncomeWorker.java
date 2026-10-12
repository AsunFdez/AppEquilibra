package asunfdiez.com.equilibra.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.DateUtils;

/**
 * Worker que comprueba si debe generarse un ingreso automático mensual.
 */
public class AutoIncomeWorker extends Worker {

    private static final String TAG = "AutoIncomeWorker";

    public AutoIncomeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Comprobando ingreso automático...");
        
        UserProfileRepository userRepo = new UserProfileRepository(getApplicationContext());
        UserProfile profile = userRepo.getUserProfileSync();

        if (profile == null || profile.getMonthlyIncome() == null || profile.getIncomeDay() == null) {
            Log.d(TAG, "No hay configuración de ingreso automático.");
            return Result.success();
        }

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int targetDay = profile.getIncomeDay();

        // Si hoy es el día o ya pasó el día del mes para el ingreso
        if (currentDay >= targetDay) {
            ExpenseRepository expenseRepo = new ExpenseRepository(getApplicationContext());
            
            // Verificar si ya se ha registrado este mes
            if (!expenseRepo.hasAutoIncomeThisMonth(Constants.AUTO_INCOME_DESCRIPTION)) {
                Log.d(TAG, "Generando ingreso automático del mes.");
                
                Expense income = new Expense();
                income.setType(Constants.TYPE_INCOME);
                income.setAmount(profile.getMonthlyIncome());
                income.setDescription(Constants.AUTO_INCOME_DESCRIPTION);
                income.setTimestamp(System.currentTimeMillis());
                
                // Formatear fecha y hora actuales
                java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat(Constants.DATE_FORMAT, java.util.Locale.getDefault());
                java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat(Constants.TIME_FORMAT, java.util.Locale.getDefault());
                
                income.setDate(sdfDate.format(calendar.getTime()));
                income.setTime(sdfTime.format(calendar.getTime()));
                
                expenseRepo.insertExpense(income);
            } else {
                Log.d(TAG, "El ingreso automático ya fue registrado este mes.");
            }
        } else {
            Log.d(TAG, "Aún no es el día del ingreso automático.");
        }

        return Result.success();
    }
}
