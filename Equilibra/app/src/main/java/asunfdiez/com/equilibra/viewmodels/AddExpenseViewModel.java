package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.SmartReminder;

/**
 * ViewModel que gestiona el estado y la lógica para la creación de nuevos gastos.
 * 
 * <p>Mantiene los datos temporales del formulario de creación, valida la integridad 
 * de la información ingresada y coordina con el controlador de gastos para persistir 
 * los datos. También gestiona la lógica de selección de categorías y fechas.</p>
 */
public class AddExpenseViewModel extends AndroidViewModel {

    // Controlador que gestiona la lógica de negocio de gastos
    private final ExpenseController expenseController;

    // Estado del importe ingresado actualmente (formato String para facilitar el input)
    private MutableLiveData<String> currentAmount = new MutableLiveData<>("");

    // Referencia a la categoría seleccionada por el usuario
    private MutableLiveData<ExpenseCategory> selectedCategory = new MutableLiveData<>();

    // Lista de categorías disponibles para el usuario
    private MutableLiveData<List<ExpenseCategory>> categories = new MutableLiveData<>();

    // Texto descriptivo del gasto
    private MutableLiveData<String> description = new MutableLiveData<>("");

    // Indicador reactivo que habilita o deshabilita la acción de guardar
    private MutableLiveData<Boolean> isFormValid = new MutableLiveData<>(false);

    // ID del gasto actual (si se está editando uno existente)
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

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
        expenseController = new ExpenseController(application);
        initializeCategories();
        // Observar cambios para validar el formulario automáticamente
        currentAmount.observeForever(s -> validateForm());
    }

    /**
     * Carga un gasto existente para editar
     */
    public void loadExpense(long expenseId) {
        if (expenseId <= 0) return;
        this.currentExpenseId = expenseId;

        executor.execute(() -> {
            Expense expense = expenseController.getExpenseById(expenseId);
            if (expense != null) {
                // Actualizar datos en el hilo principal
                new Handler(Looper.getMainLooper()).post(() -> {
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
        executor.execute(() -> {
            try {
                List<ExpenseCategory> categoryList = expenseController.getCategories();
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
                Log.d("AddExpenseViewModel", "Error al validar el importe: " + e.getMessage());
            }
        }

        isFormValid.setValue(valid);
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
            errorMessage.setValue(getApplication().getString(R.string.complete_all_fields));
            return;
        }

        try {
            double amountValue = Double.parseDouble(amount.replace(',', '.'));

            // Realizar comprobaciones en hilo secundario para evitar NetworkOnMainThread o DatabaseOnMainThread
            executor.execute(() -> {
                try {
                    // Verificar si es un gasto importante (muy alto)
                    if (expenseController.isImportantExpense(amountValue)) {
                        String impact = expenseController.calculateGoalImpact(amountValue);
                        List<String> alts = expenseController.generateAlternatives(category.getId(), amountValue, description.getValue());

                        goalImpact.postValue(impact);
                        alternatives.postValue(alts);
                        showImportantExpense.postValue(true);
                        return;
                    }

                    // Verificar patrones de gastos repetitivos (Smart Reminder)
                    SmartReminder reminder = expenseController.detectExpensePattern(category.getId(), amountValue);
                    if (reminder != null) {
                        showSmartReminder.postValue(reminder);
                        return;
                    }

                    // Verificar si necesita confirmación
                    if (expenseController.shouldShowConfirmation(amountValue)) {
                        int percentage = expenseController.calculateBudgetPercentage(amountValue);
                        budgetPercentage.postValue(percentage);
                        showConfirmExpense.postValue(true);
                        return;
                    }

                    // Si no hay alertas, guardar directamente
                    saveExpenseDirectly();
                } catch (Exception e) {
                    Log.e("AddExpenseViewModel", "Error al guardar gasto: " + e.getMessage());
                    errorMessage.postValue(getApplication().getString(R.string.saving_expense_error));
                }
            });

        } catch (NumberFormatException e) {
            errorMessage.setValue(getApplication().getString(R.string.invalid_input_number));
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
            errorMessage.setValue(getApplication().getString(R.string.complete_all_fields));
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

            expense.setDate(sdfDate.format( (calendar != null) ? calendar.getTime() : null ));
            expense.setTime(sdfTime.format( (calendar != null) ? calendar.getTime() : null ));
            expense.setTimestamp( (calendar!=null) ? calendar.getTimeInMillis() : null );

            expenseController.saveExpense(expense);
            expenseSaved.postValue(true);

        } catch (NumberFormatException e) {
            errorMessage.setValue(getApplication().getString(R.string.invalid_input_number));
        }

        checkAchievements();
    }

    private void checkAchievements () {
        executor.execute(expenseController::checkAchievements);
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

    public void consumeExpenseSavedEvent() {
        expenseSaved.setValue(null);
    }
}
