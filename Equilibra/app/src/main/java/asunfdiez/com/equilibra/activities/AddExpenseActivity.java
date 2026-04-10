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

import java.util.Calendar;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.CategoryAdapter;
import asunfdiez.com.equilibra.databinding.ActivityAddExpenseBinding;
import asunfdiez.com.equilibra.databinding.DialogConfirmExpenseBinding;
import asunfdiez.com.equilibra.databinding.DialogImportantExpenseBinding;
import asunfdiez.com.equilibra.databinding.DialogSmartReminderBinding;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.DatePickerHelper;
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

    private DatePickerHelper datePickerHelper;

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
        initDateTimePickers();

        // Vincular ViewModel con el binding
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupListeners();
        setupObservers();
        applyAnimations();

        // Cargar gasto si se pasa un ID (Modo Edición)
        long expenseId = getIntent().getLongExtra("expense_id", -1);
        if (expenseId != -1) {
            viewModel.loadExpense(expenseId);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.edit_expense));
            }
            binding.btnSaveExpense.setText(getString(R.string.save_changes));
        } else {
            binding.btnSaveExpense.setText(getString(R.string.add_expense));
        }
    }

    /**
     * Configura el Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.add_expense));
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

        // --- LÓGICA DEL INDICADOR DE SCROLL ---
        binding.rvCategories.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // CanScrollHorizontally(1) devuelve false si no se puede hacer más scroll a la derecha (fin)
                if (recyclerView.canScrollHorizontally(1)) {
                    binding.ivScrollIndicator.setVisibility(View.VISIBLE);
                } else {
                    binding.ivScrollIndicator.setVisibility(View.GONE);
                }
            }
        });

        // Comprobación inicial: esperar a que el adapter tenga datos para mostrar u ocultar la flecha
        binding.rvCategories.post(() -> {
            if (binding.rvCategories.canScrollHorizontally(1)) {
                binding.ivScrollIndicator.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initDateTimePickers() {
        // Obtenemos el calendario actual del ViewModel
        Calendar currentCal = viewModel.getSelectedDateTime().getValue();

        datePickerHelper = new DatePickerHelper(this);

        // Listener para FECHA
        binding.etDate.setOnClickListener(v -> {
            datePickerHelper.showDatePicker(binding.etDate, viewModel.getSelectedDateTime().getValue(), newCalendar -> {
                // Al actualizar el ViewModel, LiveData notificará a los observadores
                viewModel.setDateTime(newCalendar);
            });
        });

        // Listener para HORA
        binding.etTime.setOnClickListener(v -> {
            datePickerHelper.showTimePicker(binding.etTime, viewModel.getSelectedDateTime().getValue(), newCalendar -> {
                viewModel.setDateTime(newCalendar);
            });
        });


        // Observamos cambios para actualizar la UI si fuera necesario
        viewModel.getSelectedDateTime().observe(this, cal -> {
            binding.etDate.setText(datePickerHelper.formatDate(cal));
            binding.etTime.setText(datePickerHelper.formatTime(cal));
        });
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

        // Asegurar que el teclado numérico se muestra al hacer focus en el importe
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
                Toast.makeText(this, R.string.expense_saved_ok, Toast.LENGTH_SHORT).show();
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
        dialogBinding.setLifecycleOwner(this);
        smartReminderDialog.setContentView(dialogBinding.getRoot());

        // Configurar ventana del diálogo
        Window window = smartReminderDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        dialogBinding.setViewModel(viewModel);
        dialogBinding.setLifecycleOwner(this);

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
        dialogBinding.setViewModel(viewModel);
        dialogBinding.setLifecycleOwner(this);

        importantExpenseDialog.setContentView(dialogBinding.getRoot());

        // Configurar ventana del diálogo
        Window window = importantExpenseDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
