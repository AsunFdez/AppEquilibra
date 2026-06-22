package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityProfileBinding;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.viewmodels.ProfileViewModel;

/**
 * Actividad que muestra y permite gestionar el perfil del usuario.
 * 
 * <p>Muestra información del usuario, su estado de sincronización de sus datos en la nube
 * y permite la edición de datos personales y eliminación de todos los datos.
 * Utiliza un ViewModel para observar los datos del perfil en tiempo real a través de LiveData.</p>
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Modelo de Vista (ViewModel)
    private ProfileViewModel viewModel;
    
    // Vinculación de Vistas (View Binding)
    private ActivityProfileBinding binding;

    private CredentialManager credentialManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Vincular el ciclo de vida al binding (Importante para LiveData)
        binding.setLifecycleOwner(this);

        binding.setViewModel(viewModel);

        credentialManager = CredentialManager.create(this);
        
        // Observar el usuario y pasarlo al binding
        viewModel.getUserProfile().observe(this, user -> {
            binding.setUser(user);
        });

        setupToolbar();
        setupObservers();
        setupListeners();
        setupBackNavigation();
        AnimationHelper.fadeIn(binding.layoutProfile);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }
        binding.toolbarProfile.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

    }

    private void setupObservers() {
        viewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                binding.setUser(user);
            }
        });

        viewModel.getEditMode().observe(this, enabled -> {
            if (enabled) AnimationHelper.shakeAnimation(binding.etName);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                showMessage(message);
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                showMessage(message);
            }
        });

        viewModel.isAccountDeleted().observe(this, deleted -> {
            if (deleted) {
                navigateToOnboarding();
            }
        });

        // Observar estado de autenticación para forzar refresco del binding
        viewModel.getFirebaseAuthenticated().observe(this, authenticated -> {
            Log.d(TAG, "Estado de Firebase cambiado: " + authenticated);
            binding.executePendingBindings();
        });
    }

    private void setupListeners() {
        binding.ivProfileImage.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            changeProfileImage();
        });

        binding.btnEditProfile.setOnClickListener(v -> viewModel.setEditMode(true));

        binding.btnSaveProfile.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            viewModel.saveProfileChanges();
        });

        binding.btnDeleteAccount.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            showDeleteAccountDialog();
        });


        binding.btnGoogleProfile.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            if (Boolean.TRUE.equals(viewModel.getFirebaseAuthenticated().getValue())) {
                String name = viewModel.getUserProfile().getValue() != null ? viewModel.getUserProfile().getValue().getName() : "";
                String email = viewModel.getFirebaseEmail().getValue();
                viewModel.syncProfileWithGoogle(name, email, null);
            } else {
                showMessage("Inicia sesión en Google primero");
            }
        });

        binding.cardGoogle.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getFirebaseAuthenticated().getValue())) {
                showLogoutConfirmation();
            } else {
                signIn();
            }
        });
    }

    private void signIn() {
        Log.d(TAG, "Iniciando proceso de Google Sign-In con Credential Manager");

        GetCredentialRequest request = viewModel.getGoogleIdTokenRequest();

        credentialManager.getCredentialAsync(this, request, null, executor, new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onResult(GetCredentialResponse result) {
                runOnUiThread(() -> viewModel.loginWithGoogleResponse(result));
            }

            @Override
            public void onError(GetCredentialException e) {
                Log.e(TAG, "Error de Credential Manager", e);
                runOnUiThread(() -> showMessage(getString(R.string.error_login_failed)));
            }
        });
    }


    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.profile_logout_firebase)
                .setMessage(getString(R.string.confirm) + " ¿" + getString(R.string.profile_logout_firebase).toLowerCase() + "?")
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    viewModel.logoutFromFirebase();
                    showConfirmation(getString(R.string.success_logout));
                    viewModel.refreshAuthState();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Muestra mensaje de confirmación
     */
    private void showConfirmation(String message) {
        com.google.android.material.snackbar.Snackbar.make(
                binding.getRoot(), message,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show();
    }

    private void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeProfileImage() {
        showMessage(getString(R.string.feature_coming_soon));
    }

    private void showDeleteAccountDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_account_title));
        builder.setMessage(getString(R.string.delete_account_message));
        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> viewModel.deleteAccount());
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private void navigateToOnboarding() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.handleBackPress(() -> {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
            }
        });
    }
}
