package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;

/**
 * AddExpenseViewModel - ViewModel para AddExpenseActivity
 * Maneja la lógica de negocio y estados con LiveData
 */
public class AddExpenseViewModel extends AndroidViewModel {

    private final Context context;
    private final ExpenseRepository repository;

    // LiveData para el monto actual
    private MutableLiveData<String> currentAmount = new MutableLiveData<>("");

    // LiveData para la categoría seleccionada
    private MutableLiveData<ExpenseCategory> selectedCategory = new MutableLiveData<>();

    // LiveData para las categorías disponibles
    private MutableLiveData<List<ExpenseCategory>> categories = new MutableLiveData<>();

    // LiveData para la descripción
    private MutableLiveData<String> description = new MutableLiveData<>("");

    // LiveData para validación del formulario
    private MutableLiveData<Boolean> isFormValid = new MutableLiveData<>(false);

    private long currentExpenseId = 0;

    public final MutableLiveData<Calendar> selectedDateTime = new MutableLiveData<>(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));

    public LiveData<Calendar> getSelectedDateTime() {
        return selectedDateTime;
    }

    // LiveData para mostrar diálogos
    private MutableLiveData<SmartReminder> showSmartReminder = new MutableLiveData<>();
    private MutableLiveData<Boolean> showConfirmExpense = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> showImportantExpense = new MutableLiveData<>(false);

    // LiveData para el estado de guardado
    private MutableLiveData<Boolean> expenseSaved = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // LiveData para datos de los diálogos
    private MutableLiveData<Integer> budgetPercentage = new MutableLiveData<>(0);
    private MutableLiveData<String> goalImpact = new MutableLiveData<>("");
    private MutableLiveData<List<String>> alternatives = new MutableLiveData<>();

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        initializeCategories();
        this.context = application.getApplicationContext();

        // Observar cambios para validar el formulario automáticamente
        currentAmount.observeForever(s -> validateForm());
        // selectedCategory.observeForever(category -> validateForm()); // Eliminado para evitar validación prematura si se carga un gasto
    }

    /**
     * Carga un gasto existente para editar
     */
    public void loadExpense(long expenseId) {
        if (expenseId <= 0) return;
        this.currentExpenseId = expenseId;

        Executors.newSingleThreadExecutor().execute(() -> {
            Expense expense = repository.getExpenseById(expenseId);
            if (expense != null) {
                // Actualizar datos en el hilo principal
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    currentAmount.setValue(String.valueOf(expense.getAmount()));
                    description.setValue(expense.getDescription());
                    
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(expense.getTimestamp());
                    selectedDateTime.setValue(cal);

                    // Buscar y seleccionar la categoría
                    List<ExpenseCategory> categoryList = categories.getValue();
                    if (categoryList != null) {
                        for (ExpenseCategory cat : categoryList) {
                            if (cat.getId() == expense.getCategory()) {
                                cat.setSelected(true);
                                selectedCategory.setValue(cat);
                                break;
                            }
                        }
                        categories.setValue(categoryList);
                    }
                    
                    validateForm();
                });
            }
        });
    }

    /**
     * Inicializa las categorías desde la base de datos
     */
    private void initializeCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<ExpenseCategory> categoryList = repository.getCategories();
                categories.postValue(categoryList);
            } catch (Exception e) {
                errorMessage.postValue("Error al cargar categorías");
            }
        });
    }

    /**
     * Selecciona una categoría
     */
    public void selectCategory(ExpenseCategory category) {
        // Desmarcar todas las categorías
        List<ExpenseCategory> categoryList = categories.getValue();
        if (categoryList != null) {
            for (ExpenseCategory cat : categoryList) {
                cat.setSelected(false);
            }
        }

        // Marcar la seleccionada
        category.setSelected(true);
        selectedCategory.setValue(category);
        categories.setValue(categoryList);
        validateForm();
    }

    /**
     * Actualiza la descripción
     */
    public void updateDescription(String desc) {
        description.setValue(desc);
    }

    /**
     * Valida el formulario
     */
    public void validateForm() {
        String amount = currentAmount.getValue();
        ExpenseCategory category = selectedCategory.getValue();

        boolean valid = false;

        if (amount != null && !amount.isEmpty() && !amount.equals("0")) {
            try {
                // Reemplazar coma por punto si el teclado del usuario usa coma
                String amountStr = amount.replace(',', '.');
                double amountValue = Double.parseDouble(amountStr);
                valid = amountValue > 0 && category != null;
            } catch (NumberFormatException e) {
                valid = false;
            }
        }

        isFormValid.setValue(valid);
    }

    /**
     * Formatea el importe para mostrar con símbolo de euro
     * (Usado principalmente para los diálogos de confirmación)
     */
    public String getFormattedAmount() {
        String amount = currentAmount.getValue();
        if (amount == null || amount.isEmpty() || amount.equals("0")) {
            return "0€";
        }
        return amount + "€";
    }


    /**
     * Guardala fecha introducida
     */
    public void setDateTime(Calendar calendar) {
        selectedDateTime.setValue(calendar);
    }

    /**
     * Guarda el gasto - verifica patrones primero
     */
    public void saveExpense() {
        String amount = currentAmount.getValue();
        ExpenseCategory category = selectedCategory.getValue();

        if (amount == null || amount.isEmpty() || category == null) {
            errorMessage.setValue(context.getString(R.string.complete_all_fields));
            return;
        }

        try {
            double amountValue = Double.parseDouble(amount.replace(',', '.'));

            // Realizar comprobaciones en hilo secundario para evitar NetworkOnMainThread o DatabaseOnMainThread
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    // 1. Verificar si es un gasto importante (muy alto)
                    if (repository.isImportantExpense(amountValue)) {
                        String impact = repository.calculateGoalImpact(amountValue);
                        List<String> alts = repository.generateAlternatives(category.getId(), amountValue, description.getValue());
                        
                        goalImpact.postValue(impact);
                        alternatives.postValue(alts);
                        showImportantExpense.postValue(true);
                        return;
                    }

                    // 2. Verificar patrones de gastos repetitivos (Smart Reminder)
                    SmartReminder reminder = repository.detectExpensePattern(category.getId(), amountValue);
                    if (reminder != null) {
                        showSmartReminder.postValue(reminder);
                        return;
                    }

                    // 3. Verificar si necesita confirmación
                    if (repository.shouldShowConfirmation(amountValue)) {
                        int percentage = repository.calculateBudgetPercentage(amountValue);
                        budgetPercentage.postValue(percentage);
                        showConfirmExpense.postValue(true);
                        return;
                    }

                    // 4. Si no hay alertas, guardar directamente
                    saveExpenseDirectly();
                } catch (Exception e) {
                    Log.e("AddExpenseViewModel", "Error al guardar gasto: " + e.getMessage());
                    errorMessage.postValue(context.getString(R.string.saving_expense_error));
                }
            });

        } catch (NumberFormatException e) {
            errorMessage.setValue(context.getString(R.string.invalid_input_number));
        }
    }

    /**
     * Guarda el gasto directamente sin mostrar diálogos
     */
    public void saveExpenseDirectly() {
        Calendar calendar = selectedDateTime.getValue();
        String amount = currentAmount.getValue();
        ExpenseCategory category = selectedCategory.getValue();
        String desc = description.getValue();

        if (amount == null || category == null) {
            errorMessage.setValue(context.getString(R.string.complete_all_fields));
            return;
        }

        try {
            double amountValue = Double.parseDouble(amount.replace(',', '.'));

            Expense expense = new Expense();
            expense.setId(currentExpenseId);
            expense.setType("expense");
            expense.setAmount(amountValue);
            expense.setCategory( category.getId() );
            expense.setDescription(desc != null ? desc : "");

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            expense.setDate(sdfDate.format(calendar.getTime()));
            expense.setTime(sdfTime.format(calendar.getTime()));
            expense.setTimestamp(calendar.getTimeInMillis());

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    repository.saveExpense(expense);
                    expenseSaved.postValue(true);
                } catch (Exception e) {
                    errorMessage.postValue("Error al guardar en base de datos: " + e.getMessage());
                }
            });

        } catch (NumberFormatException e) {
            errorMessage.setValue(context.getString(R.string.invalid_input_number));
        }

        checkAchievements();
    }

    private void checkAchievements () {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.checkAchievements();
        });
    }


    /**
     * Cancela guardar el gasto
     */
    public void cancelExpense() {
        currentAmount.setValue("");
        selectedCategory.setValue(null);
        description.setValue("");

        List<ExpenseCategory> categoryList = categories.getValue();
        if (categoryList != null) {
            for (ExpenseCategory cat : categoryList) {
                cat.setSelected(false);
            }
            categories.setValue(categoryList);
        }

        validateForm();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    // Getters para LiveData
    public MutableLiveData<String> getCurrentAmount() {
        return currentAmount;
    }

    public LiveData<ExpenseCategory> getSelectedCategory() {
        return selectedCategory;
    }

    public LiveData<List<ExpenseCategory>> getCategories() {
        return categories;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public LiveData<Boolean> getIsFormValid() {
        return isFormValid;
    }

    public LiveData<SmartReminder> getShowSmartReminder() {
        return showSmartReminder;
    }

    public LiveData<Boolean> getShowConfirmExpense() {
        return showConfirmExpense;
    }

    public LiveData<Boolean> getShowImportantExpense() {
        return showImportantExpense;
    }

    public LiveData<Boolean> getExpenseSaved() {
        return expenseSaved;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getBudgetPercentage() {
        return budgetPercentage;
    }

    public LiveData<String> getGoalImpact() {
        return goalImpact;
    }

    public LiveData<List<String>> getAlternatives() {
        return alternatives;
    }

    public void resetDialogStates() {
        showSmartReminder.setValue(null);
        showConfirmExpense.setValue(false);
        showImportantExpense.setValue(false);
    }
}
