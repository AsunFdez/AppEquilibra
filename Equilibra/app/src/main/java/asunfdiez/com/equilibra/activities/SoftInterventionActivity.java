package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivitySoftInterventionBinding;
import asunfdiez.com.equilibra.models.FinancialData;
import asunfdiez.com.equilibra.models.InterventionMessage;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.viewmodels.SoftInterventionViewModel;

/**
 * SoftInterventionActivity - Pantalla de intervención suave
 * Muestra alertas amigables cuando los gastos son altos
 * Ofrece consejos y sugerencias personalizadas
 *
 */

public class SoftInterventionActivity extends AppCompatActivity {

    private ActivitySoftInterventionBinding binding;
    private SoftInterventionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoftInterventionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SoftInterventionViewModel.class);

        setupToolbar();
        setupObservers();
        setupListeners();
        handleBackPressed();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupObservers() {
        // Observar datos financieros
        viewModel.getFinancialData().observe(this, data -> {
            binding.tvExpenseAmount.setText(String.format("$%,.2f", data.expenses));
            int percentage = (int) Math.round(data.percentage);
            binding.tvExpensePercentage.setText(data.income > 0 ?
                    percentage + "% de tu presupuesto mensual" : "No hay datos de ingresos");

            AnimationHelper.animateProgressBar(binding.progressExpense, percentage);
            updateSuggestions(data);
        });

        // Observar mensajes de intervención
        viewModel.getInterventionMessage().observe(this, message -> {
            if (message != null) {
                binding.tvInterventionTitle.setText(message.getTitle());
                binding.tvInterventionSubtitle.setText(message.getMessage());
                updateUIBySeverity(message);
            }
        });
    }

    private void updateSuggestions(FinancialData data) {
        if (data.topCategory != null) {
            binding.tvSuggestion1Title.setText("Revisa tus gastos en " + data.topCategory);
            binding.tvSuggestion1Description.setText(String.format("Has gastado $%.2f en esta categoría", data.topCategoryAmount));
            binding.tvSuggestion2Description.setText(String.format("Podrías ahorrar hasta $%.2f este mes", data.topCategoryAmount * 0.3));
        }
    }

    private void updateUIBySeverity(InterventionMessage message) {
        int colorRes = switch (message.getSeverity()) {
            case "high" -> R.color.error;
            case "medium" -> R.color.equilibra_accent;
            default -> R.color.warning;
        };
        binding.ivInterventionIcon.setColorFilter(getColor(colorRes));
        AnimationHelper.applyScaleAnimation(binding.ivInterventionIcon);
    }

    private void setupListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> closeActivity());
        binding.btnDismiss.setOnClickListener(v -> closeActivity());

        binding.btnViewDetails.setOnClickListener(v -> {
            viewModel.markAsSeen();
            navigateTo(AnalysisActivity.class, "highlight_expenses", true);
        });

        binding.btnAdjustBudget.setOnClickListener(v -> {
            viewModel.markAsSeen();
            navigateTo(SettingsActivity.class, "show_budget_settings", true);
        });
    }

    private void handleBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeActivity();
            }
        });
    }

    private void closeActivity() {
        viewModel.markAsSeen();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void navigateTo(Class<?> cls, String extraKey, boolean extraValue) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(extraKey, extraValue);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadData();
    }
}
