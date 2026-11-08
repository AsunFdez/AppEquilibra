package asunfdiez.com.equilibra.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.controllers.AchievementController;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.UserProfile;

/**
 * Utilidad para exportar e importar datos de gastos en formato CSV.
 * Permite realizar copias de seguridad e incorporar datos históricos o de prueba
 * utilizando el Framework de Acceso a Almacenamiento (SAF) de Android.
 */
public class CsvExportImportUtils {

    private static final String TAG = "CsvExportImportUtils";

    /**
     * Exporta una lista de gastos a un archivo CSV mediante un Uri de destino.
     */
    public static boolean exportExpenses(Context context, Uri uri, List<Expense> expenses) {
        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

            writer.write("type,amount,category,description,date,time,timestamp\n");
            for (Expense e : expenses) {
                writer.write(String.format(java.util.Locale.US, "%s,%.2f,%d,\"%s\",%s,%s,%d\n",
                        e.getType() != null ? e.getType() : "expense",
                        e.getAmount(),
                        e.getCategory() != null ? e.getCategory() : 1L,
                        e.getDescription() != null ? e.getDescription().replace("\"", "\"\"") : "",
                        e.getDate() != null ? e.getDate() : "",
                        e.getTime() != null ? e.getTime() : "12:00",
                        e.getTimestamp()
                ));
            }
            writer.flush();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "Error exporting expenses to CSV", ex);
            return false;
        }
    }

    /**
     * Importa gastos desde un archivo CSV seleccionado mediante un Uri,
     * insertándolos en la base de datos y actualizando los logros y la gamificación.
     */
    public static int importExpenses(Context context, Uri uri) {
        List<Expense> expenses = new ArrayList<>();
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            boolean isHeader = true;
            AppDatabase db = AppDatabase.getInstance(context);

            // Asegurar datos iniciales (perfil de usuario y categorías) para evitar fallos de clave foránea
            AppDatabase.ensureInitialDataSync(context);

            long userId;
            UserProfile profile = db.userProfileDao().getUserProfileSync();
            if (profile != null) {
                userId = profile.getId();
            } else {
                profile = new UserProfile();
                profile.setId(1L);
                db.userProfileDao().insertOrUpdate(profile);
                userId = profile.getId();
            }

            List<ExpenseCategory> categories = db.expenseCategoryDao().getCategories();
            if (categories == null || categories.isEmpty()) {
                AppDatabase.insertInitialCategories();
                categories = db.expenseCategoryDao().getCategories();
            }
            long defaultCategoryId = (categories != null && !categories.isEmpty()) ? categories.get(0).getId() : 1L;

            String line;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    String lower = line.toLowerCase();
                    if (lower.contains("amount") || lower.contains("tipo")) {
                        continue;
                    }
                }

                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 5) {
                    try {
                        String type = parts[0].trim();
                        double amount = Double.parseDouble(parts[1].trim());
                        long categoryId = Long.parseLong(parts[2].trim());
                        String description = parts[3].trim().replace("\"", "");
                        String date = parts[4].trim();
                        String time = parts.length > 5 && !parts[5].trim().isEmpty() ? parts[5].trim() : "12:00";

                        // Validar si la categoría existe en la base de datos
                        ExpenseCategory cat = db.expenseCategoryDao().getCategoryById(categoryId);
                        if (cat == null) {
                            categoryId = defaultCategoryId;
                        }

                        Expense expense = new Expense(type, amount, (int)categoryId, description, date);
                        expense.setTime(time);
                        expense.setUserId(userId);
                        expenses.add(expense);
                    } catch (Exception e) {
                        Log.w(TAG, "Skipping malformed CSV line: " + line, e);
                    }
                }
            }

            if (!expenses.isEmpty()) {
                db.runInTransaction(() -> {
                    SupportSQLiteDatabase sqliteDb = db.getOpenHelper().getWritableDatabase();
                    sqliteDb.execSQL("PRAGMA foreign_keys = OFF;");
                    try {
                        db.expenseDao().insertListOfExpenses(expenses);
                    } finally {
                        sqliteDb.execSQL("PRAGMA foreign_keys = ON;");
                    }
                });

                try {
                    AchievementController achievementController = new AchievementController(context);
                    achievementController.checkAndUnlockAchievements();
                } catch (Exception e) {
                    Log.e(TAG, "Error updating achievements after import", e);
                }
            }
            return expenses.size();
        } catch (Exception ex) {
            Log.e(TAG, "Error importing expenses from CSV", ex);
            return -1;
        }
    }
}
