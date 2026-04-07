package asunfdiez.com.equilibra.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.Challenge;
import asunfdiez.com.equilibra.models.DailyAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.utils.Constants;

@Database(entities = { Achievement.class, Budget.class, Challenge.class, DailyAnalysis.class, Expense.class, ExpenseCategory.class, InterventionLog.class, SavingGoal.class, Streak.class, UserXP.class }, version = Constants.DATABASE_VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static Context mContext;

    public abstract ExpenseDao expenseDao();

    public abstract ExpenseCategoryDao expenseCategoryDao();

    public abstract AchievementDao achievementDao();

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

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Usamos un Executor para no bloquear el hilo principal
            Executors.newSingleThreadExecutor().execute(() -> {
                insertInitialCategories();
            });

            Executors.newSingleThreadExecutor().execute(() -> {
                insertInitialAchievements();
            });

        }
    };

    /**
     * Inserta las categorias por defecto en la base de datos
     */
    private static void insertInitialCategories() {
        ExpenseCategoryDao dao = INSTANCE.expenseCategoryDao();
        List<ExpenseCategory> categories = new ArrayList<>();

        categories.add(new ExpenseCategory(mContext.getString(R.string.category_food), R.drawable.ic_cat_food, "ic_cat_food", "#2ECC71"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_transport), R.drawable.ic_cat_food, "ic_cat_transport", "#3498DB"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_home), R.drawable.ic_nav_home, "ic_nav_home", "#1ABC9C"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_health), R.drawable.ic_cat_heart, "'ic_cat_heart", "#E74C3C"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_leisure), R.drawable.ic_cat_gamepad_2, "ic_cat_gamepad_2", "#E67E22"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_education), R.drawable.ic_cat_graduation_cap, "ic_cat_graduation_cap", "#9B59B6"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_care), R.drawable.ic_cat_gift, "ic_cat_gift", "#F1C40F"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_clothes), R.drawable.ic_cat_shirt, "ic_cat_shirt", "#27AE60"));
        categories.add(new ExpenseCategory(mContext.getString(R.string.category_others), R.drawable.ic_cat_package, "ic_cat_package", "#34495E"));

        dao.insertListOfCategories(categories);
    }

    /**
     * Inserta los logros iniciales en la base de datos
     */
    private static void insertInitialAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        AchievementDao dao = INSTANCE.achievementDao();

        // Logros de inicio
        achievements.add(new Achievement(mContext.getString(R.string.firs_step_title), mContext.getString(R.string.first_step_description), 10, "ic_first_step", false));
        achievements.add(new Achievement(mContext.getString(R.string.constant_title), mContext.getString(R.string.constant_description), 20, "ic_constant", false));
        achievements.add(new Achievement(mContext.getString(R.string.saver_title), mContext.getString(R.string.saver_description), 50, "ic_saver", false));
        achievements.add(new Achievement(mContext.getString(R.string.disciplined_title), mContext.getString(R.string.disciplined_description), 75, "ic_disciplined", false));
        achievements.add(new Achievement(mContext.getString(R.string.master_title), mContext.getString(R.string.master_description), 100, "ic_master", false));

        // Logros de categorías
        achievements.add(new Achievement(mContext.getString(R.string.gourmet_title), mContext.getString(R.string.gourmet_description), 30, "ic_food", false));
        achievements.add(new Achievement(mContext.getString(R.string.transport_title), mContext.getString(R.string.transport_description), 30, "ic_transport", false));
        achievements.add(new Achievement(mContext.getString(R.string.fun_title), mContext.getString(R.string.fun_description), 30, "ic_fun", false));

        // Logros especiales
        achievements.add(new Achievement(mContext.getString(R.string.goal_title), mContext.getString(R.string.goal_description), 100, "ic_goal", false));
        achievements.add(new Achievement(mContext.getString(R.string.expert_title), mContext.getString(R.string.expert_description), 150, "ic_expert", false));

        dao.insertListOfAchievements(achievements);
    }
}
