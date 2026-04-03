package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

public class MainActivityViewModel extends AndroidViewModel {

    private final SharedPrefsManager preferencesManager;
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> currentView = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        preferencesManager = SharedPrefsManager.getInstance(application);
        loadUserData();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<String> getCurrentView() {
        return currentView;
    }

    public void loadUserData() {
        UserProfile user = preferencesManager.getUserProfile();
        
        // Calcular días transcurridos
        int days = (int) ((System.currentTimeMillis() - user.getCreatedAt()) / (1000 * 60 * 60 * 24)) + 1;
        user.setDayCount(days);
        
        // Guardar si hubo cambios (como el dayCount)
        preferencesManager.saveUserProfile(user);
        
        userProfile.setValue(user);
        currentView.setValue(user.getCurrentView());
    }

    public void refreshData() {
        loadUserData();
    }
}
