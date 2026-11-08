package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.databinding.ActivitySettingsBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.repositories.CloudSyncRepository;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.CsvExportImportUtils;
import asunfdiez.com.equilibra.utils.DatabaseBackupHelper;
import asunfdiez.com.equilibra.viewmodels.SettingsViewModel;

/**
 * Actividad principal de configuración de la aplicación.
 * 
 * <p>Funciona como un centro de control desde el cual el usuario puede acceder a 
 * diferentes sub-pantallas de ajustes (Apariencia, Presupuesto, Notificaciones), 
 * ver información sobre la app o realizar acciones de mantenimiento de datos.</p>
 */
public class SettingsActivity extends AppCompatActivity implements BottomNavigationHandler  {

    private static final String TAG = "SettingsActivity";

    // Vinculación de Datos (Data Binding)
    private ActivitySettingsBinding binding;

    // Modelo de Vista (ViewModel)
    private SettingsViewModel viewModel;

    private final Handler handler = new Handler();

    private CredentialManager credentialManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> exportCsvLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase db = AppDatabase.getInstance(this);
                            List<Expense> expenses = db.expenseDao().getAllExpenses();
                            boolean success = CsvExportImportUtils.exportExpenses(this, uri, expenses);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, R.string.export_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> importCsvLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            int count = CsvExportImportUtils.importExpenses(this, uri);
                            runOnUiThread(() -> {
                                if (count >= 0) {
                                    String msg = getString(R.string.import_success, count);
                                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, R.string.import_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> exportDatabaseJsonLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            boolean success = DatabaseBackupHelper.exportDatabaseToFile(this, uri);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(this, R.string.export_db_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, R.string.export_db_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> importDatabaseJsonLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        showImportDatabaseConfirmation(uri);
                    }
                }
            }
    );

    private void showImportDatabaseConfirmation(Uri uri) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.import_db_confirm_title)
                .setMessage(R.string.import_db_confirm_message)
                .setPositiveButton(R.string.restore, (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        boolean success = DatabaseBackupHelper.importDatabaseFromFile(this, uri);
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, R.string.import_db_success, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, R.string.import_db_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.setViewModel(viewModel);

        credentialManager = CredentialManager.create(this);

        // Configurar UI
        setupToolbar();
        setupListeners();
        setupObservers();
        setupBottomNavigation();
        initAnimations();
        binding.executePendingBindings();

    }

    private void initAnimations() {
        View[] cards ={binding.cardGoogle, binding.cardProfile, binding.cardBudget, binding.cardNotifications, binding.cardAppearance, binding.cardAbout};
        AnimationHelper.animateCardsEntrance(cards);
    }

    /**
     * Configura el Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Configura los click listeners
     */
    private void setupListeners() {

        // Google
        binding.cardGoogle.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getFirebaseAuthenticated().getValue())) {
                showLogoutConfirmation();
            } else {
                launchGoogleSignIn();
            }
        });

        // Perfil
        binding.cardProfile.setOnClickListener(v -> openProfileSettings());

        // Presupuesto
        binding.cardBudget.setOnClickListener(v -> openBudgetSettings());

        // Notificaciones
        binding.cardNotifications.setOnClickListener(v -> openNotificationSettings());

        // Apariencia
        binding.cardAppearance.setOnClickListener(v -> openAppearanceSettings());

        // Acerca de
        binding.cardAbout.setOnClickListener(v -> openAbout());

        // Botón de limpiar datos
        binding.btnClearData.setOnClickListener(v -> showClearDataDialog());

        // Botón de exportar CSV
        binding.btnExportCsv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, "equilibra_expenses_export.csv");
            exportCsvLauncher.launch(intent);
        });

        // Botón de importar CSV
        binding.btnImportCsv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "text/csv",
                    "text/comma-separated-values",
                    "text/plain",
                    "application/csv",
                    "application/vnd.ms-excel",
                    "text/x-csv"
            });
            importCsvLauncher.launch(intent);
        });

        // Botón de generar dummy y CSV
        binding.btnGenerateDummyCsv.setOnClickListener(v -> showGenerateDummyCsvDialog());

        // Botón de exportar Base de Datos JSON
        binding.btnExportDatabaseJson.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "equilibra_db_backup_" + System.currentTimeMillis() + ".json");
            exportDatabaseJsonLauncher.launch(intent);
        });

        // Botón de importar Base de Datos JSON
        binding.btnImportDatabaseJson.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "application/json",
                    "text/plain",
                    "application/octet-stream"
            });
            importDatabaseJsonLauncher.launch(intent);
        });

        // Botón de resetear app
        binding.btnResetApp.setOnClickListener(v -> showResetAppDialog());
    }

    private void showGenerateDummyCsvDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.generate_dummy_csv_title)
                .setMessage(R.string.generate_dummy_csv_message)
                .setPositiveButton(R.string.generate_dummy_csv, (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.generateDummyCsvAndPopulate(this);
                        runOnUiThread(() -> Toast.makeText(this, R.string.dummy_csv_generated, Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Configura los observadores de LiveData
     */
    private void setupObservers() {
        // Observar tema
        viewModel.getTheme().observe(this, theme -> {
            // Aplicar tema si es necesario
        });
    }

    private void setupBottomNavigation() {
        if (binding.bottomNav != null) {
            BottomNavigationBinding navBinding = DataBindingUtil.getBinding(binding.bottomNav.bottomNavigation);
            if (navBinding != null) {
                navBinding.setActiveTab("settings");
                navBinding.setHandler(this);
            }
        }
    }

    // ========== NAVEGACIÓN ==========

    /**
     * Abre ProfileSettingsActivity
     */
    private void openProfileSettings() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Abre BudgetSettingsActivity
     */
    private void openBudgetSettings() {
        Intent intent = new Intent(this, BudgetSettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Abre NotificationSettingsActivity
     */
    private void openNotificationSettings() {
        Intent intent = new Intent(this, NotificationSettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Abre AppearanceSettingsActivity
     */
    private void openAppearanceSettings() {
        Intent intent = new Intent(this, AppearanceSettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // ========== AUTENTICACIÓN GOOGLE (CREDENTIAL MANAGER) ==========

    private void launchGoogleSignIn() {
        GetCredentialRequest request = viewModel.getGoogleIdTokenRequest();

        credentialManager.getCredentialAsync(this, request, null, executor, new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onResult(GetCredentialResponse result) {
                handleSignInResult(result);
            }

            @Override
            public void onError(GetCredentialException e) {
                Log.e(TAG, "Error de Credential Manager", e);
                runOnUiThread(() -> showMessage(getString(R.string.error_login_failed)));
            }
        });
    }

    private void handleSignInResult(GetCredentialResponse result) {
        runOnUiThread(() -> {
            viewModel.handleGoogleIdTokenResponse(result, new CloudSyncRepository.OnAuthCompleteListener() {
                @Override
                public void onSuccess(String uid) {
                    showMessage(getString(R.string.success_login));
                    viewModel.refreshAuthState();
                }

                @Override
                public void onFailure(Exception e) {
                    showMessage(getString(R.string.error_login_failed));
                }
            });
        });
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.profile_logout_firebase)
                .setMessage(getString(R.string.confirm) + " ¿" + getString(R.string.profile_logout_firebase).toLowerCase() + "?")
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    viewModel.logoutFromFirebase();
                    showMessage(getString(R.string.success_logout));
                    viewModel.refreshAuthState();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // ========== ACCIONES PELIGROSAS ==========

    /**
     * Muestra diálogo para confirmar limpiar datos
     */
    private void showClearDataDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_data_title)
                .setMessage(R.string.clear_data_message)
                .setIcon(R.drawable.ic_alert_soft)
                .setPositiveButton(R.string.clear, (dialog, which) -> {
                    viewModel.clearAllSettings();
                    showMessage(getString(R.string.data_cleared));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Muestra diálogo para confirmar reset completo
     */
    private void showResetAppDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_app_title)
                .setMessage(R.string.reset_app_message)
                .setIcon(R.drawable.ic_fea_triangle_alert)
                .setPositiveButton(R.string.reset, (dialog, which) -> {
                    viewModel.resetApp();
                    showMessage(getString(R.string.app_reset));

                    // Reiniciar app después de 2 segundos
                    new android.os.Handler().postDelayed(() -> {
                        finishAffinity();
                        System.exit(0);
                    }, 2000);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Muestra mensaje de confirmación
     */
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    @Override
    public void onHomeClick() {
        navigateTo(MainActivity.class);
    }

    @Override
    public void onAnalysisClick() { navigateTo(AnalysisActivity.class);}

    @Override
    public void onAddExpenseClick(View view) {
        AnimationHelper.animateFABPress(view);
        handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), Constants.FAB_ANIM_DURATION + 50);
    }

    @Override
    public void onAchievementsClick() { navigateTo(AchievementsActivity.class);}
    @Override
    public void onSettingsClick() {
        binding.contentScroll.smoothScrollTo(0, 0);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.refreshAuthState();
    }
}
