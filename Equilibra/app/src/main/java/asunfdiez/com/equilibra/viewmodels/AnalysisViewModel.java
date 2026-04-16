// Nuevo archivo: viewmodels/AnalysisViewModel.java
package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.models.AnalysisState;
import asunfdiez.com.equilibra.models.CategoryAnalysis;

public class AnalysisViewModel extends AndroidViewModel {

    private final ExpenseController expenseController;
    private final MutableLiveData<AnalysisState> analysisState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isWeekly = new MutableLiveData<>(true);

    private final MutableLiveData<Boolean> pieChartData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> barChartData = new MutableLiveData<>(false);

    public AnalysisViewModel(@NonNull Application application) {
        super(application);
        // Nota: En un futuro, el Controller debería ser reemplazado por un Repository según agents.md
        this.expenseController = new ExpenseController(application);
        loadData();
    }

    public LiveData<AnalysisState> getAnalysisState() {
        return analysisState;
    }

    public LiveData<Boolean> getIsWeekly() {
        return isWeekly;
    }

    public LiveData<Boolean> getPieChartData() {
        return pieChartData;
    }

    public LiveData<Boolean> getBarChartData() {
        return barChartData;
    }


    public void setPeriod(boolean weekly) {
        if (this.isWeekly.getValue() != null && this.isWeekly.getValue() != weekly) {
            this.isWeekly.setValue(weekly);
            loadData();
        }
    }

    public void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            long start, end;
            Calendar cal = Calendar.getInstance();
            end = cal.getTimeInMillis();

            Boolean isWeeklyVal = isWeekly.getValue();
            boolean weekly = isWeeklyVal != null && isWeeklyVal;

            if (weekly) {
                // Mover al inicio de la semana el lunes a las 00:00:00
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();

                // Mover al final de la semana el domingo a las 23:59:59
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                end = cal.getTimeInMillis();
            } else {
                // Mover al inicio del mes a las 00:00:00
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();

                // Mover al final del mes a las 23:59:59
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                end = cal.getTimeInMillis();
            }

            double totalExpenses = expenseController.getTotalExpensesInRange(start, end);
            double totalIncome = expenseController.getTotalIncomeInRange(start, end);
            double savings = Math.max(0, totalIncome - totalExpenses);

            List<CategoryAnalysis> pieEntries = expenseController.getTopCategoriesAnalysis(start, end, 5);
            pieChartData.postValue(!pieEntries.isEmpty());

            double[] barValues = weekly ? expenseController.getWeeklyExpenses(start) : expenseController.getMonthlyExpenses(start);
            
            // Verificar si hay datos significativos (mayores a cero)
            boolean hasBarData = false;
            if (barValues != null) {
                for (double val : barValues) {
                    if (val > 0) {
                        hasBarData = true;
                        break;
                    }
                }
            }

            barChartData.postValue(hasBarData);

            analysisState.postValue(new AnalysisState(totalExpenses, totalIncome, savings, pieEntries, barValues));

        });
    }


}

