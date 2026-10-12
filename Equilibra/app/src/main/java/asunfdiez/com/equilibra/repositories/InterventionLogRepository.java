package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.InterventionLogDao;
import asunfdiez.com.equilibra.models.InterventionLog;

public class InterventionLogRepository {

    private final InterventionLogDao interventionLogDao;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Constructor que inicializa el repositorio con el contexto de la aplicación.
     */
    public InterventionLogRepository(Context context) {
        this.context = context;
        this.interventionLogDao = AppDatabase.getInstance(context).interventionLogDao();
    }

    public void insertInterventionLog(InterventionLog interventionLog) {
        executor.execute(() -> {
            interventionLogDao.insertInterventionLog(interventionLog);
        });
    }

    public void updateInterventionLog(InterventionLog interventionLog) {
        executor.execute(() -> {
            interventionLogDao.updateInterventionLog(interventionLog);
        });
    }

    public void deleteInterventionLog(InterventionLog interventionLog) {
        executor.execute(() -> {
            interventionLogDao.deleteInterventionLog(interventionLog);
        });
    }

    public void deleteAll() {
        executor.execute(() -> {
            interventionLogDao.deleteAll();
        });
    }


    public List<InterventionLog> getRecentInterventions(long userId, int limit) {
        return interventionLogDao.getRecentInterventions(userId, limit);
    }

    public LiveData<Boolean> observeHasAnyInterventionLog() {
        return interventionLogDao.observeHasAnyInterventionLog();
    }

    public LiveData<List<InterventionLog>> observeAllInterventionLogs() {
        return interventionLogDao.observeAllInterventionLogs();
    }

    public LiveData<Boolean> observeHasLogsForUser(long userId) {
        return interventionLogDao.observeHasLogsForUser(userId);
    }

    public LiveData<List<InterventionLog>> observeByUserId(long userId) {
        return interventionLogDao.observeByUserId(userId);
    }


}
