package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.UserXPDao;
import asunfdiez.com.equilibra.database.XPAuditLogDao;
import asunfdiez.com.equilibra.models.UserXP;
import asunfdiez.com.equilibra.models.XPAuditLog;

public class UserXPRepository {
    private final UserXPDao userXPDao;
    private final XPAuditLogDao xpAuditLogDao;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public UserXPRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.userXPDao = db.userXPDao();
        this.xpAuditLogDao = db.xpAuditLogDao();
    }

    public UserXP getUserXPByUserIdSync(long userId) {
        return userXPDao.getUserXPByUserId(userId);
    }

    public LiveData<UserXP> getUserXPAsync(long userId) {
        return userXPDao.getUserXPAsync(userId);
    }

    public LiveData<List<XPAuditLog>> getAuditLogs(long userId) {
        return xpAuditLogDao.getLogsByUserId(userId);
    }

    public List<XPAuditLog> getAuditLogsSync(long userId) {
        return xpAuditLogDao.getLogsByUserIdSync(userId);
    }

    public void addXp(long userId, int amount, String description) {
        executorService.execute(() -> {
            UserXP userXP = userXPDao.getUserXPByUserId(userId);
            if (userXP != null) {
                userXP.addXP(amount);
                userXPDao.updateUserXP(userXP);
            } else {
                userXP = new UserXP();
                userXP.setUserId(userId);
                userXP.addXP(amount);
                userXPDao.insertUserXP(userXP);
            }
            xpAuditLogDao.insertLog(new XPAuditLog(userId, amount, description));
        });
    }

    public void incrementAchievementsUnlocked(long userId) {
        executorService.execute(() -> {
            UserXP userXP = userXPDao.getUserXPByUserId(userId);
            if (userXP != null) {
                userXP.setAchievementsUnlocked(userXP.getAchievementsUnlocked() + 1);
                userXPDao.updateUserXP(userXP);
            }
        });
    }
}
