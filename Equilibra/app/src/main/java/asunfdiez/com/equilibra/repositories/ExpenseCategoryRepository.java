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


    public List<ExpenseCategory> getCategories() {
        List<ExpenseCategory> categories = expenseCategoryDao.getCategories();
        if (categories.isEmpty()) {
            AppDatabase.insertInitialCategories();
            categories = expenseCategoryDao.getCategories();
        }
        return categories;
    }


    public void deleteAll() {
        expenseCategoryDao.deleteAll();
    }

    public String getCategoryNameById(long categoryId) {
        return expenseCategoryDao.getCategoryNameById(categoryId);
    }
}
