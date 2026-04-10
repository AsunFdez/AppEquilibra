package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface StreakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStreak(Streak streak);

    @Update
    void updateStreak(Streak streak);

    @Delete
    void deleteStreak(Streak streak);

    @Query("SELECT * FROM "+ Constants.TABLE_STREAK+ " WHERE userId = :userId")
    Streak getStreakByUserId(long userId);

    @Query("DELETE FROM " + Constants.TABLE_STREAK)
    void deleteAll();



}
