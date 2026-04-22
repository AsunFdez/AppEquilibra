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

import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;

public class ExpenseListViewModel extends AndroidViewModel {

    private final ExpenseController controller;
    private final MutableLiveData<List<ExpenseWithCategory>> expenses = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> totalAmount = new MutableLiveData<>("0.00");
    private final MutableLiveData<Integer> totalRecords = new MutableLiveData<>(0);
    private final MutableLiveData<Map<Integer, Boolean>> daysWithExpenses = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<String> currentMonthYear = new MutableLiveData<>("");
    private final Calendar currentMonthCalendar = Calendar.getInstance();
    private final MutableLiveData<Long> selectedDateTimestamp = new MutableLiveData<>(null);

    public ExpenseListViewModel(@NonNull Application application) {
        super(application);
        controller = new ExpenseController(application);
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

            List<ExpenseWithCategory> expenseList = controller.getExpensesWithCategoryInRange(startTime, endTime);
            
            // Filtrar por fecha seleccionada si existe
            Long selectedTs = selectedDateTimestamp.getValue();
            List<ExpenseWithCategory> filteredList = new ArrayList<>();
            if (selectedTs != null) {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTimeInMillis(selectedTs);
                int day = selectedCal.get(Calendar.DAY_OF_MONTH);
                int month = selectedCal.get(Calendar.MONTH);
                int year = selectedCal.get(Calendar.YEAR);
                
                for (ExpenseWithCategory e : expenseList) {
                    Calendar eCal = Calendar.getInstance();
                    eCal.setTimeInMillis(e.getExpense().getTimestamp());
                    if (eCal.get(Calendar.DAY_OF_MONTH) == day && 
                        eCal.get(Calendar.MONTH) == month && 
                        eCal.get(Calendar.YEAR) == year) {
                        filteredList.add(e);
                    }
                }
            } else {
                filteredList.addAll(expenseList);
            }

            // Calcular total solo para gastos
            double total = 0;
            for (ExpenseWithCategory e : filteredList) {
                if (e.getExpense().isExpense()) {
                    total += e.getExpense().getAmount();
                }
            }
            
            expenses.postValue(filteredList);
            totalAmount.postValue(String.format("%.2f", total));
            totalRecords.postValue(filteredList.size());
            
            // Procesar días con gastos para el calendario (siempre sobre la lista completa del mes)
            Map<Integer, Boolean> daysMap = new HashMap<>();
            Calendar cal = Calendar.getInstance();
            for (ExpenseWithCategory e : expenseList) {
                cal.setTimeInMillis(e.getExpense().getTimestamp());
                daysMap.put(cal.get(Calendar.DAY_OF_MONTH), true);
            }
            daysWithExpenses.postValue(daysMap);
        });
    }

    public void onDateSelected(int day) {
        Calendar selected = (Calendar) currentMonthCalendar.clone();
        selected.set(Calendar.DAY_OF_MONTH, day);
        selected.set(Calendar.HOUR_OF_DAY, 0);
        selected.set(Calendar.MINUTE, 0);
        selected.set(Calendar.SECOND, 0);
        selected.set(Calendar.MILLISECOND, 0);
        
        long newTs = selected.getTimeInMillis();
        if (selectedDateTimestamp.getValue() != null && selectedDateTimestamp.getValue() == newTs) {
            // Deseleccionar si es el mismo día
            selectedDateTimestamp.setValue(null);
        } else {
            selectedDateTimestamp.setValue(newTs);
        }
        loadData();
    }

    public LiveData<Long> getSelectedDateTimestamp() {
        return selectedDateTimestamp;
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

    public LiveData<List<ExpenseWithCategory>> getExpenses() {
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
            controller.deleteExpense(expense);
            loadData();
        });
    }

    public String getCurrentMonthYearStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM 'de' yyyy", new Locale("es", "ES"));
        String monthYear = sdf.format(currentMonthCalendar.getTime());
        return monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1);
    }
}
