package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.Achievement;

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
    @Query("SELECT * FROM achievements")
    List<Achievement> getAllAchievements();
}
