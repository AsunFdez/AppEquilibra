package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.UserController;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;
import asunfdiez.com.equilibra.utils.ValidationHelper;


/**
 * Activity de perfil de usuario
 * Permite visualizar y editar información del perfil personal
 *
 */
public class ProfileActivity extends AppCompatActivity {


    private UserController userController;
    private UserProfile user;
    private SharedPrefsManager preferencesManager;

    private Toolbar toolbar;
    private ImageView ivProfileImage;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etMonthlyIncome;
    private Button btnSaveProfile;
    private Button btnChangePassword;
    private Button btnDeleteAccount;
    private ImageButton btnEditProfile;
    private LinearLayout layoutProfile;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeComponents();
        setupToolbar();
        loadUserProfile();
        setupListeners();
        setupBackNavigation();
        AnimationHelper.fadeIn(layoutProfile);

    }

    /**
     * Inicializa los componentes de la UI
     */
    private void initializeComponents() {
        userController = new UserController(this);
        preferencesManager =  SharedPrefsManager.getInstance(this);

        toolbar = findViewById(R.id.toolbarProfile);
        layoutProfile = findViewById(R.id.layoutProfile);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etMonthlyIncome = findViewById(R.id.etMonthlyIncome);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnEditProfile = findViewById(R.id.btnEditProfile);
    }

    /**
     * Configura la toolbar con navegación
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressedDispatcher(); // Llamamos a nuestra navegación personalizada
        });
    }
    // Método para salir de la actividad
    private void onBackPressedDispatcher() {

        if (isEditMode) {
            // Si estamos editando, cancelamos la edición y recargamos datos
            setEditMode(false);
            loadUserProfile();
        } else {
            // Si no estamos editando, salimos de la actividad
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }

    /**
     * Carga el perfil del usuario
     */
    private void loadUserProfile() {
        user = userController.getCurrentUser();

        if (user != null) {
            displayUserData(user);
        } else {
            showError(getString(R.string.error_loading_profile));
        }
    }

    /**
     * Muestra los datos del usuario en la interfaz
     *
     * @param user Usuario a mostrar
     */
    private void displayUserData(UserProfile user) {
        tvProfileName.setText(user.getName());
        tvProfileEmail.setText(user.getEmail());
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        double income = user.getMonthlyIncome();
        String formatted = String.format(Locale.getDefault(), Constants.CURRENCY_FORMAT, income);
        etMonthlyIncome.setText(formatted);

        // Deshabilitar edición por defecto
        setEditMode(false);
    }

    /**
     * Configura el modo de edición de los campos
     *
     * @param enabled true para habilitar edición
     */
    private void setEditMode(boolean enabled) {
        isEditMode = enabled;
        etName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etMonthlyIncome.setEnabled(enabled);
        String emailDummy = getString(R.string.userEmailText);
        String userDummyName = getString(R.string.userText);

        if (etEmail.getText().toString().equals(emailDummy)) {
            etEmail.setText("");
        }

        if (etName.getText().toString().equals(userDummyName)) {
            etName.setText("");
        }

        btnSaveProfile.setVisibility(enabled ? View.VISIBLE : View.GONE);
        btnEditProfile.setVisibility(enabled ? View.GONE : View.VISIBLE);

    }

    /**
     * Configura los listeners de los componentes
     */
    private void setupListeners() {
        ivProfileImage.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            changeProfileImage();
        });

        btnEditProfile.setOnClickListener(v -> {
            if (!isEditMode) {
                setEditMode(true);
                AnimationHelper.shakeAnimation(etName);
            }
        });

        btnSaveProfile.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            saveProfileChanges();
            onBackPressedDispatcher();
        });

        btnChangePassword.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            showChangePasswordDialog();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            showDeleteAccountDialog();
        });
    }

    /**
     * Permite cambiar la imagen de perfil
     */
    private void changeProfileImage() {
        // TODO: Implementar selección de imagen desde galería o cámara
        Toast.makeText(this, R.string.feature_coming_soon, Toast.LENGTH_SHORT).show();
    }

    /**
     * Guarda los cambios del perfil
     */
    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String incomeStr = etMonthlyIncome.getText().toString().trim();

        if (!validateInput(name, email, incomeStr)) {
            return;
        }

        double monthlyIncome = Double.parseDouble(incomeStr);

        user = userController.getCurrentUser();
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setMonthlyIncome(monthlyIncome);

            boolean success = userController.updateUser(user);

            if (success) {
                showSuccess(getString(R.string.profile_updated_success));
                setEditMode(false);
                displayUserData(user);
            } else {
                showError(getString(R.string.error_updating_profile));
            }
        }
    }

    /**
     * Valida la entrada del usuario
     *
     * @param name Nombre a validar
     * @param email Email a validar
     * @param incomeStr Ingreso mensual a validar
     * @return true si es válido
     */
    private boolean validateInput(String name, String email, String incomeStr) {
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name_required));
            etName.requestFocus();
            AnimationHelper.shakeAnimation(etName);
            return false;
        }

        if (email.isEmpty() || !ValidationHelper.isValidEmail(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            etEmail.requestFocus();
            AnimationHelper.shakeAnimation(etEmail);
            return false;
        }

        if (incomeStr.isEmpty()) {
            etMonthlyIncome.setError(getString(R.string.error_income_required));
            etMonthlyIncome.requestFocus();
            AnimationHelper.shakeAnimation(etMonthlyIncome);
            return false;
        }

        try {
            double income = Double.parseDouble(incomeStr);
            if (income <= 0) {
                etMonthlyIncome.setError(getString(R.string.error_income_invalid));
                etMonthlyIncome.requestFocus();
                AnimationHelper.shakeAnimation(etMonthlyIncome);
                return false;
            }
        } catch (NumberFormatException e) {
            etMonthlyIncome.setError(getString(R.string.error_income_invalid));
            etMonthlyIncome.requestFocus();
            AnimationHelper.shakeAnimation(etMonthlyIncome);
            return false;
        }

        return true;
    }

    /**
     * Muestra diálogo para cambiar contraseña
     */
    private void showChangePasswordDialog() {
        // TODO: Implementar cambio de contraseña
        Toast.makeText(this, R.string.feature_coming_soon, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra diálogo de confirmación para eliminar cuenta
     */
    private void showDeleteAccountDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_account_title);
        builder.setMessage(R.string.delete_account_message);
        builder.setPositiveButton(R.string.delete, (dialog, which) -> deleteAccount());
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    /**
     * Elimina la cuenta del usuario
     */
    private void deleteAccount() {
        boolean success = userController.deleteCurrentUser();

        if (success) {
            preferencesManager.clearAll();
            navigateToOnboarding();
        } else {
            showError(getString(R.string.error_deleting_account));
        }
    }

    /**
     * Navega al onboarding después de eliminar la cuenta
     */
    private void navigateToOnboarding() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Muestra mensaje de éxito
     *
     * @param message Mensaje a mostrar
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra mensaje de error
     *
     * @param message Mensaje a mostrar
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Ejecutamos la lógica de ir atrás
            onBackPressedDispatcher();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setupBackNavigation() {

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressedDispatcher();
            }
        });
    }
}