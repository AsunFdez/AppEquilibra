package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

public class SplashViewModel extends AndroidViewModel {
    private final LiveData<UserProfile> userProfile;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        UserProfileRepository repository = new UserProfileRepository(application);
        this.userProfile = repository.getUserProfile();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }
}
