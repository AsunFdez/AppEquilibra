package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * ViewModel para SettingsActivity
 * Gestiona las preferencias generales de la app
 */
public class SettingsViewModel extends AndroidViewModel {


    private final SharedPrefsManager prefsManager;
    private final UserProfileRepository userRepository;


    // LiveData observables
    private final LiveData<String> userName;
    private final LiveData<Boolean> notificationsEnabled;
    private final LiveData<Boolean> darkMode;
    private final LiveData<String> theme;
    private final LiveData<String> language;

    /**
     * Constructor
     */
    public SettingsViewModel(@NonNull Application application) {
        super(application);

        this.prefsManager = SharedPrefsManager.getInstance(application);
        this.userRepository = new UserProfileRepository(application);

        // Inicializar LiveData
        this.userName = userRepository.getUserName();
        this.notificationsEnabled = prefsManager.getNotificationsEnabled();
        this.darkMode = prefsManager.getDarkMode();
        this.theme = prefsManager.getTheme();
        this.language = prefsManager.getLanguage();
    }

    // ========== GETTERS LIVEDATA ==========

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public LiveData<Boolean> getDarkMode() {
        return darkMode;
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public LiveData<String> getLanguage() {
        return language;
    }

    // ========== MÉTODOS DE ACCIÓN ==========

    /**
     * Limpia todas las preferencias
     */
    public void clearAllSettings() {
        prefsManager.clearAll();
    }

    /**
     * Reset completo de la app
     */
    public void resetApp() {
        prefsManager.resetAll();
    }

    /**
     * Exporta las preferencias
     */
    public String exportSettings() {
        return prefsManager.exportPreferences();
    }
}
