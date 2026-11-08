package asunfdiez.com.equilibra.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.DatabaseBackupDto;
import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.models.XPAuditLog;

/**
 * Utilidad para exportar e importar la base de datos completa en formato JSON utilizando Gson.
 */
public class DatabaseBackupHelper {

    private static final String TAG = "DatabaseBackupHelper";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Exporta toda la base de datos a una cadena JSON.
     *
     * @param context Contexto de la aplicación
     * @return Cadena JSON con la copia de seguridad de todos los datos
     */
    @NonNull
    public static String exportDatabaseToJson(@NonNull Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        DatabaseBackupDto dto = new DatabaseBackupDto();

        UserProfile profile = db.userProfileDao().getUserProfileSync();
        List<UserProfile> profiles = new ArrayList<>();
        if (profile != null) {
            profiles.add(profile);
        }
        dto.setUserProfiles(profiles);
        dto.setSavingGoals(db.savingGoalDao().getAllSavingGoalsSync());
        dto.setExpenses(db.expenseDao().getAllExpensesSync());
        dto.setExpenseCategories(db.expenseCategoryDao().getCategories());
        dto.setAchievements(db.achievementDao().getAllAchievementsSync());
        dto.setAlternatives(db.alternativeDao().getAllAlternatives());
        dto.setStreaks(db.streakDao().getAllStreaks());
        dto.setUserXPs(db.userXPDao().getAllUserXpSync());
        dto.setBudgets(db.budgetDao().getAllBudgetsSync());
        dto.setInterventionLogs(db.interventionLogDao().getAllInterventionLogsSync());
        dto.setXpAuditLogs(db.xpAuditLogDao().getAllXpAuditLogsSync());

        return gson.toJson(dto);
    }

    /**
     * Exporta toda la base de datos a un archivo JSON mediante un Uri (SAF).
     *
     * @param context Contexto de la aplicación
     * @param uri     Uri del archivo de destino
     * @return true si la exportación fue exitosa, false en caso contrario
     */
    public static boolean exportDatabaseToFile(@NonNull Context context, @NonNull Uri uri) {
        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            String json = exportDatabaseToJson(context);
            writer.write(json);
            writer.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error exporting database to file", e);
            return false;
        }
    }

    /**
     * Importa y restaura la base de datos completa desde un archivo JSON seleccionado mediante un Uri (SAF).
     *
     * @param context Contexto de la aplicación
     * @param uri     Uri del archivo JSON de origen
     * @return true si la importación y restauración fue exitosa, false en caso contrario
     */
    public static boolean importDatabaseFromFile(@NonNull Context context, @NonNull Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            DatabaseBackupDto dto = gson.fromJson(sb.toString(), DatabaseBackupDto.class);
            if (dto == null) {
                return false;
            }

            AppDatabase db = AppDatabase.getInstance(context);

            // Realizar la restauración en una transacción segura
            db.runInTransaction(() -> {
                SupportSQLiteDatabase sqliteDb = db.getOpenHelper().getWritableDatabase();
                sqliteDb.execSQL("PRAGMA foreign_keys = OFF;");
                try {
                    // Limpiar tablas existentes en orden de dependencia (hijos primero)
                    db.xpAuditLogDao().deleteAll();
                    db.interventionLogDao().deleteAll();
                    db.budgetDao().deleteAll();
                    db.userXPDao().deleteAll();
                    db.streakDao().deleteAll();
                    db.alternativeDao().deleteAll();
                    db.achievementDao().deleteAll();
                    db.expenseDao().deleteAll();
                    db.savingGoalDao().deleteAll();
                    db.expenseCategoryDao().deleteAll();
                    db.userProfileDao().deleteAll();

                    // Insertar datos importados en orden de dependencia (padres primero)
                    if (dto.getUserProfiles() != null && !dto.getUserProfiles().isEmpty()) {
                        db.userProfileDao().insertOrUpdate(dto.getUserProfiles().get(0));
                    }
                    if (dto.getExpenseCategories() != null && !dto.getExpenseCategories().isEmpty()) {
                        db.expenseCategoryDao().insertListOfCategories(dto.getExpenseCategories());
                    }
                    if (dto.getSavingGoals() != null && !dto.getSavingGoals().isEmpty()) {
                        for (SavingGoal goal : dto.getSavingGoals()) {
                            db.savingGoalDao().insertOrUpdateSavingGoal(goal);
                        }
                    }
                    if (dto.getExpenses() != null && !dto.getExpenses().isEmpty()) {
                        db.expenseDao().insertListOfExpenses(dto.getExpenses());
                    }
                    if (dto.getAlternatives() != null && !dto.getAlternatives().isEmpty()) {
                        db.alternativeDao().insertListOfAlternatives(dto.getAlternatives());
                    }
                    if (dto.getAchievements() != null && !dto.getAchievements().isEmpty()) {
                        db.achievementDao().insertListOfAchievements(dto.getAchievements());
                    }
                    if (dto.getStreaks() != null && !dto.getStreaks().isEmpty()) {
                        db.streakDao().insertListOfStreak(dto.getStreaks());
                    }
                    if (dto.getUserXPs() != null && !dto.getUserXPs().isEmpty()) {
                        for (UserXP xp : dto.getUserXPs()) {
                            db.userXPDao().insertUserXP(xp);
                        }
                    }
                    if (dto.getBudgets() != null && !dto.getBudgets().isEmpty()) {
                        for (Budget b : dto.getBudgets()) {
                            db.budgetDao().insert(b);
                        }
                    }
                    if (dto.getInterventionLogs() != null && !dto.getInterventionLogs().isEmpty()) {
                        for (InterventionLog log : dto.getInterventionLogs()) {
                            db.interventionLogDao().insertInterventionLog(log);
                        }
                    }
                    if (dto.getXpAuditLogs() != null && !dto.getXpAuditLogs().isEmpty()) {
                        for (XPAuditLog log : dto.getXpAuditLogs()) {
                            db.xpAuditLogDao().insertLog(log);
                        }
                    }
                } finally {
                    sqliteDb.execSQL("PRAGMA foreign_keys = ON;");
                }
            });

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error importing database from file", e);
            return false;
        }
    }
}
