package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.CloudSyncRepository;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;
import asunfdiez.com.equilibra.utils.ValidationHelper;

/**
 * ViewModel que gestiona la visualización y edición del perfil del usuario.
 * 
 * <p>Maneja el estado de edición de la pantalla de perfil, valida los cambios 
 * realizados y coordina con el controlador de usuario para la persistencia.
 * Gestiona la autenticación mediante cuenta de Google en Firebase y la sincronización con la nube.
 * También gestiona los mensajes de feedback (éxito/error) y la lógica de 
 * eliminación de la cuenta.</p>
 */
public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";
    // Controlador para operaciones de gestión de perfil
    private final UserProfileController userProfileController;
    
    // Perfil de usuario observado
    private final LiveData<UserProfile> userProfile;
    
    // Estado que determina si la vista está en modo lectura o edición
    private final MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);

    // Estado que determina si existe un perfil de usuario
    private final LiveData<Boolean> hasProfile;

    // Mensajes de feedback para la interfaz
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    // Estado de confirmación de eliminación de cuenta
    private final MutableLiveData<Boolean> accountDeleted = new MutableLiveData<>(false);

    // LiveData para sincronización y Firebase
    private final MutableLiveData<Boolean> firebaseAuthenticated = new MutableLiveData<>();
    private final MutableLiveData<String> firebaseEmail = new MutableLiveData<>();
    private final LiveData<Long> lastSyncTime;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.userProfileController = new UserProfileController(application);
        this.userProfile = userProfileController.getCurrentUser();
        this.hasProfile = userProfileController.hasProfile();
        
        // Obtener el tiempo de última sincronización desde SharedPrefsManager (a través de LiveData)
        this.lastSyncTime = SharedPrefsManager.getInstance(application).getLastSyncTime();
        
        // Inicializar estados de Firebase
        refreshFirebaseStatus();
    }

    public void refreshFirebaseStatus() {
        firebaseAuthenticated.setValue(userProfileController.isFirebaseAuthenticated());
        firebaseEmail.setValue(userProfileController.getFirebaseUserEmail());
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }
    public LiveData<Boolean> getHasProfile() {
        return hasProfile;
    }


    public LiveData<Boolean> getFirebaseAuthenticated() {
        return firebaseAuthenticated;
    }

    public LiveData<String> getFirebaseEmail() {
        return firebaseEmail;
    }

    public LiveData<Long> getLastSyncTime() {
        return lastSyncTime;
    }

    public void loginToFirebase(String idToken) {
        Log.d(TAG, "Iniciando login en Firebase con token");
        userProfileController.loginToFirebase(idToken, new CloudSyncRepository.OnAuthCompleteListener() {
            @Override
            public void onSuccess(String uid) {
                Log.d(TAG, "Firebase login exitoso: " + uid);
                refreshFirebaseStatus();
                successMessage.postValue(getApplication().getString(R.string.success_login));
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Firebase login fallido", e);
                errorMessage.postValue(getApplication().getString(R.string.error_login_failed));
            }
        });
    }

    public void loginWithGoogleResponse(GetCredentialResponse response) {
        userProfileController.handleGoogleIdTokenResponse(response, new CloudSyncRepository.OnAuthCompleteListener() {
            @Override
            public void onSuccess(String uid) {
                refreshFirebaseStatus();
                successMessage.postValue(getApplication().getString(R.string.success_login));
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(getApplication().getString(R.string.error_login_failed));
            }
        });
    }

    public GetCredentialRequest getGoogleIdTokenRequest() {
        return userProfileController.getGoogleIdTokenRequest();
    }

    public void logoutFromFirebase() {
        userProfileController.logoutFromFirebase();
        refreshFirebaseStatus();
        successMessage.postValue(getApplication().getString(R.string.success_logout));
    }

    public LiveData<Boolean> getEditMode() {
        return editMode;
    }
    public void setEditMode(boolean enabled) {
        editMode.setValue(enabled);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> isAccountDeleted() {
        return accountDeleted;
    }


    public void saveProfileChanges() {
        // Obtenemos el objeto que el DataBinding ha estado actualizando
        UserProfile user = userProfile.getValue();

        if (user == null) return;

        boolean isValid = validateInput(user.getName(), user.getEmail(), String.valueOf(user.getMonthlyIncome()));
        if (!isValid ) return;

        try {
            userProfileController.updateUser(user, null);
            // Notificar éxito al hilo principal
            successMessage.postValue(getApplication().getString(R.string.profile_updated_success));
            editMode.postValue(false);
        } catch (Exception e) {
            errorMessage.postValue(getApplication().getString(R.string.error_updating_profile));
        }

    }
    private boolean validateInput(String name, String email, String incomeStr) {
        if (name == null || name.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_name_required));
            return false;
        }

        if (email == null || email.isEmpty() || !ValidationHelper.isValidEmail(email)) {
            errorMessage.setValue(getApplication().getString(R.string.error_invalid_email));
            return false;
        }

        if (incomeStr == null || incomeStr.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_income_required));
            return false;
        }

        try {
            double income = Double.parseDouble(incomeStr);
            if (income <= 0) {
                errorMessage.setValue(getApplication().getString(R.string.error_income_invalid));
                return false;
            }
        } catch (NumberFormatException e) {
            errorMessage.setValue(getApplication().getString(R.string.error_income_invalid));
            return false;
        }

        return true;
    }

    public void deleteAccount() {
        try {
            userProfileController.deleteCurrentUser(() -> {
                accountDeleted.postValue(true);
                editMode.postValue(false);
            });
        } catch (Exception e) {
            errorMessage.postValue(getApplication().getString(R.string.error_deleting_account));
        }
    }

    public void syncProfileWithGoogle(String name, String email, String photoUrl) {
        Log.d(TAG, "syncProfileWithGoogle: Iniciando sincronización");
        UserProfile user = userProfile.getValue();
        if (user == null) {
            user = new UserProfile();
        }
        
        user.setName(name);
        user.setEmail(email);
        user.setPhotoUrl(photoUrl);

        try {
            userProfileController.updateUser(user, () -> {
                Log.d(TAG, "syncProfileWithGoogle: Perfil actualizado correctamente");
                successMessage.postValue(getApplication().getString(R.string.profile_updated_success));
            });
        } catch (Exception e) {
            Log.e(TAG, "Error al sincronizar con Google", e);
            errorMessage.postValue(getApplication().getString(R.string.error_updating_profile));
        }
    }

    public void handleBackPress(Runnable onFinish) {
        if (Boolean.TRUE.equals(editMode.getValue())) {
            setEditMode(false);
        } else {
            onFinish.run();
        }
    }

    public void refreshAuthState() {
        firebaseAuthenticated.setValue(userProfileController.isFirebaseAuthenticated());
        firebaseEmail.setValue(userProfileController.getFirebaseUserEmail());
    }
}
