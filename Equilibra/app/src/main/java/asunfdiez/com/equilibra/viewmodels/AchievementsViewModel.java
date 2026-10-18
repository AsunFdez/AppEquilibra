package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.AchievementController;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

/**
 * ViewModel encargado de la gestión de datos para la pantalla de logros.
 * 
 * <p>Maneja la lógica para filtrar logros (todos, desbloqueados, bloqueados) y calcula 
 * estadísticas de progreso como puntos totales y porcentaje de logros completados. 
 * Utiliza LiveData para proporcionar actualizaciones reactivas a la interfaz de usuario.</p>
 */
public class AchievementsViewModel extends AndroidViewModel {

    // Controlador que encapsula la lógica de acceso a datos de logros
    private final AchievementController controller;

    // Listas de logros observadas desde el repositorio
    private final LiveData<List<Achievement>> allAchievements;
    private final LiveData<List<Achievement>> unlockedAchievements;
    private final LiveData<List<Achievement>> lockedAchievements;
    
    // Estadísticas de puntos y conteos
    private final LiveData<Integer> totalPoints;
    private final LiveData<Integer> unlockedCount;
    private final LiveData<Integer> totalAchievements;
    private final LiveData<Integer> maxPossiblePoints;

    // Estado del filtro de visualización seleccionado por el usuario
    private final MutableLiveData<FilterType> currentFilter = new MutableLiveData<>(FilterType.ALL);

    // Lista resultante tras aplicar el filtro seleccionado
    private final MediatorLiveData<List<Achievement>> filteredAchievements = new MediatorLiveData<>();

    // LiveData para el progreso calculado (%)
    private final MediatorLiveData<Integer> progressPercentage = new MediatorLiveData<>();

    // LiveData para mostrar/ocultar empty state
    private final MediatorLiveData<Boolean> showEmptyState = new MediatorLiveData<>();

    // LiveData para el mensaje de empty state
    private final MutableLiveData<String> emptyStateMessage = new MutableLiveData<>();

    // LiveData para eventos (mostrar diálogo de nuevo logro)
    private final LiveData<Achievement> newlyUnlockedAchievement;

    // LiveData para estado de carga
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    /**
     * Tipos de filtro para mostrar logros
     */
    public enum FilterType {
        ALL,        // Todos los logros
        UNLOCKED,   // Solo desbloqueados
        LOCKED      // Solo bloqueados
    }

    /**
     * Constructor del ViewModel
     */
    public AchievementsViewModel(@NonNull Application application) {
        super(application);

        // Inicializar controller
        this.controller = new AchievementController(application);

        // Obtener LiveData
        this.allAchievements = controller.getAllAchievements();
        this.unlockedAchievements = controller.getUnlockedAchievements();
        this.lockedAchievements = controller.getLockedAchievements();
        
        UserProfileRepository userProfileRepo = new UserProfileRepository(application);
        LiveData<Integer> achPoints = controller.getTotalPoints();
        LiveData<UserProfile> userProfileLiveData = userProfileRepo.getUserProfile();

        MediatorLiveData<Integer> combinedPoints = new MediatorLiveData<>();
        Runnable updatePoints = () -> {
            Integer ach = achPoints.getValue();
            UserProfile profile = userProfileLiveData.getValue();
            int a = ach != null ? ach : 0;
            int x = profile != null ? profile.getXp() : 0;
            combinedPoints.setValue(a + x);
        };
        combinedPoints.addSource(achPoints, p -> updatePoints.run());
        combinedPoints.addSource(userProfileLiveData, p -> updatePoints.run());
        this.totalPoints = combinedPoints;

        this.unlockedCount = controller.getUnlockedCount();
        this.totalAchievements = controller.getTotalAchievements();
        this.maxPossiblePoints = controller.getMaxPossiblePoints();
        this.newlyUnlockedAchievement = controller.getNewlyUnlockedAchievement();

        // Configurar el filtrado de logros
        setupFilteredAchievements();

        // Configurar el cálculo de progreso
        setupProgressCalculation();

        // Configurar el empty state
        setupEmptyState();

        // Verificar logros al iniciar
        checkAchievements();
    }
    /**
     * Configura el LiveData filtrado según el tipo de filtro seleccionado
     */
    private void setupFilteredAchievements() {
        filteredAchievements.addSource(currentFilter, filter -> updateFilteredAchievements());
        filteredAchievements.addSource(allAchievements, achievements -> updateFilteredAchievements());
        filteredAchievements.addSource(unlockedAchievements, achievements -> updateFilteredAchievements());
        filteredAchievements.addSource(lockedAchievements, achievements -> updateFilteredAchievements());
    }
    /**
     * Actualiza los logros filtrados según el filtro actual
     */
    private void updateFilteredAchievements() {
        FilterType filter = currentFilter.getValue();
        if (filter == null) {
            filter = FilterType.ALL;
        }

        List<Achievement> achievements;
        switch (filter) {
            case UNLOCKED:
                achievements = unlockedAchievements.getValue();
                emptyStateMessage.setValue(getApplication().getString(R.string.empty_unlocked));
                break;
            case LOCKED:
                achievements = lockedAchievements.getValue();
                emptyStateMessage.setValue(getApplication().getString(R.string.empty_locked));
                break;
            case ALL:
            default:
                achievements = allAchievements.getValue();
                emptyStateMessage.setValue(getApplication().getString(R.string.empty_all));
                break;
        }

        filteredAchievements.setValue(achievements != null ? achievements : new ArrayList<>());
    }

    /**
     * Configura el cálculo automático del progreso
     */
    private void setupProgressCalculation() {
        progressPercentage.addSource(unlockedCount, count -> calculateProgress());
        progressPercentage.addSource(totalAchievements, count -> calculateProgress());
    }

    /**
     * Calcula el porcentaje de progreso
     */
    private void calculateProgress() {
        Integer unlocked = unlockedCount.getValue();
        Integer total = totalAchievements.getValue();

        if (unlocked != null && total != null && total > 0) {
            int percentage = (int) ((unlocked * 100.0) / total);
            progressPercentage.setValue(percentage);
        } else {
            progressPercentage.setValue(0);
        }
    }

    /**
     * Configura el estado vacío
     */
    private void setupEmptyState() {
        showEmptyState.addSource(filteredAchievements, achievements -> {
            showEmptyState.setValue(achievements == null || achievements.isEmpty());
        });
    }

    // ========== GETTERS PARA LIVEDATA ==========

    /**
     * Obtiene los logros filtrados
     */
    public LiveData<List<Achievement>> getFilteredAchievements() {
        return filteredAchievements;
    }

    /**
     * Obtiene todos los logros (sin filtrar)
     */
    public LiveData<List<Achievement>> getAllAchievements() {
        return allAchievements;
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
     * Obtiene el porcentaje de progreso
     */
    public LiveData<Integer> getProgressPercentage() {
        return progressPercentage;
    }

    /**
     * Obtiene el estado de empty state
     */
    public LiveData<Boolean> getShowEmptyState() {
        return showEmptyState;
    }

    /**
     * Obtiene el mensaje de empty state
     */
    public LiveData<String> getEmptyStateMessage() {
        return emptyStateMessage;
    }

    /**
     * Obtiene el filtro actual
     */
    public LiveData<FilterType> getCurrentFilter() {
        return currentFilter;
    }

    /**
     * Obtiene el evento de nuevo logro desbloqueado
     */
    public LiveData<Achievement> getNewlyUnlockedAchievement() {
        return newlyUnlockedAchievement;
    }

    /**
     * Obtiene el estado de carga
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Obtiene los puntos máximos posibles
     */
    public LiveData<Integer> getMaxPossiblePoints() {
        return maxPossiblePoints;
    }

    /**
     * LiveData transformado para mostrar el progreso como texto
     * Ejemplo: "5 de 15 logros"
     */
    public LiveData<String> getProgressText() {
        return Transformations.map(unlockedCount, unlocked -> {
            Integer total = totalAchievements.getValue();
            if (total == null) total = 0;
            return unlocked + " de " + total + " logros";
        });
    }

    /**
     * LiveData transformado para mostrar los puntos como texto
     * Ejemplo: "350 pts"
     */
    public LiveData<String> getPointsText() {
        return Transformations.map(totalPoints, points -> {
            if (points == null) points = 0;
            return points + " pts";
        });
    }

    /**
     * LiveData transformado para mostrar el nombre del nivel del usuario según XP (puntos totales)
     * 0-500 XP: Ahorrador Novato
     * 501-2000 XP: Guardián del Tesoro
     * >2000 XP: Maestro Equilibra
     */
    public LiveData<String> getUserLevelName() {
        return Transformations.map(totalPoints, points -> {
            int p = points != null ? points : 0;
            if (p <= 500) {
                return getApplication().getString(R.string.level_novice);
            } else if (p <= 2000) {
                return getApplication().getString(R.string.level_guardian);
            } else {
                return getApplication().getString(R.string.level_master);
            }
        });
    }

    // ========== MÉTODOS DE ACCIÓN ==========

    /**
     * Cambia el filtro de visualización
     */
    public void setFilter(FilterType filter) {
        if (filter != null && filter != currentFilter.getValue()) {
            currentFilter.setValue(filter);
        }
    }

    /**
     * Verifica y desbloquea logros pendientes
     */
    public void checkAchievements() {
        isLoading.setValue(true);
        controller.checkAndUnlockAchievements();

        // Simular delay para mostrar loading
        new android.os.Handler().postDelayed(() ->
                isLoading.setValue(false), 500);
    }

    /**
     * Desbloquea un logro manualmente (para testing)
     */
    public void unlockAchievement(String achievementKey) {
        controller.unlockAchievement(achievementKey);
    }

    /**
     * Resetea todos los logros (para testing)
     */
    public void resetAllAchievements() {
        controller.resetAllAchievements();
    }


    // ========== LIMPIEZA ==========

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
