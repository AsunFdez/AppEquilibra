package asunfdiez.com.equilibra.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityNotificationSettingsBinding;
import asunfdiez.com.equilibra.viewmodels.NotificationSettingsViewModel;

/**
 * Actividad para la configuración de alertas y notificaciones del sistema.
 * 
 * <p>Permite al usuario habilitar o deshabilitar diferentes tipos de recordatorios, 
 * como alertas de presupuesto, recordatorios de registro diario y avisos de logros. 
 * Las preferencias se gestionan a través del ViewModel y se almacenan de forma persistente.</p>
 */
public class NotificationSettingsActivity extends AppCompatActivity {
    
    // Vinculación de Datos (Data Binding)
    private ActivityNotificationSettingsBinding binding;

    // Modelo de Vista (ViewModel)
    private NotificationSettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_settings);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationSettingsViewModel.class);
        binding.setViewModel(viewModel);

        // Configurar UI
        setupToolbar();
        setupListeners();
        setupObservers();

        // Animación de entrada
        binding.getRoot().startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
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
     * Configura los listeners
     */
    private void setupListeners() {
        // Card de notificaciones generales
        binding.cardNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setNotificationsEnabled(isChecked);
            }
        });

        // Card de notificaciones de presupuesto
        binding.cardBudget.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setBudgetNotifications(isChecked);
            }
        });

        // Card de notificaciones de logros
        binding.cardAchievement.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setAchievementNotifications(isChecked);
            }
        });
    }

    /**
     * Configura los observadores
     */
    private void setupObservers() {
        // Nota: Los estados de los switches se gestionan mediante Data Binding 
        // directamente en el XML usando el atributo app:switchChecked

        // Observar mensajes de estado
        viewModel.getStatusMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Observar evento de solicitud de permisos
        viewModel.getRequestPermissionEvent().observe(this, unused -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                androidx.core.app.ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                viewModel.setNotificationsEnabled(true);
            } else {
                Snackbar.make(binding.getRoot(), "Se necesitan permisos para mostrar notificaciones", Snackbar.LENGTH_SHORT).show();
                viewModel.setNotificationsEnabled(false);
            }
        }
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
}