package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivitySettingsBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.SettingsViewModel;

/**
 * Activity principal de Configuración
 * Menú de opciones que navega a sub-pantallas
 */
public class SettingsActivity extends AppCompatActivity implements BottomNavigationHandler  {

    private static final String TAG = "SettingsActivity";

    // Data Binding
    private ActivitySettingsBinding binding;

    // ViewModel
    private SettingsViewModel viewModel;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.setViewModel(viewModel);

        // Configurar UI
        setupToolbar();
        setupListeners();
        setupObservers();
        setupBottomNavigation();
        initAnimations();
        binding.executePendingBindings();

    }

    private void initAnimations() {
        View[] cards ={binding.cardProfile, binding.cardBudget, binding.cardNotifications, binding.cardAppearance, binding.cardAbout};
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

        // Botón de resetear app
        binding.btnResetApp.setOnClickListener(v -> showResetAppDialog());
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

    /**
     * Abre AboutActivity
     */
    private void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                    showConfirmation(getString(R.string.data_cleared));
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
                    showConfirmation(getString(R.string.app_reset));

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
    private void showConfirmation(String message) {
        com.google.android.material.snackbar.Snackbar.make(
                binding.getRoot(), message,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show();
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
}
