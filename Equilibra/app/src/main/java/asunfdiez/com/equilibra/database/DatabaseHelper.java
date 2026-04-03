package asunfdiez.com.equilibra.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DatabaseHelper - Gestor de base de datos SQLite
 * Almacena gastos, ingresos y datos financieros del usuario
 * Singleton pattern para evitar múltiples instancias
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Singleton instance
    private static DatabaseHelper instance;

    // SQL para crear tabla de gastos
    private static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE " + Constants.TABLE_EXPENSES + " (" +
                    Constants.COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_EXPENSE_TYPE + " TEXT NOT NULL, " +
                    Constants.COLUMN_EXPENSE_AMOUNT + " REAL NOT NULL, " +
                    Constants.COLUMN_EXPENSE_CATEGORY + " TEXT NOT NULL, " +
                    Constants.COLUMN_EXPENSE_DESCRIPTION + " TEXT, " +
                    Constants.COLUMN_EXPENSE_NOTES + " TEXT, " +
                    Constants.COLUMN_EXPENSE_DATE + " TEXT NOT NULL, " +
                    Constants.COLUMN_EXPENSE_TIMESTAMP + " INTEGER NOT NULL" +
                    ")";

    // SQL para crear tabla de presupuestos
    private static final String CREATE_TABLE_BUDGETS =
            "CREATE TABLE " + Constants.TABLE_BUDGETS + " (" +
                    Constants.COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_BUDGET_CATEGORY + " TEXT NOT NULL, " +
                    Constants.COLUMN_BUDGET_AMOUNT + " REAL NOT NULL, " +
                    Constants.COLUMN_BUDGET_PERIOD + " TEXT NOT NULL, " +
                    Constants.COLUMN_BUDGET_MONTH + " INTEGER, " +
                    Constants.COLUMN_BUDGET_YEAR + " INTEGER, " +
                    "UNIQUE(" + Constants.COLUMN_BUDGET_CATEGORY + ", " +
                    Constants.COLUMN_BUDGET_MONTH + ", " +
                    Constants.COLUMN_BUDGET_YEAR + ")" +
                    ")";

    // SQL para crear tabla de logros
    private static final String CREATE_TABLE_ACHIEVEMENTS =
            "CREATE TABLE " + Constants.TABLE_ACHIEVEMENTS + " (" +
                    Constants.COLUMN_ACHIEVEMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_ACHIEVEMENT_TITLE + " TEXT NOT NULL, " +
                    Constants.COLUMN_ACHIEVEMENT_DESCRIPTION + " TEXT, " +
                    Constants.COLUMN_ACHIEVEMENT_XP + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_ACHIEVEMENT_ICON + " TEXT, " +
                    Constants.COLUMN_ACHIEVEMENT_UNLOCKED + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_ACHIEVEMENT_DATE_UNLOCKED + " INTEGER" +
                    ")";

    private DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    /**
     * Obtiene la instancia única del DatabaseHelper (Singleton)
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear todas las tablas
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_BUDGETS);
        db.execSQL(CREATE_TABLE_ACHIEVEMENTS);

        // Insertar datos iniciales
        insertInitialAchievements(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Por ahora, simplemente eliminamos y recreamos las tablas
        // En producción, deberías hacer migraciones más sofisticadas
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ACHIEVEMENTS);
        onCreate(db);
    }

    /**
     * Inserta los logros iniciales en la base de datos
     */
    private void insertInitialAchievements(SQLiteDatabase db) {
        String insertAchievement = "INSERT INTO " + Constants.TABLE_ACHIEVEMENTS +
                " (" + Constants.COLUMN_ACHIEVEMENT_TITLE + ", " +
                Constants.COLUMN_ACHIEVEMENT_DESCRIPTION + ", " +
                Constants.COLUMN_ACHIEVEMENT_XP + ", " +
                Constants.COLUMN_ACHIEVEMENT_ICON + ") VALUES ";

        // Logros de inicio
        db.execSQL(insertAchievement + "('Primer Paso', 'Registra tu primer gasto', 10, 'ic_first_step')");
        db.execSQL(insertAchievement + "('Constante', 'Registra gastos durante 7 días seguidos', 25, 'ic_consistent')");
        db.execSQL(insertAchievement + "('Ahorrador', 'Mantén tus gastos bajo el presupuesto durante un mes', 50, 'ic_saver')");
        db.execSQL(insertAchievement + "('Disciplinado', 'Completa 30 días de seguimiento', 75, 'ic_disciplined')");
        db.execSQL(insertAchievement + "('Maestro Financiero', 'Alcanza 3 meses sin exceder tu presupuesto', 100, 'ic_master')");

        // Logros de categorías
        db.execSQL(insertAchievement + "('Gourmet Consciente', 'Reduce gastos en comida un 20%', 30, 'ic_food')");
        db.execSQL(insertAchievement + "('Movilidad Eficiente', 'Reduce gastos en transporte un 15%', 30, 'ic_transport')");
        db.execSQL(insertAchievement + "('Diversión Inteligente', 'Mantén entretenimiento bajo control', 30, 'ic_fun')");

        // Logros especiales
        db.execSQL(insertAchievement + "('Meta Alcanzada', 'Completa tu primera meta de ahorro', 100, 'ic_goal')");
        db.execSQL(insertAchievement + "('Experto', 'Alcanza el nivel Avanzado', 150, 'ic_expert')");
    }

    /**
     * Limpia todas las tablas (útil para testing)
     */
    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.TABLE_EXPENSES);
        db.execSQL("DELETE FROM " + Constants.TABLE_BUDGETS);
        db.execSQL("DELETE FROM " + Constants.TABLE_ACHIEVEMENTS);
        db.close();
    }

    /**
     * Elimina la base de datos completamente
     */
    public static void deleteDatabase(Context context) {
        context.deleteDatabase(Constants.DATABASE_NAME);
    }

    // ==================== MÉTODOS CREATE ====================

    /**
     * Inserta un nuevo gasto o ingreso
     */
    public long insertExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_EXPENSE_TYPE, expense.getType());
        values.put(Constants.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        values.put(Constants.COLUMN_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(Constants.COLUMN_EXPENSE_CATEGORY, expense.getCategory());
        values.put(Constants.COLUMN_EXPENSE_DATE, expense.getDate());
        values.put(Constants.COLUMN_EXPENSE_TIMESTAMP, expense.getTimestamp());
        values.put(Constants.COLUMN_EXPENSE_NOTES, expense.getNotes());

        long id = db.insert(Constants.TABLE_EXPENSES, null, values);
        return id;
    }

    // ==================== MÉTODOS READ ====================

    /**
     * Obtiene todos los gastos e ingresos
     */
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_EXPENSES +
                " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = cursorToExpense(cursor);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenses;
    }

    /**
     * Obtiene gastos recientes limitados
     */
    public List<Expense> getRecentExpenses(int limit) {
        List<Expense> expenses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_EXPENSES +
                " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC LIMIT " + limit;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = cursorToExpense(cursor);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenses;
    }

    /**
     * Obtiene un gasto por ID
     */
    public Expense getExpenseById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_EXPENSES, null,
                Constants.COLUMN_EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Expense expense = cursorToExpense(cursor);
            cursor.close();
            return expense;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    /**
     * Obtiene gastos del mes actual
     */
    public List<Expense> getExpensesThisMonth() {
        return getExpensesForMonth(0); // 0 = mes actual
    }

    /**
     * Obtiene gastos del mes anterior
     */
    public List<Expense> getExpensesLastMonth() {
        return getExpensesForMonth(-1); // -1 = mes anterior
    }

    /**
     * Obtiene gastos de un mes específico (offset desde el mes actual)
     */
    private List<Expense> getExpensesForMonth(int monthOffset) {
        List<Expense> expenses = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthOffset);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endTime = calendar.getTimeInMillis();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_EXPENSES +
                " WHERE " + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN ? AND ?" +
                " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{String.valueOf(startTime), String.valueOf(endTime)});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = cursorToExpense(cursor);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenses;
    }

    // ==================== MÉTODOS UPDATE ====================

    /**
     * Actualiza un gasto existente
     */
    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_EXPENSE_TYPE, expense.getType());
        values.put(Constants.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        values.put(Constants.COLUMN_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(Constants.COLUMN_EXPENSE_CATEGORY, expense.getCategory());
        values.put(Constants.COLUMN_EXPENSE_DATE, expense.getDate());
        values.put(Constants.COLUMN_EXPENSE_TIMESTAMP, expense.getTimestamp());
        values.put(Constants.COLUMN_EXPENSE_NOTES, expense.getNotes());

        return db.update(Constants.TABLE_EXPENSES, values,
                Constants.COLUMN_EXPENSE_ID + "=?",
                new String[]{String.valueOf(expense.getId())});
    }

    // ==================== MÉTODOS DELETE ====================

    /**
     * Elimina un gasto
     */
    public int deleteExpense(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.TABLE_EXPENSES,
                Constants.COLUMN_EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ==================== MÉTODOS DE ANÁLISIS ====================

    /**
     * Calcula el total de ingresos del mes actual
     */
    public double getTotalIncomeThisMonth() {
        return getTotalForTypeAndMonth(Constants.TYPE_INCOME, 0);
    }

    /**
     * Calcula el total de gastos del mes actual
     */
    public double getTotalExpensesThisMonth() {
        return getTotalForTypeAndMonth(Constants.TYPE_EXPENSE, 0);
    }

    /**
     * Calcula el total de gastos del mes anterior
     */
    public double getTotalExpensesLastMonth() {
        return getTotalForTypeAndMonth(Constants.TYPE_EXPENSE, -1);
    }

    /**
     * Calcula el total para un tipo y mes específico
     */
    private double getTotalForTypeAndMonth(String type, int monthOffset) {
        double total = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthOffset);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endTime = calendar.getTimeInMillis();

        String selectQuery = "SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") as total FROM " + Constants.TABLE_EXPENSES +
                " WHERE " + Constants.COLUMN_EXPENSE_TYPE + "=? AND " +
                Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN ? AND ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{type, String.valueOf(startTime), String.valueOf(endTime)});

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }

    /**
     * Obtiene análisis por categoría (solo gastos)
     */
    public List<CategoryAnalysis> getCategoryAnalysis() {
        List<CategoryAnalysis> analyses = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        String selectQuery = "SELECT " + Constants.COLUMN_EXPENSE_CATEGORY + ", " +
                "SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") as total, " +
                "COUNT(*) as count " +
                "FROM " + Constants.TABLE_EXPENSES +
                " WHERE " + Constants.COLUMN_EXPENSE_TYPE + "=? AND " +
                Constants.COLUMN_EXPENSE_TIMESTAMP + " >= ? " +
                "GROUP BY " + Constants.COLUMN_EXPENSE_CATEGORY +
                " ORDER BY total DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{Constants.TYPE_EXPENSE, String.valueOf(startTime)});

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                double amount = cursor.getDouble(1);
                int count = cursor.getInt(2);

                CategoryAnalysis analysis = new CategoryAnalysis(category, amount, count);
                analyses.add(analysis);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return analyses;
    }

    /**
     * Calcula el promedio de gasto diario del mes actual
     */
    public double getAverageDailyExpense() {
        double totalExpenses = getTotalExpensesThisMonth();

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentDay == 0) currentDay = 1;

        return totalExpenses / currentDay;
    }

    /**
     * Obtiene el total de gastos de los últimos N días
     */
    public double getExpensesLastNDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        String selectQuery = "SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") as total FROM " + Constants.TABLE_EXPENSES +
                " WHERE " + Constants.COLUMN_EXPENSE_TYPE + "=? AND " +
                Constants.COLUMN_EXPENSE_TIMESTAMP + " >= ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{Constants.TYPE_EXPENSE, String.valueOf(startTime)});

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }

    /**
     * Obtiene el número total de gastos
     */
    public int getExpenseCount() {
        String selectQuery = "SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    /**
     * Obtiene la categoría con más gastos este mes
     */
    public String getTopExpenseCategory() {

        String topCategory = "";

        String selectQuery = "SELECT category, SUM(amount) as total FROM "+ Constants.TABLE_EXPENSES  +
                " WHERE strftime('%Y-%m', date/1000, 'unixepoch') = strftime('%Y-%m', 'now') " +
                "GROUP BY category ORDER BY total DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            topCategory = cursor.getString(cursor.getColumnIndex("category"));
        }
        cursor.close();

        return topCategory;
    }

    /**
     * Obtiene el total de gastos de una categoría este mes
     */
    public double getCategoryExpensesThisMonth(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        String selectQuery = "SELECT SUM(amount) as total FROM "+ Constants.TABLE_EXPENSES  +
                " WHERE category = ? AND " +
                "strftime('%Y-%m', date/1000, 'unixepoch') = strftime('%Y-%m', 'now')";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{category});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("total"));
        }
        cursor.close();

        return total;
    }

    // ==================== UTILIDADES ====================

    /**
     * Convierte un cursor a un objeto Expense
     */
    private Expense cursorToExpense(Cursor cursor) {
        Expense expense = new Expense();
        expense.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_ID)));
        expense.setType(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_TYPE)));
        expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_AMOUNT)));
        expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_DESCRIPTION)));
        expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_CATEGORY)));
        expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_DATE)));
        expense.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_TIMESTAMP)));
        expense.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_NOTES)));
        return expense;
    }


}