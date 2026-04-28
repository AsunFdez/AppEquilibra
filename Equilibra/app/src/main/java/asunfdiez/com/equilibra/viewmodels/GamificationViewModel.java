package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import asunfdiez.com.equilibra.controllers.AchievementController;
import asunfdiez.com.equilibra.controllers.StreakController;
import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;

/**
 * ViewModel centralizado para la gestión de todo el sistema de gamificación.
 * 
 * <p>Coordina la lógica de puntos de experiencia, niveles, rachas de uso y desbloqueo 
 * de logros. Provee datos unificados a múltiples pantallas (MainActivity, AchievementsActivity) 
 * y asegura que el progreso del usuario se actualice reactivamente en toda la app.</p>
 */
public class GamificationViewModel extends AndroidViewModel {

    // Controlador para la lógica compleja de desbloqueo de logros
    private final UserProfileController userProfileController;
    private final AchievementController achievementController;
    private final StreakController streakController;

    private final LiveData<UserProfile> userProfile;
    private final MutableLiveData<List<Achievement>> _allAchievements = new MutableLiveData<>(new java.util.ArrayList<>());
    private final MutableLiveData<Integer> _totalPoints = new MutableLiveData<>(0);
    private final MutableLiveData<String> _userLevel = new MutableLiveData<>("");
    private final MutableLiveData<Integer> _levelProgress = new MutableLiveData<>(0);

    /**
     * Logros recientes desbloqueados (últimos 4 para el preview)
     */
    private final LiveData<List<Achievement>> recentUnlockedAchievements;

    /**
     * Event: Logro desbloqueado (para mostrar animación)
     */
    private final MutableLiveData<Achievement> _achievementUnlocked = new MutableLiveData<>();

    /**
     * Rachas del usuario
     */
    private final LiveData<Streak> recordingStreak;
    private final LiveData<Streak> budgetStreak;

    /**
     * Constructor
     */
    public GamificationViewModel(@NonNull Application application) {
        super(application);
        this.userProfileController = new UserProfileController(application);
        this.achievementController = new AchievementController(application);
        this.streakController = new StreakController(application);
        this.userProfile = userProfileController.getCurrentUser();

        // Obtener rachas del usuario actual
        this.recordingStreak = Transformations.switchMap(userProfile, user -> {
            if (user != null) {
                return streakController.getStreakByType(user.getId(), Streak.TYPE_RECORDING);
            }
            return new MutableLiveData<>();
        });

        this.budgetStreak = Transformations.switchMap(userProfile, user -> {
            if (user != null) {
                return streakController.getStreakByType(user.getId(), Streak.TYPE_BUDGET);
            }
            return new MutableLiveData<>();
        });

        // Obtener logros recientes (últimos 4)
        this.recentUnlockedAchievements = achievementController.getRecentUnlocked(4);

        // Cargar datos iniciales
        loadAchievements();
    }

    /**
     * Cargar todos los logros
     */
    private void loadAchievements() {
        new Thread(() -> {
            try {
                List<Achievement> achievements = achievementController.getAllAchievementsSync();
                _allAchievements.postValue(achievements);

                // Calcular puntos totales
                int totalPoints = achievementController.getTotalPointsSync();
                _totalPoints.postValue(totalPoints);

                // Obtener nivel
                String level = achievementController.getUserLevel();
                _userLevel.postValue(level);

                // Obtener progreso hacia siguiente nivel
                int progress = achievementController.getProgressToNextLevel();
                _levelProgress.postValue(progress);

            } catch (Exception e) {
                android.util.Log.e("GamificationVM", "Error loading achievements", e);
            }
        }).start();
    }

    /**
     * Desbloquear un logro (con validación)
     * @param achievementKey Clave del logro
     */
    public void unlockAchievement(@NonNull String achievementKey) {
        new Thread(() -> {
            try {
                // Verificar si ya está desbloqueado
                List<Achievement> achievements = _allAchievements.getValue();
                if (achievements != null) {
                    for (Achievement achievement : achievements) {
                        if (achievement.getKey().equals(achievementKey) && !achievement.isUnlocked()) {
                            // Desbloquear
                            achievementController.unlockAchievement(achievementKey);

                            // Encontrar el logro para emitir evento
                            Achievement unlocked = new Achievement();
                            unlocked.setKey(achievementKey);
                            _achievementUnlocked.postValue(unlocked);

                            // Recargar datos
                            loadAchievements();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("GamificationVM", "Error unlocking achievement", e);
            }
        }).start();
    }

    /**
     * Verificar y desbloquear logros automáticamente
     * Se llama después de cada acción significativa del usuario
     */
    public void checkAndUnlockAchievements() {
        new Thread(() -> {
            try {
                achievementController.checkAndUnlockAchievements();
                loadAchievements();
            } catch (Exception e) {
                android.util.Log.e("GamificationVM", "Error checking achievements", e);
            }
        }).start();
    }

    /**
     * Incrementar racha de registro de gastos
     */
    public void recordActivity() {
        streakController.recordActivity();
    }

    /**
     * Incrementar racha de presupuesto
     */
    public void recordBudgetDay() {
        streakController.recordBudgetDay();
    }

    // ===== GETTERS =====

    public LiveData<List<Achievement>> getAllAchievements() {
        return _allAchievements;
    }

    public LiveData<Integer> getTotalPoints() {
        return _totalPoints;
    }

    public LiveData<String> getUserLevel() {
        return _userLevel;
    }

    public LiveData<Integer> getLevelProgress() {
        return _levelProgress;
    }

    public LiveData<Achievement> getAchievementUnlocked() {
        return _achievementUnlocked;
    }

    public LiveData<Streak> getRecordingStreak() {
        return recordingStreak;
    }

    /**
     * Obtiene los logros recientes desbloqueados
     * Usado para el preview en la pantalla principal
     */
    public LiveData<List<Achievement>> getRecentUnlockedAchievements() {
        return recentUnlockedAchievements;
    }

    public LiveData<Streak> getBudgetStreak() {
        return budgetStreak;
    }
}
