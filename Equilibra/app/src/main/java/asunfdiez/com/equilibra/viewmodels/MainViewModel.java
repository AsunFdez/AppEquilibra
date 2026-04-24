package asunfdiez.com.equilibra.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.BudgetRepository;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.SavingGoalRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * ViewModel principal que orquestra el estado global del panel de control.
 * 
 * <p>Coordina la información proveniente de múltiples fuentes de datos (perfil, 
 * presupuesto, gastos y metas de ahorro) para presentar un resumen coherente en 
 * la pantalla principal. Gestiona la lógica de navegación interna y la activación 
 * de avisos de intervención.</p>
 */
public class MainViewModel extends AndroidViewModel {

    // Repositorios para el acceso a las diferentes entidades de datos
    private final UserProfileRepository userProfileRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingGoalRepository savingGoalRepository;

    // Controladores y gestores de utilidades
    private final UserProfileController userProfileController;
    private final SharedPrefsManager prefsManager;

    // Perfil de usuario observado como fuente principal de datos reactivos
    private final LiveData<UserProfile> userProfile;

    // Estados internos de la interfaz de usuario
    private final MutableLiveData<String> _currentView = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _shouldShowIntervention = new MutableLiveData<>();

    // Información financiera activa
    private final LiveData<Budget> activeBudget;
    private final MutableLiveData<SavingGoal> activeSavingGoal = new MutableLiveData<>();
    private final MutableLiveData<Double> currentMonthExpenses = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> budgetProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> daysRemaining = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> goalProgress = new MutableLiveData<>(0);

    private final MutableLiveData<Double> totalExpensesThisMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> totalIncomeThisMonth = new MutableLiveData<>(0.0);

    private final MediatorLiveData<Double> balance = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> usedPercentage = new MediatorLiveData<>();
    private final MutableLiveData<Integer> goalPercentage = new MutableLiveData<>(40); // Mock/Fixed for now


    public MainViewModel(@NonNull Application application) {
        super(application);

        // Inicialización de controladores (Capa de Dominio)
        userProfileController = new UserProfileController(application);

        // Obtenemos el flujo de datos (No el valor estático)
        userProfile = userProfileController.getCurrentUser();

        prefsManager = SharedPrefsManager.getInstance(application);

        userProfileRepository = new UserProfileRepository(application);
        budgetRepository = new BudgetRepository(application);
        expenseRepository = new ExpenseRepository(application);
        savingGoalRepository = new SavingGoalRepository(application);
        activeBudget = budgetRepository.getActiveBudget();

        // Realizar comprobaciones iniciales
        performMaintenance();
    }

    private void performMaintenance() {
        userProfileController.performMaintenance();
    }

    // Getters públicos (Solo lectura)
    public LiveData<UserProfile> getUserProfile() { return userProfile; }
    public LiveData<String> getCurrentView() { return _currentView; }
    public LiveData<Boolean> getShouldShowIntervention() { return _shouldShowIntervention; }


    public void checkPreferenceView() {
        if (_currentView.getValue() == null || !_currentView.getValue().equals(prefsManager.getViewMode().getValue())) {
            _currentView.postValue(prefsManager.getViewMode().getValue());
        }
    }

    public void checkWellbeing() {
        if (_shouldShowIntervention.getValue() == null || _shouldShowIntervention.getValue() != userProfileController.checkWellbeing() ) {
            _shouldShowIntervention.postValue(userProfileController.checkWellbeing());
        }
    }

    public void resetInterventionTrigger() {
        _shouldShowIntervention.setValue(false);
    }


    private void loadFinancialSummary() {
        new Thread(() -> {
            double expenses = expenseRepository.getTotalExpensesThisMonth();

            // Cargar perfil para obtener ID de usuario
            UserProfile profile = userProfileRepository.getUserProfileSync();
            SavingGoal goal = null;
            if (profile != null) {
                goal = savingGoalRepository.getActiveSavingGoalSync(profile.getId());
            }

            // Calcular días restantes del mes
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int today = cal.get(java.util.Calendar.DAY_OF_MONTH);
            int lastDay = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            int remaining = lastDay - today;

            SavingGoal finalGoal = goal;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                currentMonthExpenses.setValue(expenses);
                daysRemaining.setValue(remaining);
                activeSavingGoal.setValue(finalGoal);
                updateProgress();
                updateGoalProgress();
            });
        }).start();
    }

    private void updateProgress() {
        Budget budget = activeBudget.getValue();
        Double expenses = currentMonthExpenses.getValue();
        if (budget != null && budget.getMonthlyBudget() > 0 && expenses != null) {
            int progress = (int) ((expenses / budget.getMonthlyBudget()) * 100);
            budgetProgress.setValue(Math.min(progress, 100));
        }
    }

    private void updateGoalProgress() {
        SavingGoal goal = activeSavingGoal.getValue();
        if (goal != null && goal.getTargetAmount() > 0) {
            int progress = (int) ((goal.getCurrentAmount() / goal.getTargetAmount()) * 100);
            goalProgress.setValue(Math.min(progress, 100));
        }
    }

    private void loadFinancialData() {
        new Thread(() -> {
            double expenses = expenseRepository.getTotalExpensesThisMonth();
            double income = expenseRepository.getTotalIncomeThisMonth();

            // Si el ingreso mensual del perfil es mayor que las transacciones de ingreso, lo usamos como base
            UserProfile profile = userProfileRepository.getUserProfile().getValue(); // Simplificado
            if (income == 0 && profile != null) {
                income = profile.getMonthlyIncome();
            }

            double finalIncome = income;
            double finalExpenses = expenses;

            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                totalIncomeThisMonth.setValue(finalIncome);
                totalExpensesThisMonth.setValue(finalExpenses);
            });
        }).start();
    }

    private void updateBalance() {
        Double income = totalIncomeThisMonth.getValue();
        Double expenses = totalExpensesThisMonth.getValue();
        if (income != null && expenses != null) {
            balance.setValue(income - expenses);
        }
    }

    private void updateUsedPercentage() {
        Double income = totalIncomeThisMonth.getValue();
        Double expenses = totalExpensesThisMonth.getValue();
        if (income != null && income > 0 && expenses != null) {
            usedPercentage.setValue((int) ((expenses / income) * 100));
        } else {
            usedPercentage.setValue(0);
        }
    }

    public LiveData<Budget> getActiveBudget() { return activeBudget; }
    public LiveData<SavingGoal> getActiveSavingGoal() { return activeSavingGoal; }
    public LiveData<Double> getCurrentMonthExpenses() { return currentMonthExpenses; }
    public LiveData<Integer> getBudgetProgress() { return budgetProgress; }
    public LiveData<Integer> getDaysRemaining() { return daysRemaining; }
    public LiveData<Integer> getGoalProgress() { return goalProgress; }
    public LiveData<Double> getBalance() { return balance; }
    public LiveData<Integer> getUsedPercentage() { return usedPercentage; }
    public LiveData<Integer> getGoalPercentage() { return goalPercentage; }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void refreshData() {
        checkPreferenceView();
        checkWellbeing();
    }
}