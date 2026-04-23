package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.AchievementRepository;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.Constants;

public class AchievementController {

    private static final String TAG = "AchievementController";
    private final AchievementRepository achievementRepository;
    private final UserProfileRepository userProfileRepository;
    private final ExpenseRepository expenseRepository;


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AchievementController(Context context) {
        this.achievementRepository = new AchievementRepository(context);
        this.userProfileRepository = new UserProfileRepository(context);
        this.expenseRepository = new ExpenseRepository(context);

    }

    public LiveData<List<Achievement>> getAllAchievements() {
        return achievementRepository.getAllAchievements();
    }

    public List<Achievement> getAllAchievementsSync() {
        return achievementRepository.getAllAchievementsSync();
    }

    public int getTotalPointsSync() {
        return achievementRepository.getTotalPointsSync();
    }

    public String getUserLevel() {
        int points = getTotalPointsSync();
        if (points >= 1000) return "Maestro";
        if (points >= 500) return "Experto";
        if (points >= 200) return "Avanzado";
        if (points >= 50) return "Iniciado";
        return "Principiante";
    }

    public int getProgressToNextLevel() {
        int points = getTotalPointsSync();
        if (points >= 1000) return 100;
        if (points >= 500) return (points - 500) * 100 / 500;
        if (points >= 200) return (points - 200) * 100 / 300;
        if (points >= 50) return (points - 50) * 100 / 150;
        return points * 100 / 50;
    }


    public LiveData<List<Achievement>> getUnlockedAchievements() {
        return achievementRepository.getUnlockedAchievements();
    }

    public LiveData<List<Achievement>> getLockedAchievements() {
        return achievementRepository.getLockedAchievements();
    }

    public LiveData<Integer> getTotalPoints() {
        return achievementRepository.getTotalPoints();
    }

    public LiveData<Integer> getUnlockedCount() {
        return achievementRepository.getUnlockedCount();
    }

    public LiveData<Integer> getTotalAchievements() {
        return achievementRepository.getTotalAchievements();
    }

    public LiveData<Integer> getMaxPossiblePoints() {
        return achievementRepository.getMaxPossiblePoints();
    }

    public LiveData<Achievement> getNewlyUnlockedAchievement() {
        return achievementRepository.getNewlyUnlockedAchievement();
    }

    public void resetAllAchievements() {
        achievementRepository.resetAllAchievements();
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
        UserProfile profile = userProfileRepository.getUserProfile().getValue();
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
        int weeksUnderBudget = userProfileRepository.getWeeksUnderBudget();

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
        double totalSaved = getTotalSavedAmount();

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
     * Calcula el ahorro total acumulado basado en el presupuesto mensual
     * @return ahorro total
     */
    public double getTotalSavedAmount() {
        //TODO implementar con Budget
        double monthlyBudget = userProfileRepository.getUserMonthlyIncome();
        List<Expense> allExpenses = expenseRepository.getAllExpenses();

        // Agrupar por Año y Mes
        Map<String, Double> monthlyTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        for (Expense e : allExpenses) {
            cal.setTimeInMillis(e.getTimestamp());
            String key = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH);
            monthlyTotals.put(key, monthlyTotals.getOrDefault(key, 0.0) + e.getAmount());
        }

        double totalSaved = 0;
        for (Double totalSpent : monthlyTotals.values()) {
            double saved = monthlyBudget - totalSpent;
            if (saved > 0) {
                totalSaved += saved;
            }
        }
        return totalSaved;
    }


    /**
     * Desbloquea un logro si aún no está desbloqueado
     */
    private void unlockAchievementIfNotUnlocked(String achievementKey) {
        executorService.execute(() -> {
            try {
                boolean isUnlocked = achievementRepository.isAchievementUnlocked(achievementKey);

                if (!isUnlocked) {
                    long timestamp = System.currentTimeMillis();
                    achievementRepository.unlockAchievement(achievementKey, timestamp);

                    // Obtener el logro completo para notificar
                    Achievement achievement = achievementRepository.getAchievementByKey(achievementKey);
                    if (achievement != null) {
                        achievementRepository.newlyUnlockedAchievement.postValue(achievement);
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
}

