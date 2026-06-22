package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la gestión del perfil único de usuario.
 * 
 * <p>Como la aplicación está diseñada para un solo perfil local, este DAO 
 * proporciona métodos para acceder a ese registro único de forma reactiva 
 * o síncrona, así como para actualizar sus campos específicos.</p>
 */
@Dao
public interface UserProfileDao {
    /**
     * Guarda o sobrescribe el perfil del usuario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserProfile userProfile);

    /**
     * Recupera el perfil completo del usuario como un observable LiveData.
     */
    @Query("SELECT * FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    LiveData<UserProfile> getUserProfile();
    
    /**
     * Recupera únicamente el nombre del usuario de forma reactiva.
     */
    @Query("SELECT "+Constants.COLUMN_USER_NAME+" FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    LiveData<String>getUserName();

    @Query("SELECT * FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    UserProfile getUserProfileSync();

    @Query("DELETE FROM "+Constants.TABLE_USER_PROFILE)
    void deleteAll();

    @Query("UPDATE "+Constants.TABLE_USER_PROFILE+" SET "+Constants.COLUMN_USER_WEEK_PROGRESS+" = "+Constants.COLUMN_USER_WEEK_PROGRESS+" + 1")
    void incrementWeekProgress();

    @Query("UPDATE "+Constants.TABLE_USER_PROFILE+" SET "+Constants.COLUMN_USER_WEEK_PROGRESS+" = CASE WHEN "+Constants.COLUMN_USER_WEEK_PROGRESS+" > 0 THEN "+Constants.COLUMN_USER_WEEK_PROGRESS+" - 1 ELSE 0 END")
    void decrementWeekProgress();

    @Query("UPDATE "+Constants.TABLE_USER_PROFILE+" SET "+Constants.COLUMN_USER_DAY_COUNT+" = "+Constants.COLUMN_USER_DAY_COUNT+" + 1")
    void incrementDayCount();

    @Query("UPDATE "+Constants.TABLE_USER_PROFILE+" SET lastConnectionTimestamp = :timestamp")
    void updateLastConnection(long timestamp);

    @Query("UPDATE "+Constants.TABLE_USER_PROFILE+" SET weekCount = weekCount + 1, "+Constants.COLUMN_USER_WEEK_PROGRESS+" = 0, lastResetTimestamp = :timestamp")
    void resetWeeklyProgress(long timestamp);

    @Query("UPDATE "+Constants.TABLE_USER_PROFILE+" SET "+Constants.COLUMN_USER_WEEK_PROGRESS+" = :progress")
    void updateWeekProgress(int progress);

    @Query("SELECT "+Constants.COLUMN_USER_WEEK_PROGRESS+" FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    int getWeeksUnderBudget();


    @Query("SELECT "+Constants.COLUMN_USER_MONTHLY_INCOME+" FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    double getUserMonthlyIncome();

    @Query("SELECT EXISTS (SELECT 1 FROM "+Constants.TABLE_USER_PROFILE+" WHERE "+Constants.COLUMN_USER_NAME+" IS NOT NULL AND "+Constants.COLUMN_USER_NAME+" != '' LIMIT 1)")
    LiveData<Boolean> hasProfile();

}
