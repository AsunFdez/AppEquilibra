package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.BudgetController;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * ViewModel que gestiona la lógica de configuración y cálculo de presupuestos.
 * 
 * <p>Maneja el estado del presupuesto activo, permitiendo la edición de los límites 
 * mensuales y realizando cálculos automáticos para los límites semanales y diarios. 
 * Sincroniza los cambios con el controlador de presupuestos para su persistencia 
 * en la base de datos.</p>
 */
public class BudgetSettingsViewModel extends AndroidViewModel {

    // Controlador para gestionar las operaciones CRUD de presupuestos
    private final BudgetController budgetController;

    // Presupuesto actual observado desde la base de datos
    private final LiveData<Budget> budget;
    
    // Estados de carga y feedback de guardado
    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>(false);
    private final MutableLiveData<String> saveMessage = new MutableLiveData<>();
    
    // Estado de activación del presupuesto consolidado
    private final MediatorLiveData<Boolean> budgetEnabled = new MediatorLiveData<>();

    // Propiedades derivadas (símbolo de moneda y código)
    private final LiveData<String> currencySymbol;
    private final LiveData<String> currency;

    // Valores numéricos del presupuesto para diferentes periodos
    private final MutableLiveData<Double> monthlyAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> weeklyAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> dailyAmount = new MutableLiveData<>(0.0);

    // Indicador de edición activa para prevenir ciclos de actualización infinitos
    private boolean isEditing = false;

    /**
     * Constructor
     */
    public BudgetSettingsViewModel(@NonNull Application application) {
        super(application);

        this.budgetController = new BudgetController(application);

        this.budget = budgetController.getActiveBudget();

        this.currencySymbol = Transformations.map(budget, b -> {
            if (b == null) return "€";
            return b.getCurrencySymbol();
        });

        this.currency = Transformations.map(budget, b -> {
            if (b == null) return Constants.CURRENCY_EUR;
            return b.getCurrency();
        });

        // Vincular el estado enabled del budget con el LiveData budgetEnabled
        this.budgetEnabled.addSource(budget, b -> {
            if (b != null && !isEditing) {
                budgetEnabled.setValue(b.isEnabled());
                monthlyAmount.setValue(b.getMonthlyBudget());
                weeklyAmount.setValue(b.getWeeklyBudget());
                dailyAmount.setValue(b.getDailyBudget());
            }
        });
    }

    // ========== GETTERS LIVEDATA ==========

    public LiveData<Budget> getBudget() {
        return budget;
    }

    public LiveData<Boolean> getIsSaving() {
        return isSaving;
    }

    public LiveData<String> getSaveMessage() {
        return saveMessage;
    }

    public LiveData<Boolean> getBudgetEnabled() {
        return budgetEnabled;
    }

    public LiveData<String> getCurrencySymbol() {
        return currencySymbol;
    }

    public LiveData<String> getCurrency() {
        return currency;
    }
    public LiveData<Double> getMonthlyAmount() {
        return monthlyAmount;
    }
    public LiveData<Double> getWeeklyAmount() {
        return weeklyAmount;
    }
    public LiveData<Double> getDailyAmount() {
        return dailyAmount;
    }


    /**
     * Actualiza la moneda
     */
    public void updateCurrency(String currency, long budgetId) {
        if (currency == null || currency.trim().isEmpty()) {
            saveMessage.setValue(getApplication().getString(R.string.currency_not_valid));
            return;
        }
        if (currency.equals(this.currency.getValue())) {
            saveMessage.setValue(getApplication().getString(R.string.no_changes));
            return;
        }

        isSaving.setValue(true);
        budgetController.updateCurrency(currency, budgetId);

        new android.os.Handler().postDelayed(() -> {
            isSaving.setValue(false);
            saveMessage.setValue(getApplication().getString(R.string.currency_saved));
        }, 500);
    }

    /**
     * Habilita o deshabilita el presupuesto
     */
    public void setBudgetEnabled(boolean enabled, long budgetId) {
        budgetEnabled.setValue(enabled);
        budgetController.setBudgetEnabled(enabled, budgetId);
        saveMessage.setValue(enabled ?
                getApplication().getString(R.string.budget_enabled) : getApplication().getString(R.string.budget_disabled));
    }

    /**
     * Guarda un presupuesto completo
     */
    public void saveBudget(Budget budget) {
        if (budget == null) {
            saveMessage.setValue(getApplication().getString(R.string.save_budget_error));
            return;
        }
        // Obtener el presupuesto actual de la base de datos (del LiveData)
        Budget currentBudget = this.budget.getValue();

        // VALIDACIÓN: Si el presupuesto no ha cambiado, no hacemos nada
        if (currentBudget != null &&
                currentBudget.getMonthlyBudget() == budget.getMonthlyBudget() &&
                currentBudget.getCurrency().equals(budget.getCurrency()) &&
                currentBudget.isEnabled() == budget.isEnabled() ) {
            saveMessage.setValue(getApplication().getString(R.string.no_changes));
            return;
        }

        if (budget.getMonthlyBudget() < 0) {
            saveMessage.setValue(getApplication().getString(R.string.budget_negative));
            return;
        }

        isSaving.setValue(true);
        isEditing = false; // Al guardar, permitimos que la DB refresque los valores si es necesario
        budgetController.saveBudget(budget);

        new android.os.Handler().postDelayed(() -> {
            isSaving.setValue(false);
            saveMessage.setValue(getApplication().getString(R.string.save_budget_success));
        }, 500);
    }

    // ========== VALIDACIÓN ==========

    /**
     * Valida que un importe sea válido
     */
    public boolean isValidAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            return amount >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Convierte un string a double
     */
    public double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Calcula el presupuesto semanal y diario desde el mensual
     * No actualiza la base de datos, solo actualiza datos visualmente
     */

    public void calculateFromMonthly(double amount) {
        isEditing = true;
        monthlyAmount.setValue(amount);
        weeklyAmount.setValue(amount / 4.0);
        dailyAmount.setValue(amount / 30.0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
