package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.Challenge;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la gestión de desafíos financieros gamificados.
 * 
 * <p>Contiene métodos para registrar el progreso del usuario en los diferentes retos, 
 * así como consultas para listar desafíos activos, completados o vencidos dentro 
 * de un rango de fechas determinado.</p>
 */
@Dao
public interface ChallengeDao {
    /**
     * Registra o actualiza un desafío en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChallenge(Challenge challenge);

    @Update
    void updateChallenge(Challenge challenge);

    @Delete
    void deleteChallenge(Challenge challenge);

    @Query("DELETE FROM " + Constants.TABLE_CHALLENGES)
    void deleteAll();

    /**
     * Obtiene todos los desafíos de un usuario
     */
    @Query("SELECT * FROM "+ Constants.TABLE_CHALLENGES+" WHERE "+Constants.COLUMN_CHALLENGE_USER_ID+" = :userId")
    List<Challenge> getAllChallengesByUserId(long userId);

    /**
     * Obtiene todos los desafíos activos de un usuario
     */
    @Query("SELECT * FROM "+ Constants.TABLE_CHALLENGES+" WHERE "+Constants.COLUMN_CHALLENGE_USER_ID+" = :userId AND "+ Constants.COLUMN_CHALLENGE_COMPLETED +"= 0 ORDER BY " + Constants.COLUMN_CHALLENGE_START_DATE + " DESC")
    public List<Challenge> getActiveChallengesByUser(long userId);

}
