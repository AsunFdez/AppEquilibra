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

/**
 * DAO para la gestión de las categorías de clasificación de gastos.
 * 
 * <p>Permite la gestión del catálogo de categorías, incluyendo la inserción masiva 
 * de categorías iniciales, la recuperación de la lista completa para su selección 
 * en formularios y la actualización de metadatos visuales como iconos y colores.</p>
 */
@Dao
public interface ExpenseCategoryDao {

    /**
     * Inserta una lista completa de categorías en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListOfCategories(List<ExpenseCategory> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpenseCategory(ExpenseCategory category);

    @Update
    void updateExpenseCategory(ExpenseCategory category);

    @Delete
    void deleteExpenseCategory(ExpenseCategory category);

    @Query("SELECT * FROM " + Constants.TABLE_CATEGORIES )
    List<ExpenseCategory> getCategories();

    @Query("SELECT * FROM " + Constants.TABLE_CATEGORIES + " WHERE " + Constants.COLUMN_CATEGORY_ID + " = :id")
    ExpenseCategory getCategoryById(long id);

    @Query("SELECT " + Constants.COLUMN_CATEGORY_NAME + " FROM " + Constants.TABLE_CATEGORIES + " WHERE " + Constants.COLUMN_CATEGORY_ID + " = :id")
    String getCategoryNameById(long id);

    @Query("SELECT " + Constants.COLUMN_CATEGORY_ID + " FROM " + Constants.TABLE_CATEGORIES + " WHERE iconName = :iconName")
    Long getCategoryIdByIconName(String iconName);

    @Query("DELETE FROM " + Constants.TABLE_CATEGORIES)
    void deleteAll();

}
