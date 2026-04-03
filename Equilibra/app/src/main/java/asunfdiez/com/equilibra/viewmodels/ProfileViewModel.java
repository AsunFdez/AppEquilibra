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
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accountDeleted = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.userController = new UserController(application);
        loadUserProfile();
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

    public void loadUserProfile() {
        UserProfile user = userController.getCurrentUser();
        if (user != null) {
            userProfile.setValue(user);
        } else {
            errorMessage.setValue(getApplication().getString(R.string.error_loading_profile));
        }
    }

    public void  saveProfileChanges() {
        UserProfile user = userProfile.getValue();
        if (user == null) {
            errorMessage.setValue(getApplication().getString(R.string.error_updating_profile));
            return;
        }
        if (!validateInput(user.getName(), user.getEmail(), String.valueOf(user.getMonthlyIncome()))) {
            return;
        }

        try {
            boolean success = userController.updateUser(user);
            if (success) {
                userProfile.setValue(user);
                successMessage.setValue(getApplication().getString(R.string.profile_updated_success));
                editMode.setValue(false);
            } else {
                errorMessage.setValue(getApplication().getString(R.string.error_updating_profile));
            }
        } catch (NumberFormatException e) {
            errorMessage.setValue(getApplication().getString(R.string.error_income_invalid));
        }
    }

    private boolean validateInput(String name, String email, String incomeStr) {
        if (name.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_name_required));
            return false;
        }

        if (email.isEmpty() || !ValidationHelper.isValidEmail(email)) {
            errorMessage.setValue(getApplication().getString(R.string.error_invalid_email));
            return false;
        }

        if (incomeStr.isEmpty()) {
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

    public void changeView(String newView) {
        UserProfile user = userProfile.getValue();
        if (user != null && !user.getCurrentView().equals(newView)) {
            userController.setCurrentView(newView);
            user.setCurrentView(newView);
            userProfile.setValue(user);
        }
    }

    public void deleteAccount() {
        boolean success = userController.deleteCurrentUser();
        if (success) {
            accountDeleted.setValue(true);
        } else {
            errorMessage.setValue(getApplication().getString(R.string.error_deleting_account));
        }
    }

    public void handleBackPress(Runnable onFinish) {
        if (Boolean.TRUE.equals(editMode.getValue())) {
            setEditMode(false);
            loadUserProfile(); // Revertir cambios no guardados
        } else {
            onFinish.run();
        }
    }

}
