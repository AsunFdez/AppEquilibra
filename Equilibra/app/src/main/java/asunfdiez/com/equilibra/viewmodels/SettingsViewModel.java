package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * ViewModel que centraliza las preferencias generales de la aplicación.
 * 
 * <p>Maneja el estado resumido de las configuraciones de usuario para mostrarlas 
 * en el menú principal de ajustes, incluyendo el nombre del usuario, el estado 
 * de las notificaciones, el tema visual y el idioma. Actúa como puente entre 
 * las preferencias compartidas y la interfaz de configuración.</p>
 */
public class SettingsViewModel extends AndroidViewModel {


    // Gestor de preferencias para lectura de ajustes guardados
    private final SharedPrefsManager prefsManager;

    // LiveData que observan los diferentes ajustes generales
    private final LiveData<String> userName;
    private final LiveData<Boolean> notificationsEnabled;
    private final LiveData<Boolean> darkMode;
    private final LiveData<String> theme;
    private final LiveData<String> language;
    
    // Controlador de perfil para acciones administrativas
    private final UserProfileController userProfileController;


    /**
     * Constructor
     */
    public SettingsViewModel(@NonNull Application application) {
        super(application);

        this.prefsManager = SharedPrefsManager.getInstance(application);
        this.userProfileController = new UserProfileController(application);

        // Inicializar LiveData
        this.userName = userProfileController.getUserName();
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
     * El userProfileController se encarga de limpiar datos de usuario
     * de la base de datos
     */
    public void resetApp() {
        try {
            userProfileController.deleteCurrentUser(prefsManager::resetAll);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Exporta las preferencias
     */
    public String exportSettings() {
        return prefsManager.exportPreferences();
    }
}
