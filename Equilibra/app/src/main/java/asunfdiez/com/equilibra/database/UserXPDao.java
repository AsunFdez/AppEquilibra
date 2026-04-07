package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface UserXPDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserXP(UserXP userXP);

    @Update
    void updateUserXP(UserXP userXP);

    @Delete
    void deleteUserXP(UserXP userXP);

    @Query("SELECT * FROM "+ Constants.TABLE_USER_XP+" WHERE userId = :userId")
    UserXP getUserXPByUserId(long userId);


}
