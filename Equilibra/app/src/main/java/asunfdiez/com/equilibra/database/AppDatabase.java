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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
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
        List<Expense> expenses = new ArrayList<>();
        UserProfile profile = INSTANCE.userProfileDao().getUserProfileSync();
        if (profile == null) {
            throw new IllegalStateException("Cannot create sample expenses without a user profile");
        }
        
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String y = String.valueOf(year);

        expenses.add(new Expense("expense",getRandom(45., 120.) , 1, "supermercado", "10/01/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "17/01/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "24/01/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "31/01/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "07/02/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "14/02/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "21/02/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "28/02/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "07/03/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "14/03/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "21/03/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "28/03/" + y));
        expenses.add(new Expense("expense",getRandom(45., 120.), 1, "supermercado", "04/04/" + y));

        expenses.add(new Expense("expense",getRandom(30., 80.) , 5, "cena amigos", "10/01/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "17/01/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "24/01/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "31/01/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "07/02/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "14/02/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "21/02/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "28/02/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "07/03/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "14/03/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "21/03/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "28/03/" + y));
        expenses.add(new Expense("expense",getRandom(30., 80.), 5, "cena amigos", "04/04/" + y));

        expenses.add(new Expense("expense",getRandom(2., 3.) , 1, "café", "01/01/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "07/01/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "14/01/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "21/01/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "05/02/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "04/02/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "11/02/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "18/02/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "17/03/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "04/03/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "01/03/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "08/03/" + y));
        expenses.add(new Expense("expense",getRandom(2., 3.), 1, "café", "04/04/" + y));


        expenses.add(new Expense("expense",getRandom(20., 50.) , 10, "camisa", "01/01/" + y));
        expenses.add(new Expense("expense",getRandom(20., 50.), 10, "pantalón", "17/02/" + y));
        expenses.add(new Expense("expense",getRandom(20., 50.), 10, "vestido", "24/03/" + y));
        expenses.add(new Expense("expense",getRandom(20., 50.), 10, "calcetines", "01/04/" + y));

        expenses.add(new Expense("expense",500 , 3, "alquiler", "01/01/" + y));
        expenses.add(new Expense("expense",500 , 3, "alquiler", "01/02/" + y));
        expenses.add(new Expense("expense",500 , 3, "alquiler", "01/03/" + y));
        expenses.add(new Expense("expense",500 , 3, "alquiler", "01/04/" + y));

        expenses.add(new Expense("expense",getRandom(40., 70.) , 3, "luz", "10/01/" + y));
        expenses.add(new Expense("expense",getRandom(40., 70.) , 3, "luz", "12/02/" + y));
        expenses.add(new Expense("expense",getRandom(70., 170.) , 3, "calefacción", "15/01/" + y));
        expenses.add(new Expense("expense",getRandom(70., 170.) , 3, "calefacción", "15/04/" + y));

        // Gastos para el mes actual para que se vean en el dashboard
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String mStr = String.format(java.util.Locale.US, "/%02d/", currentMonth);
        expenses.add(new Expense("expense",getRandom(50., 100.) , 1, "Compra semanal", "02" + mStr + y));
        expenses.add(new Expense("expense",getRandom(20., 40.) , 2, "Gasolina", "05" + mStr + y));
        expenses.add(new Expense("expense",getRandom(10., 30.) , 5, "Cine", "08" + mStr + y));
        expenses.add(new Expense("expense",getRandom(15., 25.) , 1, "Pizza", "10" + mStr + y));

        for (Expense expense : expenses) {
            expense.setUserId(profile.getId());
        }
        dao.deleteAll();
        dao.insertListOfExpenses(expenses);
    }

    private static double getRandom(Double valorMinimo, Double valorMaximo) {
        Random rand = new Random();
        double randomValue = valorMinimo + (valorMaximo - valorMinimo) * rand.nextDouble();
        return Math.round(randomValue * 100.0) / 100.0;
    }

}
