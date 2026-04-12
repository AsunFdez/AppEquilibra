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
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.repositories.AchievementRepository;

/**
 * AchievementsViewModel - ViewModel para AchievementsActivity
 * Maneja la lógica de negocio y estados con LiveData
 */
public class AchievementsViewModel extends AndroidViewModel {

    // Repository
    private final AchievementRepository repository;

    // LiveData observables desde el Repository
    private final LiveData<List<Achievement>> allAchievements;
    private final LiveData<List<Achievement>> unlockedAchievements;
    private final LiveData<List<Achievement>> lockedAchievements;
    private final LiveData<Integer> totalPoints;
    private final LiveData<Integer> unlockedCount;
    private final LiveData<Integer> totalAchievements;
    private final LiveData<Integer> maxPossiblePoints;

    // LiveData para el filtro seleccionado
    private final MutableLiveData<FilterType> currentFilter = new MutableLiveData<>(FilterType.ALL);

    // LiveData combinado según el filtro
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

        // Inicializar repository
        this.repository = new AchievementRepository(application);

        // Obtener LiveData del repository
        this.allAchievements = repository.getAllAchievements();
        this.unlockedAchievements = repository.getUnlockedAchievements();
        this.lockedAchievements = repository.getLockedAchievements();
        this.totalPoints = repository.getTotalPoints();
        this.unlockedCount = repository.getUnlockedCount();
        this.totalAchievements = repository.getTotalAchievements();
        this.maxPossiblePoints = repository.getMaxPossiblePoints();
        this.newlyUnlockedAchievement = repository.getNewlyUnlockedAchievement();

        // Configurar el filtrado de logros
        setupFilteredAchievements();

        // Configurar el cálculo de progreso
        setupProgressCalculation();

        // Configurar el empty state
        setupEmptyState();

        // Verificar logros al iniciar
        checkAchievements();
    }

    // ========== CONFIGURACIÓN DE MEDIADORES ==========

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
        repository.checkAndUnlockAchievements();

        // Simular delay para mostrar loading
        new android.os.Handler().postDelayed(() ->
                isLoading.setValue(false), 500);
    }

    /**
     * Desbloquea un logro manualmente (para testing)
     */
    public void unlockAchievement(String achievementKey) {
        repository.unlockAchievement(achievementKey);
    }

    /**
     * Resetea todos los logros (para testing)
     */
    public void resetAllAchievements() {
        repository.resetAllAchievements();
    }

    // ========== MÉTODOS DE UTILIDAD ==========

    /**
     * Obtiene el icono de un logro según su key
     */
    public int getAchievementIcon(String achievementKey) {
        // TODO: Mapear keys a recursos drawable
        // Por ahora retorna un icono genérico
        return android.R.drawable.btn_star_big_on;
    }

    /**
     * Obtiene el color de un logro según si está desbloqueado
     */
    public int getAchievementColor(boolean unlocked) {
        // Verde si está desbloqueado, gris si está bloqueado
        return unlocked ?
                android.R.color.holo_green_light :
                android.R.color.darker_gray;
    }

    // ========== LIMPIEZA ==========

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.close();
    }
}
