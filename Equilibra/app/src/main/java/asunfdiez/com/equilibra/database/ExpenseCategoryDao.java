package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface ExpenseCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListOfCategories(List<ExpenseCategory> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpenseCategory(ExpenseCategory category);

    @Update
    void updateExpenseCategory(ExpenseCategory category);

    @Delete
    void deleteExpenseCategory(ExpenseCategory category);

    @Query("SELECT * FROM " + Constants.TABLE_CATEGORIES )
    public List<ExpenseCategory> getCategories();

    @Query("DELETE FROM " + Constants.TABLE_CATEGORIES)
    void deleteAll();

}
