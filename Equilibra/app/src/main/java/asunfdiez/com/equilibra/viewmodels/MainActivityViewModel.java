package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import asunfdiez.com.equilibra.controllers.UserController;
import asunfdiez.com.equilibra.models.UserProfile;

public class MainActivityViewModel extends AndroidViewModel {

    private final UserController userController;

    // LiveData principal de Room
    private final LiveData<UserProfile> userProfile;

    // Estados de UI encapsulados (Patrón _variable / variable)
    private final MutableLiveData<String> _currentView = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _shouldShowIntervention = new MutableLiveData<>();

    // Observer persistente para reaccionar a cambios en el perfil
    private final Observer<UserProfile> profileObserver = profile -> {
        if (profile != null) {
            if (!profile.getCurrentView().equals(_currentView.getValue())) {
                _currentView.setValue(profile.getCurrentView());
            }
            updateDayCount(profile);
        }
    };

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        // Inicialización de controladores (Capa de Dominio)
        userController = new UserController(application);

        // Obtenemos el flujo de datos (No el valor estático)
        userProfile = userController.getCurrentUser();

        // IMPORTANTE: Observamos el LiveData de forma segura
        userProfile.observeForever(profileObserver);
    }

    // Getters públicos (Solo lectura)
    public LiveData<UserProfile> getUserProfile() { return userProfile; }
    public LiveData<String> getCurrentView() { return _currentView; }
    public LiveData<Boolean> getShouldShowIntervention() { return _shouldShowIntervention; }

    /**
     * Calcula y actualiza el contador de días.
     * y actualizamos el dato en el usuario.
     */
    public void updateDayCount(@NonNull UserProfile user) {
        long diff = System.currentTimeMillis() - user.getCreatedAt();
        int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;

        if (user.getDayCount() != days) {
            user.setDayCount(days);
            android.util.Log.d("MainActivityViewModel_updateDayCount", "UserProfile updated: " + user.toString());
            // Actualizamos el perfil en la base de datos"
            userController.updateUser(user);
        }
    }

    public void checkWellbeing() {
        _shouldShowIntervention.postValue(userController.checkWellbeing());
    }

    public void resetInterventionTrigger() {
        _shouldShowIntervention.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Limpieza obligatoria al usar observeForever para evitar memory leaks
        userProfile.removeObserver(profileObserver);
    }

    public void refreshData() {
        checkWellbeing();
    }
}