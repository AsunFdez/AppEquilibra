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

@Dao
public interface StreakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStreak(Streak streak);

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
}
