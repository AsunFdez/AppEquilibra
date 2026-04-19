package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la tabla de Logros (Achievements)
 * Proporciona métodos para acceder y manipular logros en la base de datos
 *
 */
@Dao
public interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievement(Achievement achievement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListOfAchievements(List<Achievement> achievements);

    @Update
    void updateAchievement(Achievement achievement);
    @Delete
    void deleteAchievement(Achievement achievement);

    @Query("SELECT * FROM "+ Constants.TABLE_ACHIEVEMENTS+" WHERE "+Constants.COLUMN_ACHIEVEMENT_TITLE+" = :title")
    Achievement getAchievementByTitle(String title);

    /**
     * Obtiene todos los logros ordenados por estado (desbloqueados primero)
     * y luego por puntos (mayor a menor)
     */
    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " ORDER BY unlocked DESC, points DESC")
    LiveData<List<Achievement>> getAllAchievements();

    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " ORDER BY unlocked DESC, points DESC")
    List<Achievement> getAllAchievementsSync();

    /**
     * Obtiene solo los logros desbloqueados
     */
    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE unlocked = 1 ORDER BY unlockedAt DESC")
    LiveData<List<Achievement>> getUnlockedAchievements();

    /**
     * Obtiene solo los logros bloqueados
     */
    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE unlocked = 0 ORDER BY points ASC")
    LiveData<List<Achievement>> getLockedAchievements();

    /**
     * Obtiene un logro específico por su key
     */
    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE id = :key LIMIT 1")
    Achievement getAchievementByKey(String key);

    /**
     * Obtiene un logro por su key como LiveData
     */
    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE id = :key LIMIT 1")
    LiveData<Achievement> getAchievementByKeyLive(String key);

    /**
     * Desbloquea un logro específico por su key
     */
    @Query("UPDATE " + Constants.TABLE_ACHIEVEMENTS +
            " SET unlocked = 1, unlockedAt = :timestamp WHERE id = :key")
    void unlockAchievement(String key, long timestamp);

    /**
     * Cuenta el total de logros
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_ACHIEVEMENTS)
    LiveData<Integer> getTotalAchievements();

    /**
     * Cuenta los logros desbloqueados
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE unlocked = 1")
    LiveData<Integer> getUnlockedCount();

    /**
     * Calcula los puntos totales acumulados
     */
    @Query("SELECT COALESCE(SUM(points), 0) FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE unlocked = 1")
    LiveData<Integer> getTotalPoints();

    @Query("SELECT COALESCE(SUM(points), 0) FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE unlocked = 1")
    int getTotalPointsSync();

    /**
     * Obtiene los puntos máximos posibles
     */
    @Query("SELECT COALESCE(SUM(points), 0) FROM " + Constants.TABLE_ACHIEVEMENTS)
    LiveData<Integer> getMaxPossiblePoints();

    /**
     * Verifica si un logro está desbloqueado
     */
    @Query("SELECT unlocked FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE id = :key LIMIT 1")
    boolean isAchievementUnlocked(String key);

    /**
     * Elimina todos los logros (útil para testing)
     */
    @Query("DELETE FROM " + Constants.TABLE_ACHIEVEMENTS)
    void deleteAll();

    /**
     * Obtiene los últimos N logros desbloqueados
     */
    @Query("SELECT * FROM " + Constants.TABLE_ACHIEVEMENTS +
            " WHERE unlocked = 1 ORDER BY unlockedAt DESC LIMIT :limit")
    LiveData<List<Achievement>> getRecentUnlocked(int limit);


    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_ACHIEVEMENTS)
    int getAchievementCountSync();
}
