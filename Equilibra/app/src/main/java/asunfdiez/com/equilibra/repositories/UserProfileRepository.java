package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.UserProfileDao;
import asunfdiez.com.equilibra.models.UserProfile;

/**
 * Repository para gestionar el perfil de usuario
 * Intermedia entre ViewModel y Room DAOs
 */
public class UserProfileRepository {
    private final UserProfileDao userProfileDao;
    private Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    public UserProfileRepository(Context context) {
        this.context = context;
        this.userProfileDao = AppDatabase.getInstance(context).userProfileDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileDao.getUserProfile();
    }

    public UserProfile getUserProfileSync() {
        return userProfileDao.getUserProfileSync();
    }

    public LiveData<String> getUserName() {
        return userProfileDao.getUserName();
    }


    public void updateProfile(UserProfile profile, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.insertOrUpdate(profile);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }



    public void deleteAll(@Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.deleteAll();
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void incrementDayCount (@Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.incrementDayCount();
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void updateLastConnection(long timestamp, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.updateLastConnection(timestamp);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void resetWeeklyProgress(long timestamp, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.resetWeeklyProgress(timestamp);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void updateWeekProgress(int progress, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.updateWeekProgress(progress);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }


    public int getWeeksUnderBudget() {
        return userProfileDao.getWeeksUnderBudget();
    }


    public void decrementWeekProgress() {
        executorService.execute(userProfileDao::decrementWeekProgress);
    }

    public void incrementWeekProgress() {
        executorService.execute(userProfileDao::incrementWeekProgress);
    }

    public double getUserMonthlyIncome() {
        return userProfileDao.getUserMonthlyIncome();
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}

