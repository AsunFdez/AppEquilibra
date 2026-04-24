package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.List;

import asunfdiez.com.equilibra.database.AlternativeDao;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Alternative;

/**
 * Repositorio que gestiona las alternativas de ahorro sugeridas al usuario.
 * 
 * <p>Proporciona métodos para consultar sugerencias de ahorro basadas en la 
 * clasificación de los gastos y palabras clave específicas. Ayuda a los controladores 
 * a ofrecer consejos personalizados durante las intervenciones suaves.</p>
 */
public class AlternativeRepository {
    // DAO para la consulta de alternativas de ahorro persistidas
    private final AlternativeDao alternativeDao;
    
    public AlternativeRepository(Context context) {
        this.alternativeDao = AppDatabase.getInstance(context).alternativeDao();
    }

    /**
     * Recupera una lista de alternativas de ahorro que coincidan con la categoría 
     * y palabras clave de la descripción de un gasto.
     * 
     * @param categoryName Nombre de la categoría del gasto analizado.
     * @param description Descripción del gasto para buscar palabras clave.
     * @return Lista de alternativas aplicables encontradas.
     */
    public List<Alternative> getAlternativesByCategoryAndDescription(String categoryName, String description) {
        return alternativeDao.getAlternativesByCategoryAndDescription(categoryName, description);
    }

}
