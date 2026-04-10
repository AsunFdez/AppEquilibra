package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;

public class ExpenseListViewModel extends AndroidViewModel {

    private final ExpenseRepository repository;
    private final MutableLiveData<List<Expense>> expenses = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> totalAmount = new MutableLiveData<>("0.00");
    private final MutableLiveData<Integer> totalRecords = new MutableLiveData<>(0);
    private final MutableLiveData<Map<Integer, Boolean>> daysWithExpenses = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<String> currentMonthYear = new MutableLiveData<>("");
    private final Calendar currentMonthCalendar = Calendar.getInstance();

    public ExpenseListViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        updateMonthYear();
        loadData();
    }

    public void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar calendar = (Calendar) currentMonthCalendar.clone();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long endTime = calendar.getTimeInMillis();

            List<Expense> expenseList = repository.getExpensesInRange(startTime, endTime);
            
            // Calcular total solo para gastos (o balance si prefieres)
            double total = 0;
            for (Expense e : expenseList) {
                if (e.isExpense()) {
                    total += e.getAmount();
                }
            }
            
            expenses.postValue(expenseList);
            totalAmount.postValue(String.format("%.2f", total));
            totalRecords.postValue(expenseList.size());
            
            // Procesar días con gastos para el calendario
            Map<Integer, Boolean> daysMap = new HashMap<>();
            Calendar cal = Calendar.getInstance();
            for (Expense e : expenseList) {
                cal.setTimeInMillis(e.getTimestamp());
                daysMap.put(cal.get(Calendar.DAY_OF_MONTH), true);
            }
            daysWithExpenses.postValue(daysMap);
        });
    }

    public void nextMonth() {
        currentMonthCalendar.add(Calendar.MONTH, 1);
        updateMonthYear();
        loadData();
    }

    public void previousMonth() {
        currentMonthCalendar.add(Calendar.MONTH, -1);
        updateMonthYear();
        loadData();
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM 'de' yyyy", new Locale("es", "ES"));
        String monthYear = sdf.format(currentMonthCalendar.getTime());
        currentMonthYear.postValue(monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1));
    }

    public LiveData<String> getCurrentMonthYear() {
        return currentMonthYear;
    }

    public Calendar getCurrentMonthCalendar() {
        return currentMonthCalendar;
    }

    public LiveData<List<Expense>> getExpenses() {
        return expenses;
    }

    public LiveData<String> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<Integer> getTotalRecords() {
        return totalRecords;
    }

    public LiveData<Map<Integer, Boolean>> getDaysWithExpenses() {
        return daysWithExpenses;
    }

    public void deleteExpense(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.deleteExpense(expense);
            loadData();
        });
    }

    public String getCurrentMonthYearStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM 'de' yyyy", new Locale("es", "ES"));
        String monthYear = sdf.format(currentMonthCalendar.getTime());
        return monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1);
    }
}
