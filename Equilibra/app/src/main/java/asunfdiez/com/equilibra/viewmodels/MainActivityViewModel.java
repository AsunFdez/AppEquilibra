package asunfdiez.com.equilibra.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import asunfdiez.com.equilibra.controllers.UserController;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

public class MainActivityViewModel extends AndroidViewModel {

    private final UserController userController;
    private final SharedPrefsManager prefsManager;

    // LiveData principal de Room
    private final LiveData<UserProfile> userProfile;

    // Estados de UI encapsulados (Patrón _variable / variable)
    private final MutableLiveData<String> _currentView = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _shouldShowIntervention = new MutableLiveData<>();

    private final MutableLiveData<Advice> _advice = new MutableLiveData<>();


    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        // Inicialización de controladores (Capa de Dominio)
        userController = new UserController(application);

        // Obtenemos el flujo de datos (No el valor estático)
        userProfile = userController.getCurrentUser();

        prefsManager = SharedPrefsManager.getInstance(application);

        // Usamos un observador para las tareas de inicio que requieren el perfil
        userProfile.observeForever(new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile profile) {
                if (profile != null) {
                    userController.checkAndResetWeeklyProgress();
                    userController.incrementDayCount();
                }
            }
        });

    }

    // Getters públicos (Solo lectura)
    public LiveData<UserProfile> getUserProfile() { return userProfile; }
    public LiveData<String> getCurrentView() { return _currentView; }
    public LiveData<Boolean> getShouldShowIntervention() { return _shouldShowIntervention; }

    public LiveData<Advice> getAdvice() {
        // Transformamos el UserProfile en un Advice automáticamente cada vez que cambie
        return Transformations.map(userProfile, user -> {
            if (user == null) return null;
            return userController.getAdvice(user);
        });
    }
    public void checkPreferenceView() {
        _currentView.postValue(prefsManager.getViewMode().getValue());
    }

    public void checkWellbeing() {
        _shouldShowIntervention.postValue(userController.checkWellbeing());
    }

    public void resetInterventionTrigger() {
        _shouldShowIntervention.setValue(false);
    }

    public void checkAdvice() {
        UserProfile user = userController.getCurrentUser().getValue();
        if (user != null) {
            // Solo procesar si el usuario existe
            _advice.postValue(userController.getAdvice(user));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void refreshData() {
        checkPreferenceView();
        checkWellbeing();
        checkAdvice();
    }
}