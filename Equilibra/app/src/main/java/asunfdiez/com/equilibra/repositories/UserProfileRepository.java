package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.UserProfileDao;
import asunfdiez.com.equilibra.models.UserProfile;

public class UserProfileRepository {
    private final UserProfileDao userProfileDao;
    private Context context;

    public UserProfileRepository(Context context) {
        this.context = context;
        this.userProfileDao = AppDatabase.getInstance(context).userProfileDao();
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


}