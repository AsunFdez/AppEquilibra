package asunfdiez.com.equilibra.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.CategoryAdapter;
import asunfdiez.com.equilibra.databinding.ActivityAddExpenseBinding;
import asunfdiez.com.equilibra.databinding.DialogConfirmExpenseBinding;
import asunfdiez.com.equilibra.databinding.DialogImportantExpenseBinding;
import asunfdiez.com.equilibra.databinding.DialogSmartReminderBinding;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.viewmodels.AddExpenseViewModel;

/**
 * AddExpenseActivity - Pantalla para añadir gastos con MVVM
 * Incluye detección inteligente de patrones y alertas
 */
public class AddExpenseActivity extends AppCompatActivity {

    // View Binding
    private ActivityAddExpenseBinding binding;

    // ViewModel
    private AddExpenseViewModel viewModel;

    // Adapter
    private CategoryAdapter categoryAdapter;

    // Diálogos
    private Dialog smartReminderDialog;
    private Dialog confirmExpenseDialog;
    private Dialog importantExpenseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_expense);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AddExpenseViewModel.class);

        // Configurar UI
        setupToolbar();
        setupCategoryRecyclerView();

        // Vincular ViewModel con el binding
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupListeners();
        setupObservers();
        applyAnimations();
    }

    /**
     * Configura el Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Añadir Gasto");
        }

        binding.toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    /**
     * Configura el RecyclerView de categorías
     */
    private void setupCategoryRecyclerView() {
        // Configurar GridLayoutManager horizontal con 2 filas (para matriz 3x2 por scroll)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        binding.rvCategories.setLayoutManager(layoutManager);

        // Añadir PagerSnapHelper para que el scroll sea por "páginas" de la matriz
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvCategories);

        categoryAdapter = new CategoryAdapter(this, category -> {
            // Al hacer click en una categoría
            viewModel.selectCategory(category);
            AnimationHelper.applyScaleAnimation(binding.rvCategories);
        });

        binding.rvCategories.setAdapter(categoryAdapter);
    }

    /**
     * Configura los listeners de los botones y campos de texto
     */
    private void setupListeners() {
        // Botón guardar
        binding.btnSaveExpense.setOnClickListener(v -> {
            AnimationHelper.applyScaleAnimation(v);
            viewModel.saveExpense();
        });

        // Descripción
        binding.etDescription.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.updateDescription(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Asegurar que el teclado numérico se muestra al hacer focus en el monto
        binding.etAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                AnimationHelper.applyScaleAnimation(binding.cardAmount);
            }
        });
    }

    /**
     * Configura los observadores de LiveData
     */
    private void setupObservers() {
        // Observar cuando se guarda el gasto
        viewModel.getExpenseSaved().observe(this, saved -> {
            if (saved != null && saved) {
                Toast.makeText(this, "Gasto guardado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Observar errores
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar Smart Reminder
        viewModel.getShowSmartReminder().observe(this, reminder -> {
            if (reminder != null) {
                showSmartReminderDialog(reminder);
            }
        });

        // Observar confirmación de gasto
        viewModel.getShowConfirmExpense().observe(this, show -> {
            if (show != null && show) {
                showConfirmExpenseDialog();
            }
        });

        // Observar gasto importante
        viewModel.getShowImportantExpense().observe(this, show -> {
            if (show != null && show) {
                showImportantExpenseDialog();
            }
        });
    }

    /**
     * Aplica animaciones iniciales
     */
    private void applyAnimations() {
        AnimationHelper.fadeIn(binding.cardAmount);
        AnimationHelper.slideInRight(binding.rvCategories);
    }

    /**
     * Muestra el diálogo de Smart Reminder (Recordatorio Inteligente)
     */
    private void showSmartReminderDialog(SmartReminder reminder) {
        if (smartReminderDialog != null && smartReminderDialog.isShowing()) {
            smartReminderDialog.dismiss();
        }

        smartReminderDialog = new Dialog(this);
        smartReminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        smartReminderDialog.setCancelable(false);

        // Inflar el layout con Data Binding
        DialogSmartReminderBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_smart_reminder,
                null,
                false
        );

        dialogBinding.setReminder(reminder);
        smartReminderDialog.setContentView(dialogBinding.getRoot());

        // Configurar ventana del diálogo
        Window window = smartReminderDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Mostrar sugerencias
        List<String> suggestions = reminder.getSuggestions();
        if (suggestions != null && !suggestions.isEmpty()) {
            if (suggestions.size() >= 1) {
                dialogBinding.layoutSuggestion1.setVisibility(View.VISIBLE);
                dialogBinding.tvSuggestion1.setText(suggestions.get(0));
            }
            if (suggestions.size() >= 2) {
                dialogBinding.layoutSuggestion2.setVisibility(View.VISIBLE);
                dialogBinding.tvSuggestion2.setText(suggestions.get(1));
            }
        }

        // Configurar botones
        dialogBinding.btnAdjust.setOnClickListener(v -> {
            smartReminderDialog.dismiss();
            viewModel.resetDialogStates();
        });

        dialogBinding.btnConfirm.setOnClickListener(v -> {
            smartReminderDialog.dismiss();
            viewModel.resetDialogStates();
            viewModel.saveExpenseDirectly();
        });

        smartReminderDialog.show();
    }

    /**
     * Muestra el diálogo de confirmación de gasto (Intervención suave)
     */
    private void showConfirmExpenseDialog() {
        if (confirmExpenseDialog != null && confirmExpenseDialog.isShowing()) {
            confirmExpenseDialog.dismiss();
        }

        confirmExpenseDialog = new Dialog(this);
        confirmExpenseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmExpenseDialog.setCancelable(false);

        // Inflar el layout con Data Binding
        DialogConfirmExpenseBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_confirm_expense,
                null,
                false
        );

        // Configurar datos
        String amount = viewModel.currentAmount.getValue();
        ExpenseCategory category = viewModel.getSelectedCategory().getValue();
        Integer percentage = viewModel.getBudgetPercentage().getValue();

        dialogBinding.setAmount(amount);
        dialogBinding.setCategory(category != null ? category.getName() : "");
        dialogBinding.setPercentage(percentage != null ? percentage : 0);

        confirmExpenseDialog.setContentView(dialogBinding.getRoot());

        // Configurar ventana del diálogo
        Window window = confirmExpenseDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Configurar botones
        dialogBinding.btnCancel.setOnClickListener(v -> {
            confirmExpenseDialog.dismiss();
            viewModel.resetDialogStates();
        });

        dialogBinding.btnConfirmExpense.setOnClickListener(v -> {
            confirmExpenseDialog.dismiss();
            viewModel.resetDialogStates();
            viewModel.saveExpenseDirectly();
        });

        confirmExpenseDialog.show();
    }

    /**
     * Muestra el diálogo de gasto importante
     */
    private void showImportantExpenseDialog() {
        if (importantExpenseDialog != null && importantExpenseDialog.isShowing()) {
            importantExpenseDialog.dismiss();
        }

        importantExpenseDialog = new Dialog(this);
        importantExpenseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        importantExpenseDialog.setCancelable(false);

        // Inflar el layout con Data Binding
        DialogImportantExpenseBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_important_expense,
                null,
                false
        );

        // Configurar datos
        String amount = viewModel.currentAmount.getValue();
        ExpenseCategory category = viewModel.getSelectedCategory().getValue();
        String impact = viewModel.getGoalImpact().getValue();

        dialogBinding.setAmount(amount);
        dialogBinding.setCategory(category != null ? category.getName() : "");
        dialogBinding.setImpact(impact != null ? impact : "");

        importantExpenseDialog.setContentView(dialogBinding.getRoot());

        // Configurar ventana del diálogo
        Window window = importantExpenseDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Mostrar alternativas
        List<String> alternatives = viewModel.getAlternatives().getValue();
        if (alternatives != null && !alternatives.isEmpty()) {
            if (alternatives.size() >= 1) {
                dialogBinding.layoutAlt1.setVisibility(View.VISIBLE);
                dialogBinding.tvAlt1.setText(alternatives.get(0));
            }
            if (alternatives.size() >= 2) {
                dialogBinding.layoutAlt2.setVisibility(View.VISIBLE);
                dialogBinding.tvAlt2.setText(alternatives.get(1));
            }
            if (alternatives.size() >= 3) {
                dialogBinding.layoutAlt3.setVisibility(View.VISIBLE);
                dialogBinding.tvAlt3.setText(alternatives.get(2));
            }
        }

        // Configurar botones
        dialogBinding.btnBuyAnyway.setOnClickListener(v -> {
            importantExpenseDialog.dismiss();
            viewModel.resetDialogStates();
            viewModel.saveExpenseDirectly();
        });

        dialogBinding.btnThinkAboutIt.setOnClickListener(v -> {
            importantExpenseDialog.dismiss();
            viewModel.resetDialogStates();
        });

        dialogBinding.btnSearchAlternatives.setOnClickListener(v -> {
            importantExpenseDialog.dismiss();
            viewModel.resetDialogStates();
            Toast.makeText(this, "Buscando alternativas...", Toast.LENGTH_SHORT).show();
        });

        importantExpenseDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cerrar diálogos si están abiertos
        if (smartReminderDialog != null && smartReminderDialog.isShowing()) {
            smartReminderDialog.dismiss();
        }
        if (confirmExpenseDialog != null && confirmExpenseDialog.isShowing()) {
            confirmExpenseDialog.dismiss();
        }
        if (importantExpenseDialog != null && importantExpenseDialog.isShowing()) {
            importantExpenseDialog.dismiss();
        }
    }
}
