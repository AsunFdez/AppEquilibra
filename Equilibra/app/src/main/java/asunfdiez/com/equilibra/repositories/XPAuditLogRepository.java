package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.XPAuditLogDao;
import asunfdiez.com.equilibra.models.XPAuditLog;

public class XPAuditLogRepository {
    private final XPAuditLogDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public XPAuditLogRepository(Context context) {
        this.dao = AppDatabase.getInstance(context).xpAuditLogDao();
    }

    public void logXp(long userId, int amount, String description) {
        executor.execute(() -> {
            dao.insertLog(new XPAuditLog(userId, amount, description));
        });
    }

    public LiveData<List<XPAuditLog>> getLogs(long userId) {
        return dao.getLogsByUserId(userId);
    }

    public List<XPAuditLog> getLogsSync(long userId) {
        return dao.getLogsByUserIdSync(userId);
    }
}
