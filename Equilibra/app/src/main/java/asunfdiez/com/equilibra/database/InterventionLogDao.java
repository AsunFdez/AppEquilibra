package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface InterventionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInterventionLog(InterventionLog log);

    @Update
    void updateInterventionLog(InterventionLog log);

    @Delete
    void deleteInterventionLog(InterventionLog log);

    /**
     * Obtiene los registros de intervención recientes de un usuario
     */
    @Query ("SELECT * FROM "+ Constants.TABLE_INTERVENTION_LOG+" WHERE "+Constants.COLUMN_INTERVENTION_USER_ID +"= :userId ORDER BY "+Constants.COLUMN_INTERVENTION_TIMESTAMP+" DESC LIMIT :limit")
    public List<InterventionLog> getRecentInterventions(long userId, int limit);




}
