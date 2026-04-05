package asunfdiez.com.equilibra.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Challenge;
import asunfdiez.com.equilibra.models.DailyAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DatabaseHelper - Gestor de base de datos SQLite
 * Singleton pattern para evitar múltiples instancias
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Singleton instance
    private static DatabaseHelper instance;
    private final Context mContext;

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

    // SQL para crear tabla de categorías
    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + Constants.TABLE_CATEGORIES + " (" +
                    Constants.COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_CATEGORY_NAME + " TEXT NOT NULL UNIQUE, " +
                    Constants.COLUMN_CATEGORY_ICON + " TEXT, " +
                    Constants.COLUMN_CATEGORY_COLOR + " TEXT, " +
                    "\"" + Constants.COLUMN_CATEGORY_DEFAULT + "\" INTEGER DEFAULT 0, " +
                    Constants.COLUMN_CATEGORY_CREATED_AT + " INTEGER" +
                    ")";

    // SQL para crear tabla de objetivos de ahorro
    private static final String CREATE_TABLE_SAVING_GOALS =
            "CREATE TABLE " + Constants.TABLE_SAVING_GOALS + " (" +
                    Constants.COLUMN_SAVING_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_SAVING_GOAL_USER_ID + " INTEGER NOT NULL, " +
                    Constants.COLUMN_SAVING_GOAL_NAME + " TEXT NOT NULL, " +
                    Constants.COLUMN_SAVING_GOAL_TARGET_AMOUNT + " REAL NOT NULL, " +
                    Constants.COLUMN_SAVING_GOAL_CURRENT_AMOUNT + " REAL DEFAULT 0, " +
                    Constants.COLUMN_SAVING_GOAL_DEADLINE + " TEXT NOT NULL, " +
                    Constants.COLUMN_SAVING_GOAL_COMPLETED + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_SAVING_GOAL_CREATED_AT + " INTEGER, " +
                    Constants.COLUMN_SAVING_GOAL_UPDATED_AT + " INTEGER" +
                    ")";

    // SQL para crear tabla de experiencia del usuario
    private static final String CREATE_TABLE_USER_XP =
            "CREATE TABLE " + Constants.TABLE_USER_XP + " (" +
                    Constants.COLUMN_USER_XP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_USER_XP_USER_ID + " INTEGER NOT NULL UNIQUE, " +
                    Constants.COLUMN_USER_XP_TOTAL_XP + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_USER_XP_CURRENT_LEVEL + " INTEGER DEFAULT 1, " +
                    Constants.COLUMN_USER_XP_XP_FOR_NEXT_LEVEL + " INTEGER DEFAULT 100, " +
                    Constants.COLUMN_USER_XP_ACHIEVEMENTS_UNLOCKED + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_USER_XP_LAST_XP_EARNED_AT + " INTEGER" +
                    ")";

    // SQL para crear tabla de racha
    private static final String CREATE_TABLE_STREAK =
            "CREATE TABLE " + Constants.TABLE_STREAK + " (" +
                    Constants.COLUMN_STREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_STREAK_USER_ID + " INTEGER NOT NULL UNIQUE, " +
                    Constants.COLUMN_STREAK_CONSECUTIVE_DAYS + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_STREAK_START_DATE + " TEXT, " +
                    Constants.COLUMN_STREAK_LAST_ACTIVITY_DATE + " TEXT, " +
                    Constants.COLUMN_STREAK_MAX_RECORD + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_STREAK_IS_ACTIVE + " INTEGER DEFAULT 0" +
                    ")";

    // SQL para crear tabla de desafíos
    private static final String CREATE_TABLE_CHALLENGES =
            "CREATE TABLE " + Constants.TABLE_CHALLENGES + " (" +
                    Constants.COLUMN_CHALLENGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_CHALLENGE_USER_ID + " INTEGER NOT NULL, " +
                    Constants.COLUMN_CHALLENGE_TITLE + " TEXT NOT NULL, " +
                    Constants.COLUMN_CHALLENGE_DESCRIPTION + " TEXT, " +
                    Constants.COLUMN_CHALLENGE_CATEGORY + " TEXT, " +
                    Constants.COLUMN_CHALLENGE_TARGET_AMOUNT + " REAL NOT NULL, " +
                    Constants.COLUMN_CHALLENGE_CURRENT_AMOUNT + " REAL DEFAULT 0, " +
                    Constants.COLUMN_CHALLENGE_START_DATE + " TEXT NOT NULL, " +
                    Constants.COLUMN_CHALLENGE_END_DATE + " TEXT NOT NULL, " +
                    Constants.COLUMN_CHALLENGE_COMPLETED + " INTEGER DEFAULT 0, " +
                    Constants.COLUMN_CHALLENGE_XP_REWARD + " INTEGER DEFAULT 0" +
                    ")";

    // SQL para crear tabla de registro de intervenciones
    private static final String CREATE_TABLE_INTERVENTION_LOG =
            "CREATE TABLE " + Constants.TABLE_INTERVENTION_LOG + " (" +
                    Constants.COLUMN_INTERVENTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_INTERVENTION_USER_ID + " INTEGER NOT NULL, " +
                    Constants.COLUMN_INTERVENTION_EXPENSE_ID + " INTEGER NOT NULL, " +
                    Constants.COLUMN_INTERVENTION_TYPE + " TEXT NOT NULL, " +
                    Constants.COLUMN_INTERVENTION_MESSAGE + " TEXT, " +
                    Constants.COLUMN_INTERVENTION_IMPACT_ON_GOAL + " TEXT, " +
                    Constants.COLUMN_INTERVENTION_ALTERNATIVE_SUGGESTION + " TEXT, " +
                    Constants.COLUMN_INTERVENTION_TIMESTAMP + " INTEGER" +
                    ")";

    // SQL para crear tabla de análisis diario
    private static final String CREATE_TABLE_DAILY_ANALYSIS =
            "CREATE TABLE " + Constants.TABLE_DAILY_ANALYSIS + " (" +
                    Constants.COLUMN_DAILY_ANALYSIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COLUMN_DAILY_ANALYSIS_USER_ID + " INTEGER NOT NULL, " +
                    Constants.COLUMN_DAILY_ANALYSIS_DATE + " TEXT NOT NULL, " +
                    Constants.COLUMN_DAILY_ANALYSIS_TOTAL_SPENT + " REAL DEFAULT 0, " +
                    Constants.COLUMN_DAILY_ANALYSIS_TOTAL_INCOME + " REAL DEFAULT 0, " +
                    Constants.COLUMN_DAILY_ANALYSIS_NET_BALANCE + " REAL DEFAULT 0, " +
                    Constants.COLUMN_DAILY_ANALYSIS_CATEGORY_BREAKDOWN + " TEXT, " +
                    Constants.COLUMN_DAILY_ANALYSIS_ANOMALY_DETECTED + " INTEGER DEFAULT 0, " +
                    "UNIQUE(" + Constants.COLUMN_DAILY_ANALYSIS_USER_ID + ", " +
                    Constants.COLUMN_DAILY_ANALYSIS_DATE + ")" +
                    ")";

    private DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.mContext = context.getApplicationContext();
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
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_SAVING_GOALS);
        db.execSQL(CREATE_TABLE_USER_XP);
        db.execSQL(CREATE_TABLE_STREAK);
        db.execSQL(CREATE_TABLE_CHALLENGES);
        db.execSQL(CREATE_TABLE_INTERVENTION_LOG);
        db.execSQL(CREATE_TABLE_DAILY_ANALYSIS);

        // Insertar datos iniciales
        insertInitialAchievements(db);
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Por ahora, simplemente eliminamos y recreamos las tablas
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SAVING_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_USER_XP);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_STREAK);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CHALLENGES);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_INTERVENTION_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_DAILY_ANALYSIS);
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
     * Inserta las categorías predeterminadas
     */
    private void insertDefaultCategories(SQLiteDatabase db) {
        String insertCategory = "INSERT INTO " + Constants.TABLE_CATEGORIES +
                " (" + Constants.COLUMN_CATEGORY_NAME + ", " +
                Constants.COLUMN_CATEGORY_ICON + ", " +
                Constants.COLUMN_CATEGORY_COLOR + ", " +
                "\"" + Constants.COLUMN_CATEGORY_DEFAULT + "\") VALUES ";

        // Categorías predefinidas obtenidas de strings.xml con nombres de drawable correctos y colores
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_food).replace("'", "''") + "', 'ic_cat_food', '#2ECC71', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_transport).replace("'", "''") + "', 'ic_cat_transport', '#3498DB', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_home).replace("'", "''") + "', 'ic_nav_home', '#1ABC9C', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_health).replace("'", "''") + "', 'ic_cat_heart', '#E74C3C', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_leisure).replace("'", "''") + "', 'ic_cat_gamepad_2', '#E67E22', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_education).replace("'", "''") + "', 'ic_cat_graduation_cap', '#9B59B6', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_care).replace("'", "''") + "', 'ic_cat_gift', '#F1C40F', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_clothes).replace("'", "''") + "', 'ic_cat_shirt', '#27AE60', 1)");
        db.execSQL(insertCategory + "('" + mContext.getString(R.string.category_others).replace("'", "''") + "', 'ic_cat_package', '#34495E', 1)");
    }


    /**
     * Limpia todas las tablas (útil para testing)
     */
    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.TABLE_EXPENSES);
        db.execSQL("DELETE FROM " + Constants.TABLE_BUDGETS);
        db.execSQL("DELETE FROM " + Constants.TABLE_ACHIEVEMENTS);
        db.execSQL("DELETE FROM " + Constants.TABLE_CATEGORIES);
        db.execSQL("DELETE FROM " + Constants.TABLE_SAVING_GOALS);
        db.execSQL("DELETE FROM " + Constants.TABLE_USER_XP);
        db.execSQL("DELETE FROM " + Constants.TABLE_STREAK);
        db.execSQL("DELETE FROM " + Constants.TABLE_CHALLENGES);
        db.execSQL("DELETE FROM " + Constants.TABLE_INTERVENTION_LOG);
        db.execSQL("DELETE FROM " + Constants.TABLE_DAILY_ANALYSIS);
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
        values.put(Constants.COLUMN_EXPENSE_IS_RECURRING, expense.isRecurring() ? 1 : 0);
        values.put(Constants.COLUMN_EXPENSE_PATTERN_COUNT, expense.getPatternCount());

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

        String selectQuery = "SELECT " + Constants.COLUMN_EXPENSE_CATEGORY + ", SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") as total FROM "+ Constants.TABLE_EXPENSES  +
                " WHERE strftime('%Y-%m', " + Constants.COLUMN_EXPENSE_TIMESTAMP + "/1000, 'unixepoch') = strftime('%Y-%m', 'now') " +
                "GROUP BY " + Constants.COLUMN_EXPENSE_CATEGORY + " ORDER BY total DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int categoryIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_EXPENSE_CATEGORY);
            topCategory = cursor.getString(categoryIndex);
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

        String selectQuery = "SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") as total FROM "+ Constants.TABLE_EXPENSES  +
                " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = ? AND " +
                "strftime('%Y-%m', " + Constants.COLUMN_EXPENSE_TIMESTAMP + "/1000, 'unixepoch') = strftime('%Y-%m', 'now')";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{category});
        if (cursor.moveToFirst()) {
            int totalIndex = cursor.getColumnIndexOrThrow("total");
            total = cursor.getDouble(totalIndex);
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

    // ==================== SAVING GOALS ====================

    /**
     * Inserta un nuevo objetivo de ahorro
     */
    public long insertSavingGoal(SavingGoal savingGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_SAVING_GOAL_USER_ID, savingGoal.getUserId());
        values.put(Constants.COLUMN_SAVING_GOAL_NAME, savingGoal.getName());
        values.put(Constants.COLUMN_SAVING_GOAL_TARGET_AMOUNT, savingGoal.getTargetAmount());
        values.put(Constants.COLUMN_SAVING_GOAL_CURRENT_AMOUNT, savingGoal.getCurrentAmount());
        values.put(Constants.COLUMN_SAVING_GOAL_DEADLINE, savingGoal.getDeadline());
        values.put(Constants.COLUMN_SAVING_GOAL_COMPLETED, savingGoal.isCompleted() ? 1 : 0);
        values.put(Constants.COLUMN_SAVING_GOAL_CREATED_AT, savingGoal.getCreatedAt());
        values.put(Constants.COLUMN_SAVING_GOAL_UPDATED_AT, savingGoal.getUpdatedAt());

        return db.insert(Constants.TABLE_SAVING_GOALS, null, values);
    }

    /**
     * Obtiene todos los objetivos de ahorro de un usuario
     */
    public List<SavingGoal> getSavingGoalsByUser(long userId) {
        List<SavingGoal> goals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_SAVING_GOALS, null,
                Constants.COLUMN_SAVING_GOAL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                Constants.COLUMN_SAVING_GOAL_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                goals.add(cursorToSavingGoal(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return goals;
    }

    /**
     * Actualiza un objetivo de ahorro
     */
    public int updateSavingGoal(SavingGoal savingGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_SAVING_GOAL_NAME, savingGoal.getName());
        values.put(Constants.COLUMN_SAVING_GOAL_TARGET_AMOUNT, savingGoal.getTargetAmount());
        values.put(Constants.COLUMN_SAVING_GOAL_CURRENT_AMOUNT, savingGoal.getCurrentAmount());
        values.put(Constants.COLUMN_SAVING_GOAL_DEADLINE, savingGoal.getDeadline());
        values.put(Constants.COLUMN_SAVING_GOAL_COMPLETED, savingGoal.isCompleted() ? 1 : 0);
        values.put(Constants.COLUMN_SAVING_GOAL_UPDATED_AT, System.currentTimeMillis());

        return db.update(Constants.TABLE_SAVING_GOALS, values,
                Constants.COLUMN_SAVING_GOAL_ID + "=?",
                new String[]{String.valueOf(savingGoal.getId())});
    }

    /**
     * Elimina un objetivo de ahorro
     */
    public int deleteSavingGoal(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.TABLE_SAVING_GOALS,
                Constants.COLUMN_SAVING_GOAL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    private SavingGoal cursorToSavingGoal(Cursor cursor) {
        SavingGoal goal = new SavingGoal();
        goal.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_ID)));
        goal.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_USER_ID)));
        goal.setName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_NAME)));
        goal.setTargetAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_TARGET_AMOUNT)));
        goal.setCurrentAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_CURRENT_AMOUNT)));
        goal.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_DEADLINE)));
        goal.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_COMPLETED)) == 1);
        goal.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_CREATED_AT)));
        goal.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_SAVING_GOAL_UPDATED_AT)));
        return goal;
    }

    // ==================== USER XP ====================

    /**
     * Inserta/crea el registro de XP para un usuario
     */
    public long insertUserXP(UserXP userXP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_USER_XP_USER_ID, userXP.getUserId());
        values.put(Constants.COLUMN_USER_XP_TOTAL_XP, userXP.getTotalXP());
        values.put(Constants.COLUMN_USER_XP_CURRENT_LEVEL, userXP.getCurrentLevel());
        values.put(Constants.COLUMN_USER_XP_XP_FOR_NEXT_LEVEL, userXP.getXpForNextLevel());
        values.put(Constants.COLUMN_USER_XP_ACHIEVEMENTS_UNLOCKED, userXP.getAchievementsUnlocked());
        values.put(Constants.COLUMN_USER_XP_LAST_XP_EARNED_AT, userXP.getLastXPEarnedAt());

        return db.insert(Constants.TABLE_USER_XP, null, values);
    }

    /**
     * Obtiene el registro de XP de un usuario
     */
    public UserXP getUserXPByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_USER_XP, null,
                Constants.COLUMN_USER_XP_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            UserXP userXP = cursorToUserXP(cursor);
            cursor.close();
            return userXP;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    /**
     * Actualiza el XP de un usuario
     */
    public int updateUserXP(UserXP userXP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_USER_XP_TOTAL_XP, userXP.getTotalXP());
        values.put(Constants.COLUMN_USER_XP_CURRENT_LEVEL, userXP.getCurrentLevel());
        values.put(Constants.COLUMN_USER_XP_XP_FOR_NEXT_LEVEL, userXP.getXpForNextLevel());
        values.put(Constants.COLUMN_USER_XP_ACHIEVEMENTS_UNLOCKED, userXP.getAchievementsUnlocked());
        values.put(Constants.COLUMN_USER_XP_LAST_XP_EARNED_AT, userXP.getLastXPEarnedAt());

        return db.update(Constants.TABLE_USER_XP, values,
                Constants.COLUMN_USER_XP_USER_ID + "=?",
                new String[]{String.valueOf(userXP.getUserId())});
    }

    private UserXP cursorToUserXP(Cursor cursor) {
        UserXP userXP = new UserXP();
        userXP.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_ID)));
        userXP.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_USER_ID)));
        userXP.setTotalXP(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_TOTAL_XP)));
        userXP.setCurrentLevel(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_CURRENT_LEVEL)));
        userXP.setXpForNextLevel(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_XP_FOR_NEXT_LEVEL)));
        userXP.setAchievementsUnlocked(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_ACHIEVEMENTS_UNLOCKED)));
        userXP.setLastXPEarnedAt(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_USER_XP_LAST_XP_EARNED_AT)));
        return userXP;
    }

    // ==================== STREAK ====================

    /**
     * Inserta/crea el registro de racha para un usuario
     */
    public long insertStreak(Streak streak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_STREAK_USER_ID, streak.getUserId());
        values.put(Constants.COLUMN_STREAK_CONSECUTIVE_DAYS, streak.getConsecutiveDays());
        values.put(Constants.COLUMN_STREAK_START_DATE, streak.getStartDate());
        values.put(Constants.COLUMN_STREAK_LAST_ACTIVITY_DATE, streak.getLastActivityDate());
        values.put(Constants.COLUMN_STREAK_MAX_RECORD, streak.getMaxStreakRecord());
        values.put(Constants.COLUMN_STREAK_IS_ACTIVE, streak.isActive() ? 1 : 0);

        return db.insert(Constants.TABLE_STREAK, null, values);
    }

    /**
     * Obtiene la racha de un usuario
     */
    public Streak getStreakByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_STREAK, null,
                Constants.COLUMN_STREAK_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Streak streak = cursorToStreak(cursor);
            cursor.close();
            return streak;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    /**
     * Actualiza la racha de un usuario
     */
    public int updateStreak(Streak streak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_STREAK_CONSECUTIVE_DAYS, streak.getConsecutiveDays());
        values.put(Constants.COLUMN_STREAK_START_DATE, streak.getStartDate());
        values.put(Constants.COLUMN_STREAK_LAST_ACTIVITY_DATE, streak.getLastActivityDate());
        values.put(Constants.COLUMN_STREAK_MAX_RECORD, streak.getMaxStreakRecord());
        values.put(Constants.COLUMN_STREAK_IS_ACTIVE, streak.isActive() ? 1 : 0);

        return db.update(Constants.TABLE_STREAK, values,
                Constants.COLUMN_STREAK_USER_ID + "=?",
                new String[]{String.valueOf(streak.getUserId())});
    }

    private Streak cursorToStreak(Cursor cursor) {
        Streak streak = new Streak();
        streak.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_ID)));
        streak.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_USER_ID)));
        streak.setConsecutiveDays(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_CONSECUTIVE_DAYS)));
        streak.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_START_DATE)));
        streak.setLastActivityDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_LAST_ACTIVITY_DATE)));
        streak.setMaxStreakRecord(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_MAX_RECORD)));
        streak.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_STREAK_IS_ACTIVE)) == 1);
        return streak;
    }

    // ==================== CHALLENGES ====================

    /**
     * Inserta un nuevo desafío
     */
    public long insertChallenge(Challenge challenge) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_CHALLENGE_USER_ID, challenge.getUserId());
        values.put(Constants.COLUMN_CHALLENGE_TITLE, challenge.getTitle());
        values.put(Constants.COLUMN_CHALLENGE_DESCRIPTION, challenge.getDescription());
        values.put(Constants.COLUMN_CHALLENGE_CATEGORY, challenge.getCategory());
        values.put(Constants.COLUMN_CHALLENGE_TARGET_AMOUNT, challenge.getTargetAmount());
        values.put(Constants.COLUMN_CHALLENGE_CURRENT_AMOUNT, challenge.getCurrentAmount());
        values.put(Constants.COLUMN_CHALLENGE_START_DATE, challenge.getStartDate());
        values.put(Constants.COLUMN_CHALLENGE_END_DATE, challenge.getEndDate());
        values.put(Constants.COLUMN_CHALLENGE_COMPLETED, challenge.isCompleted() ? 1 : 0);
        values.put(Constants.COLUMN_CHALLENGE_XP_REWARD, challenge.getXpReward());

        return db.insert(Constants.TABLE_CHALLENGES, null, values);
    }

    /**
     * Obtiene todos los desafíos activos de un usuario
     */
    public List<Challenge> getActiveChallengesByUser(long userId) {
        List<Challenge> challenges = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_CHALLENGES, null,
                Constants.COLUMN_CHALLENGE_USER_ID + "=? AND " + Constants.COLUMN_CHALLENGE_COMPLETED + "=0",
                new String[]{String.valueOf(userId)},
                null, null,
                Constants.COLUMN_CHALLENGE_START_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                challenges.add(cursorToChallenge(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return challenges;
    }

    /**
     * Actualiza un desafío
     */
    public int updateChallenge(Challenge challenge) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_CHALLENGE_TITLE, challenge.getTitle());
        values.put(Constants.COLUMN_CHALLENGE_DESCRIPTION, challenge.getDescription());
        values.put(Constants.COLUMN_CHALLENGE_CATEGORY, challenge.getCategory());
        values.put(Constants.COLUMN_CHALLENGE_TARGET_AMOUNT, challenge.getTargetAmount());
        values.put(Constants.COLUMN_CHALLENGE_CURRENT_AMOUNT, challenge.getCurrentAmount());
        values.put(Constants.COLUMN_CHALLENGE_COMPLETED, challenge.isCompleted() ? 1 : 0);
        values.put(Constants.COLUMN_CHALLENGE_XP_REWARD, challenge.getXpReward());

        return db.update(Constants.TABLE_CHALLENGES, values,
                Constants.COLUMN_CHALLENGE_ID + "=?",
                new String[]{String.valueOf(challenge.getId())});
    }

    private Challenge cursorToChallenge(Cursor cursor) {
        Challenge challenge = new Challenge();
        challenge.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_ID)));
        challenge.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_USER_ID)));
        challenge.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_TITLE)));
        challenge.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_DESCRIPTION)));
        challenge.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_CATEGORY)));
        challenge.setTargetAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_TARGET_AMOUNT)));
        challenge.setCurrentAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_CURRENT_AMOUNT)));
        challenge.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_START_DATE)));
        challenge.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_END_DATE)));
        challenge.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_COMPLETED)) == 1);
        challenge.setXpReward(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_CHALLENGE_XP_REWARD)));
        return challenge;
    }

    // ==================== INTERVENTION LOG ====================

    /**
     * Inserta un nuevo registro de intervención
     */
    public long insertInterventionLog(InterventionLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_INTERVENTION_USER_ID, log.getUserId());
        values.put(Constants.COLUMN_INTERVENTION_EXPENSE_ID, log.getExpenseId());
        values.put(Constants.COLUMN_INTERVENTION_TYPE, log.getInterventionType());
        values.put(Constants.COLUMN_INTERVENTION_MESSAGE, log.getMessage());
        values.put(Constants.COLUMN_INTERVENTION_IMPACT_ON_GOAL, log.getImpactOnGoal());
        values.put(Constants.COLUMN_INTERVENTION_ALTERNATIVE_SUGGESTION, log.getAlternativeSuggestion());
        values.put(Constants.COLUMN_INTERVENTION_TIMESTAMP, log.getTimestamp());

        return db.insert(Constants.TABLE_INTERVENTION_LOG, null, values);
    }

    /**
     * Obtiene los registros de intervención recientes de un usuario
     */
    public List<InterventionLog> getRecentInterventions(long userId, int limit) {
        List<InterventionLog> interventions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_INTERVENTION_LOG, null,
                Constants.COLUMN_INTERVENTION_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                Constants.COLUMN_INTERVENTION_TIMESTAMP + " DESC LIMIT " + limit);

        if (cursor.moveToFirst()) {
            do {
                interventions.add(cursorToInterventionLog(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return interventions;
    }

    private InterventionLog cursorToInterventionLog(Cursor cursor) {
        InterventionLog log = new InterventionLog();
        log.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_ID)));
        log.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_USER_ID)));
        log.setExpenseId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_EXPENSE_ID)));
        log.setInterventionType(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_TYPE)));
        log.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_MESSAGE)));
        log.setImpactOnGoal(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_IMPACT_ON_GOAL)));
        log.setAlternativeSuggestion(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_ALTERNATIVE_SUGGESTION)));
        log.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_INTERVENTION_TIMESTAMP)));
        return log;
    }

    // ==================== DAILY ANALYSIS ====================

    /**
     * Inserta/actualiza un análisis diario
     */
    public long insertOrUpdateDailyAnalysis(DailyAnalysis analysis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_DAILY_ANALYSIS_USER_ID, analysis.getUserId());
        values.put(Constants.COLUMN_DAILY_ANALYSIS_DATE, analysis.getAnalysisDate());
        values.put(Constants.COLUMN_DAILY_ANALYSIS_TOTAL_SPENT, analysis.getTotalSpent());
        values.put(Constants.COLUMN_DAILY_ANALYSIS_TOTAL_INCOME, analysis.getTotalIncome());
        values.put(Constants.COLUMN_DAILY_ANALYSIS_NET_BALANCE, analysis.getNetBalance());
        values.put(Constants.COLUMN_DAILY_ANALYSIS_CATEGORY_BREAKDOWN, analysis.getCategoryBreakdown());
        values.put(Constants.COLUMN_DAILY_ANALYSIS_ANOMALY_DETECTED, analysis.isAnomalyDetected() ? 1 : 0);

        return db.insertWithOnConflict(Constants.TABLE_DAILY_ANALYSIS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Obtiene el análisis diario de una fecha específica
     */
    public DailyAnalysis getDailyAnalysis(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_DAILY_ANALYSIS, null,
                Constants.COLUMN_DAILY_ANALYSIS_USER_ID + "=? AND " +
                        Constants.COLUMN_DAILY_ANALYSIS_DATE + "=?",
                new String[]{String.valueOf(userId), date},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            DailyAnalysis analysis = cursorToDailyAnalysis(cursor);
            cursor.close();
            return analysis;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    /**
     * Obtiene los análisis de un rango de fechas
     */
    public List<DailyAnalysis> getDailyAnalysisByRange(long userId, String startDate, String endDate) {
        List<DailyAnalysis> analyses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_DAILY_ANALYSIS, null,
                Constants.COLUMN_DAILY_ANALYSIS_USER_ID + "=? AND " +
                        Constants.COLUMN_DAILY_ANALYSIS_DATE + " BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), startDate, endDate},
                null, null,
                Constants.COLUMN_DAILY_ANALYSIS_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                analyses.add(cursorToDailyAnalysis(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return analyses;
    }

    private DailyAnalysis cursorToDailyAnalysis(Cursor cursor) {
        DailyAnalysis analysis = new DailyAnalysis();
        analysis.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_ID)));
        analysis.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_USER_ID)));
        analysis.setAnalysisDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_DATE)));
        analysis.setTotalSpent(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_TOTAL_SPENT)));
        analysis.setTotalIncome(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_TOTAL_INCOME)));
        analysis.setNetBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_NET_BALANCE)));
        analysis.setCategoryBreakdown(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_CATEGORY_BREAKDOWN)));
        analysis.setAnomalyDetected(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COLUMN_DAILY_ANALYSIS_ANOMALY_DETECTED)) == 1);
        return analysis;
    }


    /**
     * Cuenta cuántas veces se ha gastado en una categoría esta semana
     */
    public int countCategoryExpensesThisWeek(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Fecha de hace 7 días
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long weekAgo = cal.getTimeInMillis();

        String query = "SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES +
                " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = ? AND " + Constants.COLUMN_EXPENSE_TIMESTAMP + " >= ?";

        Cursor cursor = db.rawQuery(query, new String[]{category, String.valueOf(weekAgo)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    /**
     * Obtiene el total gastado en una categoría esta semana
     */
    public double getCategoryExpensesThisWeek(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long weekAgo = cal.getTimeInMillis();

        String query = "SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
                " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = ? AND " + Constants.COLUMN_EXPENSE_TIMESTAMP + " >= ?";

        Cursor cursor = db.rawQuery(query, new String[]{category, String.valueOf(weekAgo)});

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();

        return total;
    }

    // ==================== CATEGORIES ====================

    /**
     * Obtiene todas las categorías de la base de datos
     */
    public List<ExpenseCategory> getAllCategories() {
        List<ExpenseCategory> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_CATEGORIES, null, null, null, null, null, Constants.COLUMN_CATEGORY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY_NAME);
                int iconIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY_ICON);
                int colorIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY_COLOR);
                
                String name = cursor.getString(nameIndex);
                String iconName = cursor.getString(iconIndex);
                String color = cursor.getString(colorIndex);
                
                int iconResId = mContext.getResources().getIdentifier(iconName, "drawable", mContext.getPackageName());
                if (iconResId == 0) iconResId = android.R.drawable.ic_menu_help;

                categories.add(new ExpenseCategory(name, iconResId, iconName, color));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    /**
     * Obtiene las categorías predeterminadas con sus iconos resueltos
     */
    public List<ExpenseCategory> getDefaultCategories(Context context) {
        List<ExpenseCategory> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_CATEGORIES, null, 
                "\"" + Constants.COLUMN_CATEGORY_DEFAULT + "\"=1", null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY_NAME);
                int iconIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY_ICON);
                int colorIndex = cursor.getColumnIndexOrThrow(Constants.COLUMN_CATEGORY_COLOR);
                
                String name = cursor.getString(nameIndex);
                String iconName = cursor.getString(iconIndex);
                String color = cursor.getString(colorIndex);
                
                int iconResId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
                if (iconResId == 0) iconResId = android.R.drawable.ic_menu_help; // Fallback

                categories.add(new ExpenseCategory(name, iconResId, iconName, color));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

}
