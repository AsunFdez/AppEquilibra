package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AchievementDao;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * Repository para gestionar la lógica de Logros (Achievements)
 * Maneja la detección automática de logros y su persistencia
 * Patrón Repository: Separa la lógica de negocio de la fuente de datos
 */
public class AchievementRepository {

    private static final String TAG = "AchievementRepository";

    private final AchievementDao achievementDao;
    private final ExecutorService executorService;

    // LiveData observables
    private final LiveData<List<Achievement>> allAchievements;
    private final LiveData<Integer> totalPoints;
    private final LiveData<Integer> unlockedCount;
    private final LiveData<Integer> totalAchievements;
    private final LiveData<Integer> maxPossiblePoints;

    // MutableLiveData para notificar eventos
    private final MutableLiveData<Achievement> newlyUnlockedAchievement = new MutableLiveData<>();

    private final UserProfileRepository userRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Constructor del Repository
     */
    public AchievementRepository(Context context) {
        this.achievementDao = AppDatabase.getInstance(context).achievementDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.userRepository = new UserProfileRepository(context);
        this.expenseRepository = new ExpenseRepository(context);

        // Inicializar LiveData
        this.allAchievements = achievementDao.getAllAchievements();
        this.totalPoints = achievementDao.getTotalPoints();
        this.unlockedCount = achievementDao.getUnlockedCount();
        this.totalAchievements = achievementDao.getTotalAchievements();
        this.maxPossiblePoints = achievementDao.getMaxPossiblePoints();

        executorService.execute(() -> {
            int count = achievementDao.getAchievementCountSync();
            if (count == 0) {
                Log.d(TAG, "Base de datos de logros vacía. Insertando iniciales...");
                AppDatabase.insertInitialAchievements();
            }
        });

    }

    // ========== MÉTODOS DE ACCESO A DATOS ==========

    /**
     * Obtiene todos los logros como LiveData
     */
    public LiveData<List<Achievement>> getAllAchievements() {
        return allAchievements;
    }

    /**
     * Obtiene solo logros desbloqueados
     */
    public LiveData<List<Achievement>> getUnlockedAchievements() {
        return achievementDao.getUnlockedAchievements();
    }

    /**
     * Obtiene solo logros bloqueados
     */
    public LiveData<List<Achievement>> getLockedAchievements() {
        return achievementDao.getLockedAchievements();
    }

    /**
     * Obtiene el total de puntos acumulados
     */
    public LiveData<Integer> getTotalPoints() {
        return totalPoints;
    }

    /**
     * Obtiene el conteo de logros desbloqueados
     */
    public LiveData<Integer> getUnlockedCount() {
        return unlockedCount;
    }

    /**
     * Obtiene el total de logros
     */
    public LiveData<Integer> getTotalAchievements() {
        return totalAchievements;
    }

    /**
     * Obtiene todos los logros de forma síncrona
     */
    public List<Achievement> getAllAchievementsSync() {
        return achievementDao.getAllAchievementsSync();
    }

    /**
     * Obtiene los puntos totales de forma síncrona
     */
    public int getTotalPointsSync() {
        return achievementDao.getTotalPointsSync();
    }

    /**
     * Obtiene los puntos máximos posibles
     */
    public LiveData<Integer> getMaxPossiblePoints() {
        return maxPossiblePoints;
    }

    /**
     * LiveData para observar cuando se desbloquea un nuevo logro
     */
    public LiveData<Achievement> getNewlyUnlockedAchievement() {
        return newlyUnlockedAchievement;
    }

    /**
     * Obtiene logros recientes
     */
    public LiveData<List<Achievement>> getRecentUnlocked(int limit) {
        return achievementDao.getRecentUnlocked(limit);
    }

    // ========== MÉTODOS DE DETECCIÓN DE LOGROS ==========

    /**
     * Verifica y desbloquea logros basados en el comportamiento del usuario
     * Se debe llamar después de eventos importantes (nuevo gasto, racha, etc.)
     */
    public void checkAndUnlockAchievements() {
        executorService.execute(() -> {
            try {

                // Verificar cada tipo de logro
                checkExpenseCountAchievements();
                checkStreakAchievements();
                checkBudgetAchievements();
                checkCategoryAchievements();
                checkSavingsAchievements();

                Log.d(TAG, "Achievements checked successfully");

            } catch (Exception e) {
                Log.e(TAG, "Error checking achievements: " + e.getMessage(), e);
            }
        });
    }


    /**
     * Logros por cantidad de gastos registrados
     */
    private void checkExpenseCountAchievements() {
        int totalExpenses = expenseRepository.getTotalExpenseCount();
        if (totalExpenses == 1) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_FIRST_EXPENSE);
        }
        if (totalExpenses >= 10) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_10_EXPENSES);
        }
        if (totalExpenses >= 50) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_50_EXPENSES);
        }
        if (totalExpenses >= 100) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_100_EXPENSES);
        }
        if (totalExpenses >= 500) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_500_EXPENSES);
        }
    }

    /**
     * Logros por racha de días consecutivos
     */
    private void checkStreakAchievements() {
        UserProfile profile = userRepository.getUserProfile().getValue();
        if (profile == null) return;
        int consecutiveDays = profile.getDayCount();

        if (consecutiveDays >= 7) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_WEEK_STREAK);
        }
        if (consecutiveDays >= 30) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_MONTH_STREAK);
        }
        if (consecutiveDays >= 100) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_100_DAYS_STREAK);
        }
    }

    /**
     * Logros relacionados con presupuesto
     */
    private void checkBudgetAchievements() {
        int weeksUnderBudget = userRepository.getWeeksUnderBudget();

        if (weeksUnderBudget >= 1) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_UNDER_BUDGET_WEEK);
        }
        if (weeksUnderBudget >= 4) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_UNDER_BUDGET_MONTH);
        }
    }

    /**
     * Logros por uso de categorías
     */
    private void checkCategoryAchievements() {
        int categoriesUsed = expenseRepository.getUniqueCategoriesCount();
        if (categoriesUsed >= 5) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_CATEGORY_EXPLORER);
        }
        if (categoriesUsed >= 10) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_CATEGORY_MASTER);
        }
    }

    /**
     * Logros por ahorro
     */
    private void checkSavingsAchievements() {
        double totalSaved = expenseRepository.getTotalSavedAmount();

        if (totalSaved >= 50) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_SAVER_50);
        }
        if (totalSaved >= 100) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_SAVER_100);
        }
        if (totalSaved >= 500) {
            unlockAchievementIfNotUnlocked(Constants.ACHIEVEMENT_SAVER_500);
        }
    }

    /**
     * Desbloquea un logro si aún no está desbloqueado
     */
    private void unlockAchievementIfNotUnlocked(String achievementKey) {
        executorService.execute(() -> {
            try {
                boolean isUnlocked = achievementDao.isAchievementUnlocked(achievementKey);

                if (!isUnlocked) {
                    long timestamp = System.currentTimeMillis();
                    achievementDao.unlockAchievement(achievementKey, timestamp);

                    // Obtener el logro completo para notificar
                    Achievement achievement = achievementDao.getAchievementByKey(achievementKey);
                    if (achievement != null) {
                        newlyUnlockedAchievement.postValue(achievement);
                        Log.d(TAG, "Achievement unlocked: " + achievementKey);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error unlocking achievement: " + achievementKey, e);
            }
        });
    }

    /**
     * Desbloquea un logro manualmente (para testing)
     */
    public void unlockAchievement(String achievementKey) {
        unlockAchievementIfNotUnlocked(achievementKey);
    }

    /**
     * Limpia todos los logros (útil para testing)
     */
    public void resetAllAchievements() {
        executorService.execute(() -> {
            achievementDao.deleteAll();
        });
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
