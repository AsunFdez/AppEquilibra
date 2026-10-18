package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import asunfdiez.com.equilibra.models.Alternative;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para la gestión de las alternativas de ahorro sugeridas.
 * 
 * <p>Define las consultas para recuperar sugerencias de ahorro basadas en la 
 * categoría del gasto y palabras clave en su descripción. Se utiliza durante 
 * el proceso de análisis para ofrecer opciones más económicas al usuario.</p>
 */
@Dao
public interface AlternativeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlternative(Alternative alternative);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListOfAlternatives(List<Alternative> alternatives);

    @Query("SELECT * FROM " + Constants.TABLE_ALTERNATIVES + " WHERE categoryId = :categoryId")
    List<Alternative> getAlternativesByCategory(long categoryId);

    @Query("SELECT * FROM " + Constants.TABLE_ALTERNATIVES + " WHERE categoryId = :categoryId AND (descriptionKeyword IS NULL OR descriptionKeyword = '' OR :description LIKE '%' || descriptionKeyword || '%')")
    List<Alternative> getAlternativesByCategoryAndDescription(long categoryId, String description);

    @Query("SELECT * FROM " + Constants.TABLE_ALTERNATIVES)
    List<Alternative> getAllAlternatives();

    @Query("DELETE FROM " + Constants.TABLE_ALTERNATIVES)
    void deleteAll();
}
