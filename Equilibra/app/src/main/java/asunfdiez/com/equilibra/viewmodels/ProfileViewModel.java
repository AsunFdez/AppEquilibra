package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.UserController;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.ValidationHelper;

public class ProfileViewModel extends AndroidViewModel {
    private final UserController userController;
    private final LiveData<UserProfile> userProfile;
    private final MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accountDeleted = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.userController = new UserController(application);
        userProfile = userController.getCurrentUser();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
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
            userController.updateUser(user, null);
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
            userController.deleteCurrentUser(() -> {
                accountDeleted.postValue(true);
                editMode.postValue(false);
            });
        } catch (Exception e) {
            errorMessage.postValue(getApplication().getString(R.string.error_deleting_account));
        }
    }

    public void handleBackPress(Runnable onFinish) {
        if (Boolean.TRUE.equals(editMode.getValue())) {
            setEditMode(false);
        } else {
            onFinish.run();
        }
    }

}
