package asunfdiez.com.equilibra.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Alternative;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.Challenge;
import asunfdiez.com.equilibra.models.DailyAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * Clase principal de la base de datos de la aplicación utilizando Room.
 * 
 * <p>Define todas las entidades que componen el esquema de persistencia local y 
 * proporciona los métodos abstractos para acceder a sus respectivos DAOs. Implementa 
 * el patrón Singleton para garantizar una única instancia de la base de datos y 
 * gestiona la inicialización de datos semilla (categorías, logros, etc.) en la 
 * primera ejecución.</p>
 */
@Database(entities = { Achievement.class, Budget.class, Challenge.class, DailyAnalysis.class, Expense.class, ExpenseCategory.class, InterventionLog.class, SavingGoal.class, Streak.class, UserProfile.class, Alternative.class }, version = Constants.DATABASE_VERSION, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // Instancia única de la base de datos (Patrón Singleton)
    private static volatile AppDatabase INSTANCE;
    private static Context mContext;

    // Métodos de acceso a los Objetos de Acceso a Datos (DAOs)
    public abstract UserProfileDao userProfileDao();
    public abstract SavingGoalDao savingGoalDao();
    public abstract ExpenseDao expenseDao();
    public abstract ExpenseCategoryDao expenseCategoryDao();
    public abstract AchievementDao achievementDao();
    public abstract AlternativeDao alternativeDao();
    public abstract StreakDao streakDao();

    public abstract ChallengeDao challengeDao();

    public abstract BudgetDao budgetDao();

    public abstract DailyAnalysisDao dailyAnalysisDao();

    public abstract InterventionLogDao interventionLogDao();


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
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private final static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            initialConfiguration();
        }
    };

    public static void initialConfiguration() {
        // Usamos un Executor para no bloquear el hilo principal
        Executors.newSingleThreadExecutor().execute(AppDatabase::insertInitialCategories);

        Executors.newSingleThreadExecutor().execute(AppDatabase::insertInitialAchievements);

        Executors.newSingleThreadExecutor().execute(AppDatabase::insertInitialAlternatives);
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

    public static void initInitalStreaks() {
        //TODO
        List <Streak> streaks = new ArrayList<>();
        StreakDao dao = INSTANCE.streakDao();
        streaks.add ( new Streak ());
        dao.insertListOfStreak(streaks);
    }

    /**
     * Inserta los logros iniciales en la base de datos
     */
    public static void insertInitialAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        AchievementDao dao = INSTANCE.achievementDao();

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
                mContext.getString(R.string.constant_title),
                mContext.getString(R.string.constant_description),
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
                mContext.getString(R.string.legend_description),
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
                mContext.getString(R.string.one_week_title),
                mContext.getString(R.string.one_week_description),
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
                mContext.getString(R.string.under_budget_week_title),
                mContext.getString(R.string.under_budget_week_description),
                40,
                Constants.ACHIEVEMENT_UNDER_BUDGET_WEEK,
                false,
                R.color.primary,
                R.drawable.ic_achievement_budget_week));

        achievements.add(new Achievement(
                mContext.getString(R.string.under_budget_month_title),
                mContext.getString(R.string.under_budget_week_description),
                150,
                Constants.ACHIEVEMENT_UNDER_BUDGET_MONTH,
                false,
                R.color.secondary,
                R.drawable.ic_achievement_budget_month));

        // Logros de categorías
        achievements.add(new Achievement(
                mContext.getString(R.string.category_explorer_title),
                mContext.getString(R.string.category_explorer_description),
                25,
                Constants.ACHIEVEMENT_CATEGORY_EXPLORER,
                false,
                R.color.primary,
                R.drawable.ic_achievement_explorer));

        achievements.add(new Achievement(
                mContext.getString(R.string.category_master_title),
                mContext.getString(R.string.category_master_description),
                75,
                Constants.ACHIEVEMENT_CATEGORY_MASTER,
                false,
                R.color.secondary,
                R.drawable.ic_ach_star));

        // Ahorro
        achievements.add(new Achievement(
                mContext.getString(R.string.saver_50_title),
                mContext.getString(R.string.saver_50_description),
                50,
                Constants.ACHIEVEMENT_SAVER_50,
                false,
                R.color.primary,
                R.drawable.ic_achievement_saver_beginner
        ));

        achievements.add(new Achievement(
                mContext.getString(R.string.saver_100_title),
                mContext.getString(R.string.saver_100_description),
                100,
                Constants.ACHIEVEMENT_SAVER_100,
                false,
                R.color.secondary,
                R.drawable.ic_achievement_saver_advanced
        ));

        achievements.add(new Achievement(
                mContext.getString(R.string.saver_500_title),
                mContext.getString(R.string.saver_500_description),
                250,
                Constants.ACHIEVEMENT_SAVER_500,
                false,
                R.color.accent,
                R.drawable.ic_achievement_saver_master
        ));

        dao.insertListOfAchievements(achievements);
    }

    /**
     * Inserta las alternativas iniciales en la base de datos
     */
    public static void insertInitialAlternatives() {
        AlternativeDao dao = INSTANCE.alternativeDao();
        List<Alternative> alternatives = new ArrayList<>();

        // Ropa
        alternatives.add(new Alternative(mContext.getString(R.string.category_clothes), "", mContext.getString(R.string.alternative_clothes_1), 0.5));
        alternatives.add(new Alternative(mContext.getString(R.string.category_clothes), "", mContext.getString(R.string.alternative_clothes_2), 0.3));
        alternatives.add(new Alternative(mContext.getString(R.string.category_clothes), mContext.getString(R.string.description_clothes_1), mContext.getString(R.string.alternative_clothes_3), 0.4));

        // Ocio
        alternatives.add(new Alternative(mContext.getString(R.string.category_leisure), "", mContext.getString(R.string.alternative_leisure_1), 1.0));
        alternatives.add(new Alternative(mContext.getString(R.string.category_leisure), mContext.getString(R.string.description_leisure_1), mContext.getString(R.string.alternative_leisure_2), 0.4));
        alternatives.add(new Alternative(mContext.getString(R.string.category_leisure), mContext.getString(R.string.description_leisure_2), mContext.getString(R.string.alternative_leisure_3), 0.7));

        // Comida
        alternatives.add(new Alternative(mContext.getString(R.string.category_food), mContext.getString(R.string.description_food_1), mContext.getString(R.string.alternative_food_1), 0.9));
        alternatives.add(new Alternative(mContext.getString(R.string.category_food), mContext.getString(R.string.description_food_1), mContext.getString(R.string.alternative_food_2), 0.7));
        alternatives.add(new Alternative(mContext.getString(R.string.category_food), mContext.getString(R.string.description_food_2), mContext.getString(R.string.alternative_food_3),  0.3));

        // Transporte
        alternatives.add(new Alternative(mContext.getString(R.string.category_transport), "", mContext.getString(R.string.alternative_transport_1), 0.6));
        alternatives.add(new Alternative(mContext.getString(R.string.category_transport), "", mContext.getString(R.string.alternative_transport_2), 0.6));
        alternatives.add(new Alternative(mContext.getString(R.string.category_transport), "", mContext.getString(R.string.alternative_transport_4), 0.6));
        alternatives.add(new Alternative(mContext.getString(R.string.category_transport), mContext.getString(R.string.description_transport_1), mContext.getString(R.string.alternative_transport_3), 0.8));

        dao.insertListOfAlternatives(alternatives);
    }

    public static void dummyData() {
        ExpenseDao dao = INSTANCE.expenseDao();
        List<Expense> expenses = new ArrayList<>();
        
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

        dao.deleteAll();
        dao.insertListOfExpenses(expenses);
    }

    private static double getRandom(Double valorMinimo, Double valorMaximo) {
        Random rand = new Random();
        double randomValue = valorMinimo + (valorMaximo - valorMinimo) * rand.nextDouble();
        return Math.round(randomValue * 100.0) / 100.0;
    }

}
