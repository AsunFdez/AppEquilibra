package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la gestión de las rachas de uso y actividad del usuario.
 * 
 * <p>Proporciona acceso a la información sobre días consecutivos de uso, 
 * permitiendo su actualización y consulta reactiva por tipo de racha para 
 * alimentar el sistema de gamificación.</p>
 */
@Dao
public interface StreakDao {

    /**
     * Inserta una nueva racha o actualiza una existente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStreak(Streak streak);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListOfStreak (List<Streak> streak);

    @Update
    void updateStreak(Streak streak);

    @Delete
    void deleteStreak(Streak streak);

    @Query("SELECT * FROM "+ Constants.TABLE_STREAK+ " WHERE userId = :userId")
    Streak getStreakByUserId(long userId);

    @Query("DELETE FROM " + Constants.TABLE_STREAK)
    void deleteAll();


    @Query("SELECT * FROM " + Constants.TABLE_STREAK + " WHERE userId = :userId AND type = :type")
    LiveData<Streak> getStreakByType(long userId, String type);

    @Query("SELECT * FROM " + Constants.TABLE_STREAK + " WHERE userId = :userId AND type = :type")
    Streak getStreakByTypeSync(long userId, String type);

    @Query("SELECT * FROM " + Constants.TABLE_STREAK + " WHERE userId = :userId")
    LiveData<List<Streak>> getUserStreaks(long userId);

    @Query("SELECT * FROM " + Constants.TABLE_STREAK)
    List<Streak> getAllStreaks();
}
