package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);

    @Query("SELECT * FROM "+ Constants.TABLE_BUDGETS+" WHERE category = :category")
    Budget getBudgetByCategory(String category);

    @Query("DELETE FROM " + Constants.TABLE_BUDGETS)
    void deleteAll();



}
