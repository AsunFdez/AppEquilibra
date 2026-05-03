package asunfdiez.com.equilibra.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import asunfdiez.com.equilibra.controllers.AnalyticsController;
import asunfdiez.com.equilibra.controllers.BudgetController;
import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * ViewModel principal que orquesta el estado global del panel de control.
 * 
 * <p>Coordina la información proveniente de múltiples fuentes de datos (perfil, 
 * presupuesto, gastos y metas de ahorro) para presentar un resumen coherente en 
 * la pantalla principal. Gestiona la lógica de navegación interna y la activación 
 * de avisos de intervención. Integra análisis financieros avanzados para usuarios
 * intermedios y avanzados.</p>
 */
public class MainViewModel extends AndroidViewModel {

    // Controladores
    private final UserProfileController userProfileController;
    private final SharedPrefsManager prefsManager;
    private final BudgetController budgetController;
    private final ExpenseController expenseController;
    private final AnalyticsController analyticsController;

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
    private final MutableLiveData<String> currentMonthName = new MutableLiveData<>("");

    // Datos de análisis financiero para usuarios avanzados
    private final MutableLiveData<Double> monthlyExpensesTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> monthlySavings = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> savingsRate = new MutableLiveData<>(0.0); // Porcentaje
    private final MutableLiveData<Double> monthlyTrend = new MutableLiveData<>(0.0); // Comparación mes anterior
    private final MutableLiveData<String> financialHealthStatus = new MutableLiveData<>("");
    private final MutableLiveData<List<CategoryAnalysis>> topCategoriesAnalysis = new MutableLiveData<>();
    private final MutableLiveData<Double[]> weeklyExpensesChart = new MutableLiveData<>();
    private final MutableLiveData<Double[]> monthlyExpensesChart = new MutableLiveData<>();
    private final MutableLiveData<String[]> weeklyChartLabels = new MutableLiveData<>();
    private final MutableLiveData<String[]> monthlyChartLabels = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasWeeklyData = new MutableLiveData<>(false);


    public MainViewModel(@NonNull Application application) {
        super(application);

        userProfileController = new UserProfileController(application);
        userProfile = userProfileController.getCurrentUser();

        prefsManager = SharedPrefsManager.getInstance(application);
        budgetController = new BudgetController(application);
        expenseController = new ExpenseController(application);
        analyticsController = new AnalyticsController(application);
        activeBudget = budgetController.getActiveBudget();

        // Configurar MediatorLiveData
        balance.addSource(totalIncomeThisMonth, income -> updateBalance());
        balance.addSource(totalExpensesThisMonth, expenses -> updateBalance());

        usedPercentage.addSource(totalIncomeThisMonth, income -> updateUsedPercentage());
        usedPercentage.addSource(totalExpensesThisMonth, expenses -> updateUsedPercentage());

        // Realizar comprobaciones iniciales
        performMaintenance();
        refreshData();
    }

    private void loadAnalysisData() {
        new Thread(() -> {
            long[] currentMonthRange = analyticsController.getCurrentMonthRange();
            long startTime = currentMonthRange[0];
            long endTime = currentMonthRange[1];

            double totalExpenses = analyticsController.getTotalExpensesInRange(startTime, endTime);
            double totalIncome = analyticsController.getTotalIncomeInRange(startTime, endTime);
            
            // Si el totalIncome es 0, intentar usar el ingreso mensual del perfil
            if (totalIncome == 0) {
                UserProfile profile = userProfileController.getUserProfileSync();
                if (profile != null) totalIncome = profile.getMonthlyIncome();
            }

            double savings = analyticsController.calculateSavings(totalIncome, totalExpenses);
            double rate = analyticsController.calculateSavingsRate(totalIncome, savings);
            
            // Comparar con el período anterior (mes anterior) para la tendencia
            long[] prevMonthRange = analyticsController.getPreviousPeriodRange(false);
            double prevExpenses = analyticsController.getTotalExpensesInRange(prevMonthRange[0], prevMonthRange[1]);
            double trend = analyticsController.calculateTrend(totalExpenses, prevExpenses);
            
            String status = analyticsController.getFinancialHealthStatus(rate, trend);
            List<CategoryAnalysis> topCats = analyticsController.getGroupedCategoriesAnalysis(startTime, endTime, totalExpenses);
            
            // Convertir double[] a Double[] para LiveData
            double[] weekly = analyticsController.getWeeklyExpenses(analyticsController.getCurrentWeekRange()[0]);
            Double[] weeklyDouble = new Double[weekly.length];
            boolean hasData = false;
            for(int i=0; i<weekly.length; i++) {
                weeklyDouble[i] = weekly[i];
                if (weeklyDouble[i] > 0) hasData = true;
            }

            String[] labels = new String[]{
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_mon),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_tue),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_wed),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_thu),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_fri),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_sat),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_sun)
            };

            String[] mLabels = new String[]{
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_jan),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_feb),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_mar),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_apr),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_may),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_jun),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_jul),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_aug),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_sep),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_oct),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_nov),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_dec)
            };

            double[] monthly = analyticsController.getMonthlyExpenses(startTime);
            Double[] monthlyDouble = new Double[monthly.length];
            for(int i=0; i<monthly.length; i++) monthlyDouble[i] = monthly[i];

            double finalTotalIncome = totalIncome;
            double finalTotalExpenses = totalExpenses;
            boolean finalHasData = hasData;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                monthlyExpensesTotal.setValue(finalTotalExpenses);
                monthlySavings.setValue(savings);
                savingsRate.setValue(rate);
                monthlyTrend.setValue(trend);
                financialHealthStatus.setValue(status);
                topCategoriesAnalysis.setValue(topCats);
                weeklyExpensesChart.setValue(weeklyDouble);
                weeklyChartLabels.setValue(labels);
                monthlyChartLabels.setValue(mLabels);
                hasWeeklyData.setValue(finalHasData);
                monthlyExpensesChart.setValue(monthlyDouble);
                
                // Actualizar también los valores compartidos
                totalIncomeThisMonth.setValue(finalTotalIncome);
                totalExpensesThisMonth.setValue(finalTotalExpenses);
            });
        }).start();
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
    public void checkDailyBudgetStreak() {
        budgetController.checkDailyBudgetStreak();
    }

    public void resetInterventionTrigger() {
        _shouldShowIntervention.setValue(false);
    }


    private void loadFinancialSummary() {
        new Thread(() -> {
            double expenses = expenseController.getTotalExpensesThisMonth();

            // Cargar perfil para obtener ID de usuario
            UserProfile profile = userProfileController.getUserProfileSync();
            SavingGoal goal = null;
            if (profile != null) {
                goal = userProfileController.getActiveSavingGoalSync(profile.getId());
            }

            // Calcular días restantes del mes
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int today = cal.get(java.util.Calendar.DAY_OF_MONTH);
            int lastDay = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            int remaining = lastDay - today;
            String month = cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault());
            // Capitalizar primera letra
            if (month != null && !month.isEmpty()) {
                month = month.substring(0, 1).toUpperCase() + month.substring(1);
            }

            SavingGoal finalGoal = goal;
            String finalMonth = month;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                currentMonthExpenses.setValue(expenses);
                daysRemaining.setValue(remaining);
                activeSavingGoal.setValue(finalGoal);
                currentMonthName.setValue(finalMonth);
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
            double expenses = expenseController.getTotalExpensesThisMonth();
            double income = expenseController.getTotalIncomeThisMonth();

            // Si el ingreso mensual del perfil es mayor que las transacciones de ingreso, lo usamos como base
            UserProfile profile = userProfileController.getUserProfileSync();
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
    public LiveData<Double> getTotalExpensesThisMonth() { return totalExpensesThisMonth; }
    public LiveData<Double> getTotalIncomeThisMonth() { return totalIncomeThisMonth; }
    public LiveData<Integer> getBudgetProgress() { return budgetProgress; }
    public LiveData<Integer> getDaysRemaining() { return daysRemaining; }
    public LiveData<Integer> getGoalProgress() { return goalProgress; }
    public LiveData<Double> getBalance() { return balance; }
    public LiveData<Integer> getUsedPercentage() { return usedPercentage; }
    public LiveData<Integer> getGoalPercentage() { return goalPercentage; }
    public LiveData<String> getCurrentMonthName() { return currentMonthName; }

    public LiveData<Double> getMonthlyExpensesTotal() { return monthlyExpensesTotal; }
    public LiveData<Double> getMonthlySavings() { return monthlySavings; }
    public LiveData<Double> getSavingsRate() { return savingsRate; }
    public LiveData<Double> getMonthlyTrend() { return monthlyTrend; }
    public LiveData<String> getFinancialHealthStatus() { return financialHealthStatus; }
    public LiveData<List<CategoryAnalysis>> getTopCategoriesAnalysis() { return topCategoriesAnalysis; }
    public LiveData<Double[]> getWeeklyExpensesChart() { return weeklyExpensesChart; }
    public LiveData<Double[]> getMonthlyExpensesChart() { return monthlyExpensesChart; }
    public LiveData<String[]> getWeeklyChartLabels() { return weeklyChartLabels; }
    public LiveData<String[]> getMonthlyChartLabels() { return monthlyChartLabels; }
    public LiveData<Boolean> getHasWeeklyData() { return hasWeeklyData; }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void refreshData() {
        checkPreferenceView();
        checkWellbeing();
        checkDailyBudgetStreak();
        loadFinancialSummary();
        loadFinancialData();
        loadAnalysisData();
    }


}