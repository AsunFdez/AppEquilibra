package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * ViewModel para NotificationSettingsActivity
 * Gestiona configuración de notificaciones (SharedPreferences)
 */
public class NotificationSettingsViewModel extends AndroidViewModel {

    private final SharedPrefsManager prefsManager;

    // LiveData observables
    private final LiveData<Boolean> notificationsEnabled;
    private final LiveData<Boolean> budgetNotifications;
    private final LiveData<Boolean> achievementNotifications;

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    /**
     * Constructor
     */
    public NotificationSettingsViewModel(@NonNull Application application) {
        super(application);

        this.prefsManager = SharedPrefsManager.getInstance(application);

        // Inicializar LiveData
        this.notificationsEnabled = prefsManager.getNotificationsEnabled();
        this.budgetNotifications = prefsManager.getBudgetNotifications();
        this.achievementNotifications = prefsManager.getAchievementNotifications();
    }

    // ========== GETTERS LIVEDATA ==========

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public LiveData<Boolean> getBudgetNotifications() {
        return budgetNotifications;
    }

    public LiveData<Boolean> getAchievementNotifications() {
        return achievementNotifications;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    // ========== MÉTODOS DE ACCIÓN ==========

    /**
     * Habilita o deshabilita todas las notificaciones
     */
    public void setNotificationsEnabled(boolean enabled) {
        prefsManager.setNotificationsEnabled(enabled);
        if (!enabled) {
            disableAll();
        } else {
            enableAll();
        }

        statusMessage.setValue(enabled ?
                getApplication().getString(R.string.enabled_all_notification) :
                getApplication().getString(R.string.disabled_all_notification));
    }


    /**
     * Habilita o deshabilita notificaciones de presupuesto
     */
    public void setBudgetNotifications(boolean enabled) {

        prefsManager.setBudgetNotifications(enabled);
        statusMessage.setValue(enabled ?
            getApplication().getString(R.string.budget_notifications_enabled) :
            getApplication().getString(R.string.budget_notifications_disabled));
    }

    /**
     * Habilita o deshabilita notificaciones de logros
     */
    public void setAchievementNotifications(boolean enabled) {

        prefsManager.setAchievementNotifications(enabled);
        statusMessage.setValue(enabled ?
                getApplication().getString(R.string.achievement_notifications_enabled) :
                getApplication().getString(R.string.achievement_notifications_disabled));
    }

    /**
     * Activa todas las notificaciones
     */
    public void enableAll() {
        prefsManager.setNotificationsEnabled(true);
        prefsManager.setBudgetNotifications(true);
        prefsManager.setAchievementNotifications(true);
        statusMessage.setValue(getApplication().getString(R.string.enabled_all_notification));
    }

    /**
     * Desactiva todas las notificaciones
     */
    public void disableAll() {
        prefsManager.setNotificationsEnabled(false);
        prefsManager.setBudgetNotifications(false);
        prefsManager.setAchievementNotifications(false);
        statusMessage.setValue(getApplication().getString(R.string.disabled_all_notification));
    }
}
