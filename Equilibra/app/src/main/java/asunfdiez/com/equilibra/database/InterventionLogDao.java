package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la gestión del historial de intervenciones realizadas por el sistema.
 * 
 * <p>Permite registrar cada vez que se presenta una sugerencia o alerta al usuario, 
 * facilitando el análisis posterior de la efectividad de las intervenciones 
 * y el seguimiento de los consejos proporcionados.</p>
 */
@Dao
public interface InterventionLogDao {

    /**
     * Registra un nuevo evento de intervención en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInterventionLog(InterventionLog log);

    @Update
    void updateInterventionLog(InterventionLog log);

    @Delete
    void deleteInterventionLog(InterventionLog log);

    @Query("DELETE FROM " + Constants.TABLE_INTERVENTION_LOG)
    void deleteAll();


    /**
     * Obtiene los registros de intervención recientes de un usuario
     */
    @Query ("SELECT * FROM "+ Constants.TABLE_INTERVENTION_LOG+" WHERE "+Constants.COLUMN_INTERVENTION_USER_ID +"= :userId ORDER BY "+Constants.COLUMN_INTERVENTION_TIMESTAMP+" DESC LIMIT :limit")
    public List<InterventionLog> getRecentInterventions(long userId, int limit);

    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.TABLE_INTERVENTION_LOG + ")")
    LiveData<Boolean> observeHasAnyInterventionLog();

    @Query("SELECT * FROM " + Constants.TABLE_INTERVENTION_LOG
            + " ORDER BY " + Constants.COLUMN_INTERVENTION_TIMESTAMP + " DESC")
    LiveData<List<InterventionLog>> observeAllInterventionLogs();

    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.TABLE_INTERVENTION_LOG
            + " WHERE " + Constants.COLUMN_INTERVENTION_USER_ID + " = :userId)")
    LiveData<Boolean> observeHasLogsForUser(long userId);

    @Query("SELECT * FROM " + Constants.TABLE_INTERVENTION_LOG
            + " WHERE " + Constants.COLUMN_INTERVENTION_USER_ID + " = :userId"
            + " ORDER BY " + Constants.COLUMN_INTERVENTION_TIMESTAMP + " DESC")
    LiveData<List<InterventionLog>> observeByUserId(long userId);


}
