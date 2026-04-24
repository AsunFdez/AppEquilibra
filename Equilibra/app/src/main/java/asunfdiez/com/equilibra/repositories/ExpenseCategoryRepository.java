package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.List;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.ExpenseCategoryDao;
import asunfdiez.com.equilibra.models.ExpenseCategory;

/**
 * Repositorio que gestiona el catálogo de categorías de gastos.
 * 
 * <p>Proporciona acceso a las categorías disponibles para la clasificación de gastos 
 * y asegura que la base de datos contenga las categorías iniciales predefinidas 
 * mediante una inserción automática si se detecta que la tabla está vacía.</p>
 */
public class ExpenseCategoryRepository {

    // DAO para la gestión de categorías
    private final ExpenseCategoryDao expenseCategoryDao;
    private final Context context;

    public ExpenseCategoryRepository(Context context) {
        this.context = context;
        this.expenseCategoryDao = AppDatabase.getInstance(context).expenseCategoryDao();
    }

    /**
     * Recupera todas las categorías disponibles. Si la tabla está vacía, 
     * dispara la inserción de las categorías predeterminadas del sistema.
     * 
     * @return Lista de objetos ExpenseCategory.
     */
    public List<ExpenseCategory> getCategories() {
        List<ExpenseCategory> categories = expenseCategoryDao.getCategories();
        if (categories.isEmpty()) {
            AppDatabase.insertInitialCategories();
            categories = expenseCategoryDao.getCategories();
        }
        return categories;
    }

    /**
     * Elimina permanentemente todos los registros de categorías en la base de datos.
     */
    public void deleteAll() {
        expenseCategoryDao.deleteAll();
    }

    /**
     * Obtiene la categoría por su Id
     * @param categoryId id de la categoría
     * @return categoría obtenida
     */
    public String getCategoryNameById(long categoryId) {
        return expenseCategoryDao.getCategoryNameById(categoryId);
    }
}
