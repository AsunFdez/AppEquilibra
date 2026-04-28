package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.UserProfile;

public class SplashViewModel extends AndroidViewModel {
    private final LiveData<UserProfile> userProfile;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        UserProfileController controller = new UserProfileController(application);
        this.userProfile = controller.getCurrentUser();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }
}
