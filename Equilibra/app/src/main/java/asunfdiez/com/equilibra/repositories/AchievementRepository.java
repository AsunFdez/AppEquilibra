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

/**
 * Repository para gestionar la lógica de Logros (Achievements)
 * Intermedia entre ViewModel y Room DAOs
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
    public final MutableLiveData<Achievement> newlyUnlockedAchievement = new MutableLiveData<>();


    /**
     * Constructor del Repository
     */
    public AchievementRepository(Context context) {
        this.achievementDao = AppDatabase.getInstance(context).achievementDao();
        this.executorService = Executors.newSingleThreadExecutor();
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

    /**
     * Actualiza un logro en la base de datos
     * @param achievement Logro a actualizar
     */
    public void updateAchievement(Achievement achievement) {
        executorService.execute(() -> {
            achievementDao.updateAchievement(achievement);
        });
    }

    /**
     * Obtiene un logro por su título
     * @param string Título del logro
     * @return Logro
     */
    public Achievement getAchievementByTitle(String string) {
        return achievementDao.getAchievementByTitle(string);
    }

    /**
     * Comprueba si un logro está desbloqueado por su clave
     * @param achievementKey Clave del logro
     * @return true si está desbloqueado, false en caso contrario
     */
    public boolean isAchievementUnlocked(String achievementKey) {
        return achievementDao.isAchievementUnlocked(achievementKey);
    }
    /**
     * Desbloquea un logro por su clave
     * @param achievementKey Clave del logro
     * @param timestamp Timestamp de desbloqueo
     */

    public void unlockAchievement(String achievementKey, long timestamp) {
        executorService.execute(() -> {
            achievementDao.unlockAchievement(achievementKey, timestamp);
        });
    }

    /**
     * Obtiene un logro por su clave
     * @param achievementKey Clave del logro
     * @return Logro
     */
    public Achievement getAchievementByKey(String achievementKey) {
        return achievementDao.getAchievementByKey(achievementKey);
    }

    /**
     * Obtiene todos los logros como LiveData
     * @return LiveData de la lista de logros
     */
    public LiveData<List<Achievement>> getAllAchievements() {
        return allAchievements;
    }

    /**
     * Obtiene solo logros desbloqueados
     * @return LiveData de la lista de logros desbloqueados
     */
    public LiveData<List<Achievement>> getUnlockedAchievements() {
        return achievementDao.getUnlockedAchievements();
    }

    /**
     * Obtiene solo logros bloqueados
     * @return LiveData de la lista de logros bloqueados
     */
    public LiveData<List<Achievement>> getLockedAchievements() {
        return achievementDao.getLockedAchievements();
    }

    /**
     * Obtiene el total de puntos acumulados
     * @return LiveData del total de puntos
     */
    public LiveData<Integer> getTotalPoints() {
        return totalPoints;
    }

    /**
     * Obtiene el conteo de logros desbloqueados
     * @return LiveData del conteo de logros desbloqueados
     */
    public LiveData<Integer> getUnlockedCount() {
        return unlockedCount;
    }

    /**
     * Obtiene el total de logros
     * @return LiveData del total de logros
     */
    public LiveData<Integer> getTotalAchievements() {
        return totalAchievements;
    }

    /**
     * Obtiene todos los logros de forma síncrona
     * @return Lista de logros
     */
    public List<Achievement> getAllAchievementsSync() {
        return achievementDao.getAllAchievementsSync();
    }

    /**
     * Obtiene los puntos totales de forma síncrona
     * @return Total de puntos
     */
    public int getTotalPointsSync() {
        return achievementDao.getTotalPointsSync();
    }

    /**
     * Obtiene los puntos máximos posibles
     * @return LiveData de los puntos máximos posibles
     */
    public LiveData<Integer> getMaxPossiblePoints() {
        return maxPossiblePoints;
    }

    /**
     * LiveData para observar cuando se desbloquea un nuevo logro
     * @return LiveData del logro desbloqueado
     */
    public LiveData<Achievement> getNewlyUnlockedAchievement() {
        return newlyUnlockedAchievement;
    }

    /**
     * Obtiene logros recientes
     * @param limit Cantidad máxima de logros
     */
    public LiveData<List<Achievement>> getRecentUnlocked(int limit) {
        return achievementDao.getRecentUnlocked(limit);
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
