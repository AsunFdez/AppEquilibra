package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserProfile userProfile);

    @Query("SELECT * FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    LiveData<UserProfile> getUserProfile();
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

    @Query("SELECT "+Constants.COLUMN_USER_WEEK_PROGRESS+" FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    int getWeeksUnderBudget();


    @Query("SELECT "+Constants.COLUMN_USER_MONTHLY_INCOME+" FROM "+Constants.TABLE_USER_PROFILE+" LIMIT 1")
    double getUserMonthlyIncome();
}
