package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.SavingGoalDao;
import asunfdiez.com.equilibra.database.UserProfileDao;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;

public class UserProfileRepository {
    private final UserProfileDao userProfileDao;
    private final SavingGoalDao savigGoalDao;
    private Context context;

    public UserProfileRepository(Context context) {
        this.context = context;
        this.userProfileDao = AppDatabase.getInstance(context).userProfileDao();
        this.savigGoalDao = AppDatabase.getInstance(context).savingGoalDao();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileDao.getUserProfile();
    }

    public void updateProfile(UserProfile profile) {
        userProfileDao.insertOrUpdate(profile);
    }


    public void deleteAll() {
        userProfileDao.deleteAll();
    }

    public void saveSavingGoal (SavingGoal savingGoal) {
        savigGoalDao.insertOrUpdateSavingGoal(savingGoal);
    }

    public void incrementDayCount () {
        userProfileDao.incrementDayCount();
    }


}

