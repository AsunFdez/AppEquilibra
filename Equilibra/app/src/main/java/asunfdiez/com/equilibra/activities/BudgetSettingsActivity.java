package asunfdiez.com.equilibra.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityBudgetSettingsBinding;
import asunfdiez.com.equilibra.databinding.ItemSelectorPopupBinding;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.viewmodels.BudgetSettingsViewModel;

/**
 * Activity para configurar presupuestos
 *
 */
public class BudgetSettingsActivity extends AppCompatActivity {

    // Data Binding
    private ActivityBudgetSettingsBinding binding;

    // ViewModel
    private BudgetSettingsViewModel viewModel;

    // Flag para prevenir loops en TextWatchers
    private boolean isUpdatingFromViewModel = false;
    private long currentBudgetId = 0;

    private PopupWindow currencySelectorPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_budget_settings);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(BudgetSettingsViewModel.class);
        binding.setViewModel(viewModel);

        // Configurar UI
        setupToolbar();
        setupTextWatchers();
        setupListeners();
        setupObservers();

        // El botón de guardado se gestiona vía LiveData en el layout

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
     * Configura los TextWatchers para actualizar en tiempo real
     */
    private void setupTextWatchers() {
        // TextWatcher para presupuesto mensual
        binding.etMonthlyBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingFromViewModel) return;

                String amountStr = s.toString().trim().replace(',', '.');
                if (amountStr.isEmpty()) return;

                if (viewModel.isValidAmount(amountStr)) {
                    double amount = viewModel.parseAmount(amountStr);
                    // IMPORTANTE: Solo llamar si el valor realmente ha cambiado para evitar bucles de redondeo
                    if (Math.abs(viewModel.getMonthlyAmount().getValue() - amount) > 0.001) {
                        viewModel.calculateFromMonthly(amount);
                    }
                }
            }
        });
    }

    /**
     * Configura los listeners
     */
    private void setupListeners() {
        // Switch para habilitar/deshabilitar presupuesto
        binding.cardBudgetActivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // Solo si es acción del usuario
                viewModel.setBudgetEnabled(isChecked, currentBudgetId);
            }
        });

        // Selector de moneda
        binding.selectorCurrency.setOnClickListener(v -> showCurrencySelector() );

        // Botón guardar
        binding.btnSave.setOnClickListener(v -> saveBudget());
    }

    public void showCurrencySelector() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        String[] currencyModes = getResources().getStringArray(R.array.currency_codes);
        String[] currencyNames = getResources().getStringArray(R.array.currencies);

        for (int i = 0; i < currencyModes.length; i++) {
            final String currencyValue = currencyModes[i];
            ItemSelectorPopupBinding itemBinding = ItemSelectorPopupBinding.inflate(getLayoutInflater(), container, false);
            itemBinding.setText(currencyNames[i]);
            itemBinding.setSelected(currencyValue.equals(viewModel.getCurrency().getValue()));
            if (i == currencyModes.length - 1) itemBinding.divider.setVisibility(View.GONE);
            itemBinding.executePendingBindings();
            itemBinding.getRoot().setOnClickListener(v -> {
                viewModel.updateCurrency(currencyValue, currentBudgetId);
                if (currencySelectorPopup != null) currencySelectorPopup.dismiss();
            });
            container.addView(itemBinding.getRoot());
        }

        currencySelectorPopup = createPopup(container, binding.selectorCurrency);
        binding.selectorCurrency.rotateArrow(180f);
        currencySelectorPopup.setOnDismissListener(() -> binding.selectorCurrency.rotateArrow(0f));

    }
    private PopupWindow createPopup(View content, View anchor) {
        androidx.cardview.widget.CardView cardView = new androidx.cardview.widget.CardView(this);
        cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
        cardView.setCardElevation(12f);
        cardView.addView(content);

        PopupWindow popup = new PopupWindow(this);
        popup.setContentView(cardView);
        popup.setWidth(anchor.getWidth());
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        popup.showAsDropDown(anchor, 0, 10);
        return popup;
    }


    /**
     * Configura los observadores
     */
    private void setupObservers() {
        // Observar presupuesto activo
        viewModel.getBudget().observe(this, budget -> {

            if (budget != null) {
                currentBudgetId = budget.getId();
                isUpdatingFromViewModel = true; // Bloqueamos los textwatchers
                // Solo actualizamos el texto si el campo NO tiene el foco
                // (es decir, viene de la DB, no del teclado del usuario)
                if (!binding.etMonthlyBudget.hasFocus()) {
                    binding.etMonthlyBudget.setText(
                            String.format("%.2f", budget.getMonthlyBudget()));
                }
                binding.etWeeklyBudget.setText(String.format("%.2f", budget.getWeeklyBudget()));
                binding.etDailyBudget.setText(String.format("%.2f", budget.getDailyBudget()));

                binding.setBudget(budget);
                isUpdatingFromViewModel = false;
            }
        });

        // Observar estado de guardado
        viewModel.getIsSaving().observe(this, isSaving -> {
            if (isSaving) {
                binding.btnSave.setText(R.string.saving);
            } else {
                binding.btnSave.setText(R.string.save);
            }
        });

        // Observar mensajes
        viewModel.getSaveMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Observar símbolo de moneda
        viewModel.getCurrencySymbol().observe(this, symbol -> {
            binding.tvCurrencySymbolMonthly.setText(symbol);
            binding.tvCurrencySymbolWeekly.setText(symbol);
            binding.tvCurrencySymbolDaily.setText(symbol);
        });

        viewModel.getMonthlyAmount().observe(this, amount -> {
            if (isUpdatingFromViewModel) return;
            
            // Verificamos si el valor mostrado es diferente al nuevo (considerando precisión)
            String currentText = binding.etMonthlyBudget.getText().toString().replace(',', '.');
            double currentVal = viewModel.parseAmount(currentText);
            
            if (Math.abs(currentVal - amount) > 0.001 && !binding.etMonthlyBudget.hasFocus()) {
                isUpdatingFromViewModel = true;
                binding.etMonthlyBudget.setText(String.format("%.2f", amount));
                isUpdatingFromViewModel = false;
            }
        });

        viewModel.getWeeklyAmount().observe(this, amount -> {
            if (isUpdatingFromViewModel) return;
            isUpdatingFromViewModel = true;
            binding.etWeeklyBudget.setText(String.format("%.2f", amount));
            isUpdatingFromViewModel = false;
        });

        viewModel.getDailyAmount().observe(this, amount -> {
            if (isUpdatingFromViewModel) return;
            isUpdatingFromViewModel = true;
            binding.etDailyBudget.setText(String.format("%.2f", amount));
            isUpdatingFromViewModel = false;
        });
    }

    /**
     * Guarda el presupuesto
     */
    private void saveBudget() {
        String monthlyStr = binding.etMonthlyBudget.getText().toString();
        String weeklyStr = binding.etWeeklyBudget.getText().toString();
        String dailyStr = binding.etDailyBudget.getText().toString();

        if (!viewModel.isValidAmount(monthlyStr)) {
            Snackbar.make(binding.getRoot(),
                    R.string.invalid_monthly_budget, Snackbar.LENGTH_SHORT).show();
            return;
        }

        double monthlyBudget = viewModel.parseAmount(monthlyStr);
        double weeklyBudget = viewModel.parseAmount(weeklyStr);
        double dailyBudget = viewModel.parseAmount(dailyStr);
        String currency = viewModel.getCurrency().getValue();

        Budget budget = new Budget();
        budget.setMonthlyBudget(monthlyBudget);
        budget.setWeeklyBudget(weeklyBudget);
        budget.setDailyBudget(dailyBudget);
        budget.setCurrency(currency);
        budget.setEnabled(binding.cardBudgetActivate.isChecked());
        viewModel.saveBudget(budget);
        binding.etMonthlyBudget.clearFocus();
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
