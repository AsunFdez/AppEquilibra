package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la gestión de los puntos de experiencia (XP) y niveles del usuario.
 * 
 * <p>Mantiene el registro persistente del progreso de gamificación, permitiendo 
 * la actualización de los puntos acumulados y la consulta de la situación 
 * actual del usuario en el sistema de niveles.</p>
 */
@Dao
public interface UserXPDao {

    /**
     * Registra el estado inicial de XP para un usuario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserXP(UserXP userXP);

    /**
     * Actualiza el progreso de XP acumulado.
     */
    @Update
    void updateUserXP(UserXP userXP);

    @Delete
    void deleteUserXP(UserXP userXP);

    @Query("SELECT * FROM "+ Constants.TABLE_USER_XP+" WHERE userId = :userId")
    UserXP getUserXPByUserId(long userId);

    @Query("SELECT * FROM "+ Constants.TABLE_USER_XP+" WHERE userId = :userId LIMIT 1")
    androidx.lifecycle.LiveData<UserXP> getUserXPAsync(long userId);


}
