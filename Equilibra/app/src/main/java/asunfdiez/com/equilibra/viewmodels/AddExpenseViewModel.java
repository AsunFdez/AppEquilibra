package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;

/**
 * AddExpenseViewModel - ViewModel para AddExpenseActivity
 * Maneja la lógica de negocio y estados con LiveData
 */
public class AddExpenseViewModel extends AndroidViewModel {

    private ExpenseRepository repository;

    // LiveData para el monto actual
    public MutableLiveData<String> currentAmount = new MutableLiveData<>("");

    // LiveData para la categoría seleccionada
    private MutableLiveData<ExpenseCategory> selectedCategory = new MutableLiveData<>();

    // LiveData para las categorías disponibles
    private MutableLiveData<List<ExpenseCategory>> categories = new MutableLiveData<>();

    // LiveData para la descripción
    private MutableLiveData<String> description = new MutableLiveData<>("");

    // LiveData para validación del formulario
    private MutableLiveData<Boolean> isFormValid = new MutableLiveData<>(false);

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

        // Observar cambios para validar el formulario automáticamente
        currentAmount.observeForever(s -> validateForm());
        selectedCategory.observeForever(category -> validateForm());
    }

    /**
     * Inicializa las categorías desde la base de datos
     */
    private void initializeCategories() {
        List<ExpenseCategory> categoryList = repository.getDefaultCategories();
        categories.setValue(categoryList);
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
     * Formatea el monto para mostrar con símbolo de euro
     * (Usado ahora principalmente para los diálogos de confirmación)
     */
    public String getFormattedAmount() {
        String amount = currentAmount.getValue();
        if (amount == null || amount.isEmpty() || amount.equals("0")) {
            return "0€";
        }
        return amount + "€";
    }

    /**
     * Guarda el gasto - verifica patrones primero
     */
    public void saveExpense() {
        String amount = currentAmount.getValue();
        ExpenseCategory category = selectedCategory.getValue();

        if (amount == null || amount.isEmpty() || category == null) {
            errorMessage.setValue("Completa todos los campos");
            return;
        }

        try {
            double amountValue = Double.parseDouble(amount.replace(',', '.'));

            // 1. Verificar si es un gasto importante (muy alto)
            if (repository.isImportantExpense(amountValue)) {
                goalImpact.setValue(repository.calculateGoalImpact(amountValue));
                alternatives.setValue(repository.generateAlternatives(category.getName(), amountValue));
                showImportantExpense.setValue(true);
                return;
            }

            // 2. Verificar patrones de gastos repetitivos (Smart Reminder)
            SmartReminder reminder = repository.detectExpensePattern(category.getName(), amountValue);
            if (reminder != null) {
                showSmartReminder.setValue(reminder);
                return;
            }

            // 3. Verificar si necesita confirmación
            if (repository.shouldShowConfirmation(amountValue)) {
                budgetPercentage.setValue(repository.calculateBudgetPercentage(amountValue));
                showConfirmExpense.setValue(true);
                return;
            }

            // 4. Si no hay alertas, guardar directamente
            saveExpenseDirectly();

        } catch (NumberFormatException e) {
            errorMessage.setValue("Formato de monto inválido");
        }
    }

    /**
     * Guarda el gasto directamente sin mostrar diálogos
     */
    public void saveExpenseDirectly() {
        String amount = currentAmount.getValue();
        ExpenseCategory category = selectedCategory.getValue();
        String desc = description.getValue();

        if (amount == null || category == null) {
            errorMessage.setValue("Completa todos los campos");
            return;
        }

        try {
            double amountValue = Double.parseDouble(amount.replace(',', '.'));

            Expense expense = new Expense();
            expense.setType("expense");
            expense.setAmount(amountValue);
            expense.setCategory(category.getName());
            expense.setDescription(desc != null ? desc : "");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            expense.setDate(sdf.format(Calendar.getInstance().getTime()));
            expense.setTimestamp(System.currentTimeMillis());

            long id = repository.saveExpense(expense);

            if (id > 0) {
                expenseSaved.setValue(true);
            } else {
                errorMessage.setValue("Error al guardar el gasto");
            }

        } catch (NumberFormatException e) {
            errorMessage.setValue("Formato de monto inválido");
        }
    }

    /**
     * Cancela el guardado del gasto
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
        // Nota: en una app real, si usamos observeForever deberíamos removerlos,
        // pero aquí el ViewModel muere con la Activity, así que es aceptable.
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

    public LiveData<String> getDescription() {
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
