package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import asunfdiez.com.equilibra.controllers.AchievementController;
import asunfdiez.com.equilibra.controllers.StreakController;
import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.repositories.AchievementRepository;
import asunfdiez.com.equilibra.repositories.UserXPRepository;

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
    private final MediatorLiveData<Achievement> achievementUnlocked = new MediatorLiveData<>();

    /**
     * Rachas del usuario
     */
    private final LiveData<Streak> recordingStreak;
    private final LiveData<Streak> budgetStreak;

    /**
     * Event: Hito de racha alcanzado (7 registros o 7 días presupuesto)
     */
    private final MutableLiveData<StreakMilestone> _streakMilestoneReached = new MutableLiveData<>();

    /**
     * Clase auxiliar para definir hitos de racha
     */
    public static class StreakMilestone {
        public enum Type { RECORDING, BUDGET }
        public final Type type;
        public final int value;

        public StreakMilestone(Type type, int value) {
            this.type = type;
            this.value = value;
        }
    }

    /**
     * Información de progreso combinado para la pantalla principal
     */
    private final MediatorLiveData<MainProgressInfo> mainProgressInfo = new MediatorLiveData<>();

    /**
     * Clase auxiliar para agrupar la información de progreso
     */
    public static class MainProgressInfo {
        public final String title;
        public final int current;
        public final int goal;
        public final String progressText;

        public MainProgressInfo(String title, int current, int goal, String progressText) {
            this.title = title;
            this.current = current;
            this.goal = goal;
            this.progressText = progressText;
        }
    }

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

        // Configurar el progreso combinado
        setupMainProgressInfo(application);

        // Obtener logros recientes (últimos 4)
        this.recentUnlockedAchievements = achievementController.getRecentUnlocked(4);

        // Configurar fuentes para logros desbloqueados
        achievementUnlocked.addSource(achievementController.getNewlyUnlockedAchievement(), achievementUnlocked::setValue);
        achievementUnlocked.addSource(_achievementUnlocked, achievementUnlocked::setValue);

        // Cargar datos iniciales
        loadAchievements();
        userProfile.observeForever(profile -> loadAchievements());

        UserXPRepository userXPRepo = new UserXPRepository(application);
        LiveData<UserXP> userXpLiveData = userXPRepo.getUserXPAsync(1);
        userXpLiveData.observeForever(userXP -> {
            if (userXP != null) {
                _totalPoints.postValue(userXP.getTotalXP());
                _userLevel.postValue("Nivel " + userXP.getCurrentLevel());
                _levelProgress.postValue((int) userXP.getLevelProgress());
            }
        });
    }

    private Integer previousRecDays = null;
    private Integer previousBudDays = null;

    /**
     * Configura la lógica de decisión para el progreso principal.
     * 1. Si los gastos semanales < 7, mostrar progreso de registros.
     * 2. Si ya son >= 7, mostrar progreso de días bajo presupuesto.
     */
    private void setupMainProgressInfo(Application app) {
        Runnable updateLogic = () -> {
            Streak rec = recordingStreak.getValue();
            Streak bud = budgetStreak.getValue();

            if (rec != null) {
                int currentRec = rec.getConsecutiveDays();
                // Detectar hito de 7 registros
                if (previousRecDays != null && previousRecDays < 7 && currentRec == 7) {
                    _streakMilestoneReached.postValue(new StreakMilestone(StreakMilestone.Type.RECORDING, 7));
                }
                previousRecDays = currentRec;

                if (rec.getConsecutiveDays() < 7) {
                    mainProgressInfo.setValue(new MainProgressInfo(
                            app.getString(asunfdiez.com.equilibra.R.string.weekly_expenses_title),
                            rec.getConsecutiveDays(),
                            7,
                            app.getString(asunfdiez.com.equilibra.R.string.week_progress, rec.getConsecutiveDays(), 7)
                    ));
                } else {
                    int budDays = (bud != null) ? bud.getConsecutiveDays() : 0;
                    
                    // Detectar hito de 7 días bajo presupuesto
                    if (previousBudDays != null && previousBudDays < 7 && budDays == 7) {
                        _streakMilestoneReached.postValue(new StreakMilestone(StreakMilestone.Type.BUDGET, 7));
                    }
                    previousBudDays = budDays;

                    mainProgressInfo.setValue(new MainProgressInfo(
                            app.getString(asunfdiez.com.equilibra.R.string.weekly_budget_title),
                            budDays,
                            7,
                            app.getString(asunfdiez.com.equilibra.R.string.week_budget_progress, budDays, 7)
                    ));
                }
            }
        };

        mainProgressInfo.addSource(recordingStreak, streak -> updateLogic.run());
        mainProgressInfo.addSource(budgetStreak, streak -> updateLogic.run());
    }

    /**
     * Cargar todos los logros
     */
    private void loadAchievements() {
        new Thread(() -> {
            try {
                List<Achievement> achievements = achievementController.getAllAchievementsSync();
                _allAchievements.postValue(achievements);
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
        return achievementUnlocked;
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

    public LiveData<MainProgressInfo> getMainProgressInfo() {
        return mainProgressInfo;
    }

    public LiveData<StreakMilestone> getStreakMilestoneReached() {
        return _streakMilestoneReached;
    }

    public void consumeStreakMilestoneEvent() {
        _streakMilestoneReached.postValue(null);
    }

    public void consumeAchievementUnlockedEvent() {
        achievementUnlocked.setValue(null);
        _achievementUnlocked.setValue(null);
        AchievementRepository.consumeNewlyUnlockedAchievement();
    }
}
