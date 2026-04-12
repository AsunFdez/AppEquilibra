package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.SavingGoalDao;
import asunfdiez.com.equilibra.database.UserProfileDao;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;

public class UserProfileRepository {
    private final UserProfileDao userProfileDao;
    private final SavingGoalDao savingGoalDao;
    private Context context;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public UserProfileRepository(Context context) {
        this.context = context;
        this.userProfileDao = AppDatabase.getInstance(context).userProfileDao();
        this.savingGoalDao = AppDatabase.getInstance(context).savingGoalDao();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileDao.getUserProfile();
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

    public void saveSavingGoal(SavingGoal savingGoal, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            savingGoalDao.insertOrUpdateSavingGoal(savingGoal);
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


    public int getWeeksUnderBudget() {
        return userProfileDao.getWeeksUnderBudget();
    }


}

