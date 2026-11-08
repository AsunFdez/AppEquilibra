package asunfdiez.com.equilibra.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.AchievementController;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Alternative;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.models.XPAuditLog;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * Clase principal de la base de datos de la aplicación utilizando Room.
 * 
 * <p>Define todas las entidades que componen el esquema de persistencia local y 
 * proporciona los métodos abstractos para acceder a sus respectivos DAO. Implementa
 * el patrón Singleton para garantizar una única instancia de la base de datos y 
 * gestiona la inicialización de datos de configuración (categorías, logros, alternativas, rachas) en la
 * primera ejecución.</p>
 */
@Database(
        entities = { Achievement.class, Alternative.class,
                    Budget.class,
                    Expense.class, ExpenseCategory.class,
                    InterventionLog.class,
                    SavingGoal.class, Streak.class,
                    UserProfile.class, UserXP.class, XPAuditLog.class },
        version = Constants.DATABASE_VERSION,
        exportSchema = true
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS xp_audit_log (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "userId INTEGER NOT NULL, " +
                            "xpAmount INTEGER NOT NULL, " +
                            "description TEXT, " +
                            "timestamp INTEGER NOT NULL, " +
                            "FOREIGN KEY(userId) REFERENCES user_profile(id) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX index_xp_audit_log_userId ON xp_audit_log(userId)");
        }
    };


    // Instancia única de la base de datos (Patrón Singleton)
    private static volatile AppDatabase INSTANCE;
    private static Context mContext;
    private static final ExecutorService INITIALIZATION_EXECUTOR = Executors.newSingleThreadExecutor();

    // Métodos de acceso a los Objetos de Acceso a Datos (DAO)
    public abstract UserProfileDao userProfileDao();
    public abstract SavingGoalDao savingGoalDao();
    public abstract ExpenseDao expenseDao();
    public abstract ExpenseCategoryDao expenseCategoryDao();
    public abstract AchievementDao achievementDao();
    public abstract AlternativeDao alternativeDao();
    public abstract StreakDao streakDao();
    public abstract UserXPDao userXPDao();

    public abstract BudgetDao budgetDao();

    public abstract InterventionLogDao interventionLogDao();

    public abstract XPAuditLogDao xpAuditLogDao();


    /**
     * Obtiene la instancia única de la base de datos
     */
    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    mContext = context.getApplicationContext();
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, Constants.DATABASE_NAME)
                            .addMigrations(MIGRATION_14_15)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                    ensureInitialDataSync(context);
                }
            }
        }
        return INSTANCE;
    }

    public static void ensureInitialDataSync(Context context) {
        try {
            if (INSTANCE == null) return;
            Context ctx = context.getApplicationContext();
            mContext = ctx;

            // Drop legacy tables if they exist
            INSTANCE.query("DROP TABLE IF EXISTS daily_analysis", null).close();
            INSTANCE.query("DROP TABLE IF EXISTS challenges", null).close();

            // 1. User Profile
            UserProfile profile = INSTANCE.userProfileDao().getUserProfileSync();
            if (profile == null) {
                profile = new UserProfile();
                profile.setId(1);
                INSTANCE.userProfileDao().insertOrUpdate(profile);
                Log.d("AppDatabase", "Initial UserProfile created synchronously");
            }

            // 2. User XP
            UserXP userXP = INSTANCE.userXPDao().getUserXPByUserId(profile.getId());
            if (userXP == null) {
                insertInitialUserXP();
                Log.d("AppDatabase", "Initial UserXP created synchronously");
            }

            // 3. Categories
            if (INSTANCE.expenseCategoryDao().getCategories().isEmpty()) {
                insertInitialCategories();
                Log.d("AppDatabase", "Initial Categories inserted synchronously");
            }

            // 4. Achievements
            if (INSTANCE.achievementDao().getAchievementCountSync() == 0) {
                insertInitialAchievements();
                Log.d("AppDatabase", "Initial Achievements inserted synchronously");
            }

            // 5. Alternatives
            if (INSTANCE.alternativeDao().getAllAlternatives().isEmpty()) {
                insertInitialAlternatives();
                Log.d("AppDatabase", "Initial Alternatives inserted synchronously");
            }

            // 6. Streaks
            UserProfile currentUser = INSTANCE.userProfileDao().getUserProfileSync();
            if (currentUser != null && INSTANCE.streakDao().getAllStreaks().isEmpty()) {
                insertInitialStreaks();
                Log.d("AppDatabase", "Initial Streaks inserted synchronously");
            }
        } catch (Exception e) {
            Log.e("AppDatabase", "Error in ensureInitialDataSync: " + e.getMessage(), e);
        }
    }


    /**
     * Borrar la base de datos
     */
    private static void deleteDatabase() {
        if (INSTANCE != null) {
            INSTANCE.clearAllTables();
            INSTANCE.close();
            INSTANCE = null;
        }
    }

    /**
     * Inserta las categorías por defecto en la base de datos
     */
    public static void insertInitialCategories() {
        ExpenseCategoryDao dao = INSTANCE.expenseCategoryDao();
        List<ExpenseCategory> categories = new ArrayList<>();

        categories.add(new ExpenseCategory(mContext.getString(R.string.category_food), R.drawable.ic_cat_utensils, "ic_cat_food", "#2ECC71"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_transport), R.drawable.ic_cat_car, "ic_cat_transport", "#3498DB"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_home), R.drawable.ic_nav_home, "ic_nav_home", "#1ABC9C"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_health), R.drawable.ic_cat_heart, "'ic_cat_heart", "#E74C3C"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_leisure), R.drawable.ic_cat_gamepad_2, "ic_cat_gamepad_2", "#E67E22"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_subscriptions), R.drawable.ic_cat_suscriptions, "ic_cat_suscriptions", "#2ECC71"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_education), R.drawable.ic_cat_graduation_cap, "ic_cat_graduation_cap", "#9B59B6"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_gifts), R.drawable.ic_cat_gifts, "ic_cat_gift", "#F1C40F"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_care), R.drawable.ic_cat_self_care, "ic_cat_self_care", "#F1C40F"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_clothes), R.drawable.ic_cat_shirt, "ic_cat_shirt", "#3498DB"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_shopping), R.drawable.ic_cat_shopping_cart, "ic_cat_shoping_cart", "#34495E"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_gym), R.drawable.ic_cat_gym, "ic_cat_gym", "#E74C3C"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_pets), R.drawable.ic_cat_pets, "ic_cat_pets", "#9B59B6"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_others), R.drawable.ic_cat_package, "ic_cat_package", "#1ABC9C"));

        dao.insertListOfCategories(categories);
    }

    /**
     * Inserta las rachas iniciales por defecto en la base de datos
     */
    public static void insertInitialStreaks() {
        StreakDao dao = INSTANCE.streakDao();
        UserProfile user = INSTANCE.userProfileDao().getUserProfileSync();
        if (user == null) {
            Log.i("AppDatabase", "Skipping initial streaks until a user profile exists");
            return;
        }
        long userId = user.getId();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Streak> streaks = new ArrayList<>();

        // Racha de registro de gastos
        Streak recordingStreak = new Streak();
        recordingStreak.setUserId(userId);
        recordingStreak.setType(Streak.TYPE_RECORDING);
        recordingStreak.setConsecutiveDays(0);
        recordingStreak.setStartDate(today);
        recordingStreak.setLastActivityDate(today);
        recordingStreak.setMaxStreakRecord(0);
        recordingStreak.setActive(true);

        // Racha de cumplimiento de presupuesto
        Streak budgetStreak = new Streak();
        budgetStreak.setUserId(userId);
        budgetStreak.setType(Streak.TYPE_BUDGET);
        budgetStreak.setConsecutiveDays(0);
        budgetStreak.setStartDate(today);
        budgetStreak.setLastActivityDate(today);
        budgetStreak.setMaxStreakRecord(0);
        budgetStreak.setActive(true);

        streaks.add(recordingStreak);
        streaks.add(budgetStreak);

        dao.insertListOfStreak(streaks);
    }

    public static void insertInitialUserXP() {
        UserProfile profile = INSTANCE.userProfileDao().getUserProfileSync();
        if (profile == null) {
            Log.i("AppDatabase", "Skipping initial user XP until a user profile exists");
            return;
        }
        UserXP userXP;
        userXP = new UserXP();
        userXP.setUserId(profile.getId());
        userXP.setTotalXP(0);
        userXP.setCurrentLevel(1);
        userXP.setXpForNextLevel(100);
        userXP.setAchievementsUnlocked(0);
        userXP.setLastXPEarnedAt(System.currentTimeMillis());
        INSTANCE.userXPDao().insertUserXP(userXP);
    }

    /**
     * Inserta los logros iniciales en la base de datos
     */
    public static void insertInitialAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        AchievementDao dao = INSTANCE.achievementDao();
        UserProfile user = INSTANCE.userProfileDao().getUserProfileSync();
        if (user == null) {
            Log.i("AppDatabase", "Skipping initial achievements until a user profile exists");
            return;
        }
        if (dao.getAchievementCountSync() > 0) {
            return;
        }

        // Logros de registro de gasto
        achievements.add(new Achievement(
                mContext.getString(R.string.first_step_title),
                mContext.getString(R.string.first_step_description),
                10, Constants.ACHIEVEMENT_FIRST_EXPENSE,
                false,
                R.color.primary,
                R.drawable.ic_achievement_first_step));
        achievements.add(new Achievement(
                mContext.getString(R.string.beginner_title),
                mContext.getString(R.string.beginner_description),
                10,
                Constants.ACHIEVEMENT_10_EXPENSES,
                false,
                R.color.secondary,
                R.drawable.ic_achievement_beginner));
        achievements.add(new Achievement(
                mContext.getString(R.string.disciplined_title),
                mContext.getString(R.string.disciplined_description),
                20, Constants.ACHIEVEMENT_50_EXPENSES,
                false,
                R.color.accent,
                R.drawable.ic_achievement_consistent));
        achievements.add(new Achievement(
                mContext.getString(R.string.centurion_title),
                mContext.getString(R.string.centurion_description),
                50, Constants.ACHIEVEMENT_100_EXPENSES,
                false,
                R.color.primary,
                R.drawable.ic_achievement_centurion));
        achievements.add(new Achievement(
                mContext.getString(R.string.legend_title),
                mContext.getString(R.string.legend_description),
                50,
                Constants.ACHIEVEMENT_500_EXPENSES,
                false,
                R.color.accent,
                R.drawable.ic_achievement_legend));

        //Logros por días seguidos
        achievements.add(new Achievement(
                mContext.getString(R.string.one_week_title),
                mContext.getString(R.string.one_week_description),
                30,
                Constants.ACHIEVEMENT_WEEK_STREAK,
                false,
                R.color.primary,
                R.drawable.ic_achievement_week));

        achievements.add(new Achievement(
                mContext.getString(R.string.one_month_title),
                mContext.getString(R.string.one_month_description),
                100,
                Constants.ACHIEVEMENT_MONTH_STREAK,
                false,
                R.color.secondary,
                R.drawable.ic_achievement_month));

        achievements.add(new Achievement(
                mContext.getString(R.string.hundred_days_title),
                mContext.getString(R.string.hundred_days_description),
                300,
                Constants.ACHIEVEMENT_100_DAYS_STREAK,
                false,
                R.color.accent,
                R.drawable.ic_achievement_unstoppable));

        // Presupuesto
        achievements.add(new Achievement(
                mContext.getString(R.string.create_budget_title),
                mContext.getString(R.string.create_budget_description),
                10,
                Constants.ACHIEVEMENT_CREATE_BUDGET,
                false,
                R.color.primary,
                R.drawable.ic_achievement_saver_advanced));
        achievements.add(new Achievement(
                mContext.getString(R.string.under_budget_week_title),
                mContext.getString(R.string.under_budget_week_description),
                40,
                Constants.ACHIEVEMENT_UNDER_BUDGET_WEEK,
                false,
                R.color.primary,
                R.drawable.ic_achievement_budget_week));

        achievements.add(new Achievement(
                mContext.getString(R.string.under_budget_month_title),
                mContext.getString(R.string.under_budget_month_description),
                150,
                Constants.ACHIEVEMENT_UNDER_BUDGET_MONTH,
                false,
                R.color.secondary,
                R.drawable.ic_achievement_budget_month));

        achievements.add(new Achievement(
                mContext.getString(R.string.under_budget_3_month_title),
                mContext.getString(R.string.under_budget_3_month_description),
                150,
                Constants.ACHIEVEMENT_UNDER_BUDGET_MONTH,
                false,
                R.color.secondary,
                R.drawable.ic_achievement_saver_master));

        for (Achievement achievement : achievements) {
            achievement.setUserId(user.getId());
        }
        dao.insertListOfAchievements(achievements);
    }

    /**
     * Inserta las alternativas iniciales en la base de datos
     */
    public static void insertInitialAlternatives() {
        AlternativeDao dao = INSTANCE.alternativeDao();
        ExpenseCategoryDao categoryDao = INSTANCE.expenseCategoryDao();
        if (categoryDao.getCategories().isEmpty()) {
            insertInitialCategories();
        }

        long clothesCategoryId = requireCategoryId(categoryDao, "ic_cat_shirt");
        long leisureCategoryId = requireCategoryId(categoryDao, "ic_cat_gamepad_2");
        long foodCategoryId = requireCategoryId(categoryDao, "ic_cat_food");
        long transportCategoryId = requireCategoryId(categoryDao, "ic_cat_transport");
        List<Alternative> alternatives = new ArrayList<>();

        // Ropa
        alternatives.add(new Alternative(clothesCategoryId, "", mContext.getString(R.string.alternative_clothes_1), 0.5));
        alternatives.add(new Alternative(clothesCategoryId, "", mContext.getString(R.string.alternative_clothes_2), 0.3));
        alternatives.add(new Alternative(clothesCategoryId, mContext.getString(R.string.description_clothes_1), mContext.getString(R.string.alternative_clothes_3), 0.4));

        // Ocio
        alternatives.add(new Alternative(leisureCategoryId, "", mContext.getString(R.string.alternative_leisure_1), 1.0));
        alternatives.add(new Alternative(leisureCategoryId, mContext.getString(R.string.description_leisure_1), mContext.getString(R.string.alternative_leisure_2), 0.4));
        alternatives.add(new Alternative(leisureCategoryId, mContext.getString(R.string.description_leisure_2), mContext.getString(R.string.alternative_leisure_3), 0.7));

        // Comida
        alternatives.add(new Alternative(foodCategoryId, mContext.getString(R.string.description_food_1), mContext.getString(R.string.alternative_food_1), 0.9));
        alternatives.add(new Alternative(foodCategoryId, mContext.getString(R.string.description_food_1), mContext.getString(R.string.alternative_food_2), 0.7));
        alternatives.add(new Alternative(foodCategoryId, mContext.getString(R.string.description_food_2), mContext.getString(R.string.alternative_food_3),  0.3));

        // Transporte
        alternatives.add(new Alternative(transportCategoryId, "", mContext.getString(R.string.alternative_transport_1), 0.6));
        alternatives.add(new Alternative(transportCategoryId, "", mContext.getString(R.string.alternative_transport_2), 0.6));
        alternatives.add(new Alternative(transportCategoryId, "", mContext.getString(R.string.alternative_transport_4), 0.6));
        alternatives.add(new Alternative(transportCategoryId, mContext.getString(R.string.description_transport_1), mContext.getString(R.string.alternative_transport_3), 0.8));

        dao.insertListOfAlternatives(alternatives);
    }

    private static long requireCategoryId(ExpenseCategoryDao categoryDao, String iconName) {
        Long categoryId = categoryDao.getCategoryIdByIconName(iconName);
        if (categoryId == null) {
            throw new IllegalStateException("Missing expense category with icon " + iconName);
        }
        return categoryId;
    }

    public static void dummyData() {
        ExpenseDao dao = INSTANCE.expenseDao();
        ExpenseCategoryDao categoryDao = INSTANCE.expenseCategoryDao();
        UserProfileDao userDao = INSTANCE.userProfileDao();
        StreakDao streakDao = INSTANCE.streakDao();
        UserXPDao xpDao = INSTANCE.userXPDao();

        UserProfile profile = userDao.getUserProfileSync();
        if (profile == null) {
            profile = new UserProfile();
            profile.setId(1);
        }
        profile.setDayCount(365); // 12 meses de actividad simulada
        userDao.insertOrUpdate(profile);
        profile = userDao.getUserProfileSync();

        if (categoryDao.getCategories().isEmpty()) {
            insertInitialCategories();
        }

        long foodId = requireCategoryId(categoryDao, "ic_cat_food");
        long shoppingId = requireCategoryId(categoryDao, "ic_cat_shoping_cart");
        long transportId = requireCategoryId(categoryDao, "ic_cat_transport");
        long homeId = requireCategoryId(categoryDao, "ic_nav_home");
        long leisureId = requireCategoryId(categoryDao, "ic_cat_gamepad_2");
        long subsId = requireCategoryId(categoryDao, "ic_cat_suscriptions");
        long clothesId = requireCategoryId(categoryDao, "ic_cat_shirt");
        long gymId = requireCategoryId(categoryDao, "ic_cat_gym");
        long othersId = requireCategoryId(categoryDao, "ic_cat_package");

        List<Expense> expenses = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        // Generar datos coherentes para los últimos 12 meses hasta el mes actual (sin superar el día actual)
        for (int i = 11; i >= 0; i--) {
            Calendar monthCal = (Calendar) cal.clone();
            monthCal.add(Calendar.MONTH, -i);
            int m = monthCal.get(Calendar.MONTH) + 1;
            int yr = monthCal.get(Calendar.YEAR);
            String mStr = String.format(Locale.US, "%02d", m);
            String yrStr = String.valueOf(yr);

            addExpenseIfValid(expenses, profile.getId(), "income", 1200.0, (int)othersId, "Nómina mensual", "01/" + mStr + "/" + yrStr, "09:00", yr, m, 1);
            addExpenseIfValid(expenses, profile.getId(), "expense", 500.0, (int)homeId, "Alquiler", "01/" + mStr + "/" + yrStr, "10:00", yr, m, 1);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(45.0, 75.0), (int)homeId, "Luz", "10/" + mStr + "/" + yrStr, "11:30", yr, m, 10);
            addExpenseIfValid(expenses, profile.getId(), "expense", 35.0, (int)gymId, "Gimnasio", "05/" + mStr + "/" + yrStr, "08:00", yr, m, 5);
            addExpenseIfValid(expenses, profile.getId(), "expense", 14.99, (int)subsId, "Suscripción streaming", "15/" + mStr + "/" + yrStr, "16:00", yr, m, 15);

            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(40.0, 90.0), (int)shoppingId, "Supermercado", "03/" + mStr + "/" + yrStr, "18:00", yr, m, 3);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(45.0, 100.0), (int)shoppingId, "Supermercado", "10/" + mStr + "/" + yrStr, "18:30", yr, m, 10);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(50.0, 110.0), (int)shoppingId, "Supermercado", "17/" + mStr + "/" + yrStr, "19:00", yr, m, 17);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(40.0, 95.0), (int)shoppingId, "Supermercado", "24/" + mStr + "/" + yrStr, "18:15", yr, m, 24);

            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(2.0, 3.5), (int)foodId, "Café", "04/" + mStr + "/" + yrStr, "09:30", yr, m, 4);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(2.0, 3.5), (int)foodId, "Café", "12/" + mStr + "/" + yrStr, "10:15", yr, m, 12);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(2.0, 3.5), (int)foodId, "Café", "19/" + mStr + "/" + yrStr, "09:45", yr, m, 19);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(2.0, 3.5), (int)foodId, "Café", "26/" + mStr + "/" + yrStr, "10:00", yr, m, 26);

            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(25.0, 70.0), (int)leisureId, "Cena amigos", "08/" + mStr + "/" + yrStr, "21:30", yr, m, 8);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(15.0, 40.0), (int)leisureId, "Cine y palomitas", "22/" + mStr + "/" + yrStr, "20:00", yr, m, 22);

            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(30.0, 60.0), (int)transportId, "Gasolina", "12/" + mStr + "/" + yrStr, "14:00", yr, m, 12);
            addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(30.0, 60.0), (int)transportId, "Gasolina", "25/" + mStr + "/" + yrStr, "15:30", yr, m, 25);

            if (i % 2 == 0) {
                addExpenseIfValid(expenses, profile.getId(), "expense", getRandom(20.0, 65.0), (int)clothesId, "Ropa", "18/" + mStr + "/" + yrStr, "17:00", yr, m, 18);
            }
        }

        dao.deleteAll();
        dao.insertListOfExpenses(expenses);

        // Actualizar tabla Streak, comprobar logros y actualizar userXP
        try {
            streakDao.deleteAll();
            String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Streak recordingStreak = new Streak();
            recordingStreak.setUserId(profile.getId());
            recordingStreak.setType(Streak.TYPE_RECORDING);
            recordingStreak.setConsecutiveDays(5);
            recordingStreak.setStartDate(LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            recordingStreak.setLastActivityDate(todayStr);
            recordingStreak.setMaxStreakRecord(12);
            recordingStreak.setActive(true);

            Streak budgetStreak = new Streak();
            budgetStreak.setUserId(profile.getId());
            budgetStreak.setType(Streak.TYPE_BUDGET);
            budgetStreak.setConsecutiveDays(4);
            budgetStreak.setStartDate(LocalDate.now().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            budgetStreak.setLastActivityDate(todayStr);
            budgetStreak.setMaxStreakRecord(10);
            budgetStreak.setActive(true);

            List<Streak> streaks = new ArrayList<>();
            streaks.add(recordingStreak);
            streaks.add(budgetStreak);
            streakDao.insertListOfStreak(streaks);

            AchievementController achievementController = new AchievementController(mContext);
            achievementController.checkAndUnlockAchievements();

            // Actualizar tabla userXP en consecuencia
            UserXP userXP = xpDao.getUserXPByUserId(profile.getId());
            if (userXP == null) {
                userXP = new UserXP();
                userXP.setUserId(profile.getId());
                xpDao.insertUserXP(userXP);
                userXP = xpDao.getUserXPByUserId(profile.getId());
            }
            if (userXP != null) {
                int transactionXp = expenses.size() * 10;
                int achievementPoints = INSTANCE.achievementDao().getTotalPointsSync();
                int totalXpEarned = transactionXp + achievementPoints + 200;

                userXP.setTotalXP(totalXpEarned);
                int level = 1;
                int remainingXp = totalXpEarned;
                while (remainingXp >= 100 * level) {
                    remainingXp -= 100 * level;
                    level++;
                }
                userXP.setCurrentLevel(level);
                userXP.setXpForNextLevel((100 * level) - remainingXp);
                userXP.setAchievementsUnlocked(INSTANCE.achievementDao().getUnlockedCountSync());
                userXP.setLastXPEarnedAt(System.currentTimeMillis());
                xpDao.updateUserXP(userXP);
            }

            Log.d("AppDatabase", "Dummy data populated successfully, streaks, achievements and userXP updated.");
        } catch (Exception e) {
            Log.e("AppDatabase", "Error updating streaks/achievements/userXP during dummyData: " + e.getMessage(), e);
        }
    }

    private static void addExpenseIfValid(List<Expense> list, long userId, String type, double amount, int category, String desc, String dateStr, String timeStr, int yr, int m, int day) {
        Calendar targetCal = Calendar.getInstance();
        int hour = 12;
        int minute = 0;
        try {
            String[] parts = timeStr.split(":");
            if (parts.length >= 2) {
                hour = Integer.parseInt(parts[0].trim());
                minute = Integer.parseInt(parts[1].trim());
            }
        } catch (Exception ignored) {}

        targetCal.set(yr, m - 1, day, hour, minute, 0);
        targetCal.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        if (targetCal.before(now) || targetCal.equals(now)) {
            Expense e = new Expense(type, amount, category, desc, dateStr);
            e.setTime(timeStr);
            e.setUserId(userId);
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                java.util.Date parsed = sdf.parse(dateStr + " " + timeStr);
                if (parsed != null) {
                    e.setTimestamp(parsed.getTime());
                }
            } catch (Exception ignored) {
                e.setTimestamp(targetCal.getTimeInMillis());
            }
            list.add(e);
        }
    }

    private static double getRandom(Double valorMinimo, Double valorMaximo) {
        Random rand = new Random();
        double randomValue = valorMinimo + (valorMaximo - valorMinimo) * rand.nextDouble();
        return Math.round(randomValue * 100.0) / 100.0;
    }

    public static void generateDummyCsvAndPopulate(Context context) {
        dummyData();
        try {
            java.io.File sharedPrefsDir = new java.io.File(context.getApplicationInfo().dataDir, "shared_prefs");
            if (!sharedPrefsDir.exists()) {
                sharedPrefsDir.mkdirs();
            }
            java.io.File csvFile = new java.io.File(sharedPrefsDir, "equilibra_dummy_expenses.csv");

            AppDatabase db = AppDatabase.getInstance(context);
            List<Expense> allExpenses = db.expenseDao().getAllExpenses();

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(csvFile);
                 java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(fos, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write("type,amount,category,description,date,time,timestamp\n");
                for (Expense e : allExpenses) {
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
            }
            Log.d("AppDatabase", "Dummy CSV generated at: " + csvFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("AppDatabase", "Error generating dummy CSV", e);
        }
    }

}
