package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.List;

import asunfdiez.com.equilibra.database.AlternativeDao;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Alternative;

/**
 * Repository para gestionar la lógica de Alternativas ante el registro de un gasto
 * Intermedia entre ViewModel y Room DAOs
 */
public class AlternativeRepository {
    private final AlternativeDao alternativeDao;
    public AlternativeRepository(Context context) {
        this.alternativeDao = AppDatabase.getInstance(context).alternativeDao();
    }

    /**
     * Obtiene la lista de alternativas de una categoría y descripción del gasto
      * @param categoryName nombre de la categoría
     * @param description descripción del gasto
     * @return lista con las posibles alternativas
     */
    public List<Alternative> getAlternativesByCategoryAndDescription(String categoryName, String description) {
        return alternativeDao.getAlternativesByCategoryAndDescription(categoryName, description);
    }

}
