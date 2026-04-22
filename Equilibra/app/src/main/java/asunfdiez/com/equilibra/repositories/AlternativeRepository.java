package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AlternativeDao;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Alternative;

public class AlternativeRepository {
    private final AlternativeDao alternativeDao;
    private final ExecutorService executorService;
    public AlternativeRepository(Context context) {
        this.alternativeDao = AppDatabase.getInstance(context).alternativeDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public List<Alternative> getAlternativesByCategoryAndDescription(String categoryName, String description) {
        return alternativeDao.getAlternativesByCategoryAndDescription(categoryName, description);
    }
}
