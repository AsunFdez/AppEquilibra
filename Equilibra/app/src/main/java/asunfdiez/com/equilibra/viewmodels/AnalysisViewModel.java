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
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                start = cal.getTimeInMillis();
            } else {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                start = cal.getTimeInMillis();
            }

            double totalExpenses = expenseController.getTotalExpensesInRange(start, end);
            double totalIncome = expenseController.getTotalIncomeInRange(start, end);
            double savings = Math.max(0, totalIncome - totalExpenses);

            List<CategoryAnalysis> pieEntries = expenseController.getTopCategoriesAnalysis(start, end, 5);
            double[] barValues = weekly ? expenseController.getWeeklyExpenses() : expenseController.getMonthlyExpenses();

            analysisState.postValue(new AnalysisState(totalExpenses, totalIncome, savings, pieEntries, barValues));
        });
    }
}