package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import asunfdiez.com.equilibra.models.Alternative;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface AlternativeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlternative(Alternative alternative);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListOfAlternatives(List<Alternative> alternatives);

    @Query("SELECT * FROM " + Constants.TABLE_ALTERNATIVES + " WHERE category = :category")
    List<Alternative> getAlternativesByCategory(String category);

    @Query("SELECT * FROM " + Constants.TABLE_ALTERNATIVES + " WHERE category = :category AND (descriptionKeyword IS NULL OR descriptionKeyword = '' OR :description LIKE '%' || descriptionKeyword || '%')")
    List<Alternative> getAlternativesByCategoryAndDescription(String category, String description);

    @Query("SELECT * FROM " + Constants.TABLE_ALTERNATIVES)
    List<Alternative> getAllAlternatives();

    @Query("DELETE FROM " + Constants.TABLE_ALTERNATIVES)
    void deleteAll();
}
