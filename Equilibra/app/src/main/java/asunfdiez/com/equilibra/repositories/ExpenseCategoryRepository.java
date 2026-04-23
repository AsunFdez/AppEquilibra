package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.List;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.ExpenseCategoryDao;
import asunfdiez.com.equilibra.models.ExpenseCategory;

public class ExpenseCategoryRepository {

    private final ExpenseCategoryDao expenseCategoryDao;
    private final Context context;

    public ExpenseCategoryRepository(Context context) {
        this.context = context;
        this.expenseCategoryDao = AppDatabase.getInstance(context).expenseCategoryDao();
    }

    /**
     * Obtiene las categorías
     * @return lista de categorías
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
     * Borra todas las categorías de la base de datos
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
