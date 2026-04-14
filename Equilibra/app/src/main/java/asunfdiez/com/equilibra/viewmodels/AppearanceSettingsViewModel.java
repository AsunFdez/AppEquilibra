package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * ViewModel para AppearanceSettingsActivity
 * Gestiona la apariencia de la app (SharedPreferences)
 *
 */
public class AppearanceSettingsViewModel extends AndroidViewModel {

    private final SharedPrefsManager prefsManager;

    // LiveData observables
    private final LiveData<String> viewMode;
    private final LiveData<String> theme;
    private final LiveData<String> language;

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    /**
     * Constructor
     */
    public AppearanceSettingsViewModel(@NonNull Application application) {
        super(application);

        this.prefsManager = SharedPrefsManager.getInstance(application);

        // Inicializar LiveData
        this.theme = prefsManager.getTheme();
        this.language = prefsManager.getLanguage();
        this.viewMode = prefsManager.getViewMode();
    }

    // ========== GETTERS LIVEDATA ==========


    public LiveData<String> getViewMode() {
        return viewMode;
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public LiveData<String> getLanguage() {
        return language;
    }


    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    // ========== MÉTODOS DE ACCIÓN ==========

    /**
     * Cambia el modo de vista beginner, intermediate o advanced
     * @param viewModeValue
     */
    public void setViewMode(String viewModeValue) {
        if (viewModeValue == null || viewModeValue.trim().isEmpty()) {
            statusMessage.setValue(getApplication().getString(R.string.view_mode_not_valid));
            return;
        }
        prefsManager.setViewMode(viewModeValue);
        statusMessage.setValue(getApplication().getString(R.string.view_mode_changed , viewModeValue));
    }

    /**
     * Cambia el tema
     * @param themeValue
     */
    public void setTheme(String themeValue) {
        if (themeValue == null || themeValue.trim().isEmpty()) {
            statusMessage.setValue("Tema no válido");
            return;
        }

        prefsManager.setTheme(themeValue);
        statusMessage.setValue("Tema cambiado a: " + getThemeName(themeValue));
    }

    /**
     * Cambia el idioma
     * @param lang
     */
    public void setLanguage(String lang) {
        if (lang == null || lang.trim().isEmpty()) {
            statusMessage.setValue(getApplication().getString(R.string.invalid_language));
            return;
        }

        prefsManager.setLanguage(lang);
        statusMessage.setValue(getApplication().getString(R.string.language_changed));
    }


    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtiene el nombre del tema
     */
    private String getThemeName(String themeValue) {
        switch (themeValue) {
            case "light":
                return getApplication().getString(R.string.theme_light);
            case "dark":
                return getApplication().getString(R.string.theme_dark);
            case "auto":
                return getApplication().getString(R.string.theme_auto);
            default:
                return themeValue;
        }
    }

    /**
     * Obtiene el nombre del idioma
     */
    public String getLanguageName(String langCode) {
        switch (langCode) {
            case "es":
                return "Español";
            case "en":
                return "English";
            case "fr":
                return "Français";
            case "de":
                return "Deutsch";
            default:
                return langCode;
        }
    }

    /**
     * Verifica si un tema es válido
     */
    public boolean isValidTheme(String themeValue) {
        return "light".equals(themeValue) ||
                "dark".equals(themeValue) ||
                "auto".equals(themeValue);
    }

    /**
     * Verifica si un idioma es válido
     */
    public boolean isValidLanguage(String lang) {
        return "es".equals(lang) ||
                "en".equals(lang) ||
                "fr".equals(lang) ||
                "de".equals(lang);
    }
}

