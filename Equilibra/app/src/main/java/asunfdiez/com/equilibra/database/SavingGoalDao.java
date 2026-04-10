package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface SavingGoalDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateSavingGoal(SavingGoal savingGoal);

    @Update
    void updateSavingGoal(SavingGoal savingGoal);

    @Delete
    void deleteSavingGoal(SavingGoal savingGoal);

    @Query("DELETE FROM " + Constants.TABLE_SAVING_GOALS)
    void deleteAll();

    @Query("SELECT * FROM "+ Constants.TABLE_SAVING_GOALS+" WHERE userId = :userId and completed = 0 LIMIT 1")
    SavingGoal getFirstSavingGoalActiveByUserId(long userId);



}
