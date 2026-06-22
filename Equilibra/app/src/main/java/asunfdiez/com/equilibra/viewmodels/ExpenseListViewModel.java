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

/**
 * ViewModel que gestiona la lógica de visualización y filtrado del historial de gastos.
 * 
 * <p>Mantiene el estado de la lista de gastos filtrada, calcula totales acumulados 
 * y gestiona el calendario de navegación. Proporciona datos reactivos sobre qué 
 * días del mes contienen registros para facilitar la visualización en el calendario.</p>
 */
public class ExpenseListViewModel extends AndroidViewModel {

    // Controlador para operaciones CRUD y consultas de gastos
    private final ExpenseController controller;
    
    // Lista de gastos enriquecidos con su categoría para mostrar en la UI
    private final MutableLiveData<List<ExpenseWithCategory>> expenses = new MutableLiveData<>(new ArrayList<>());
    
    // Importe total calculado de los gastos en el periodo o día seleccionado
    private final MutableLiveData<String> totalAmount = new MutableLiveData<>("0.00");
    
    // Conteo total de transacciones registradas
    private final MutableLiveData<Integer> totalRecords = new MutableLiveData<>(0);
    
    // Mapa que indica qué días del mes tienen gastos (para el calendario)
    private final MutableLiveData<Map<Integer, Boolean>> daysWithExpenses = new MutableLiveData<>(new HashMap<>());
    
    // Texto descriptivo del mes y año visualizados
    private final MutableLiveData<String> currentMonthYear = new MutableLiveData<>("");
    
    // Referencia temporal del mes actual para navegación por calendario
    private final Calendar currentMonthCalendar = Calendar.getInstance();
    
    // Marca de tiempo del día seleccionado para el filtrado detallado
    private final MutableLiveData<Long> selectedDateTimestamp = new MutableLiveData<>(null);

    // Tipo de transacción a visualizar: true para gastos, false para ingresos
    private final MutableLiveData<Boolean> isExpenseMode = new MutableLiveData<>(true);

    /**
     *Constructor
     * @param application
     */
    public ExpenseListViewModel(@NonNull Application application) {
        super(application);
        controller = new ExpenseController(application);
        updateMonthYear();
        loadData();
    }


    /**
     * Carga la lista de gastos, por defecto los datos del mes actual
     * 
     */
    public void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ExpenseWithCategory> expenseList = getExpensesCurrentMonth();
            Boolean isExpense = isExpenseMode.getValue();
            
            // Filtrar la lista del mes por el tipo seleccionado (gasto/ingreso)
            List<ExpenseWithCategory> typeFilteredList = new ArrayList<>();
            for (ExpenseWithCategory e : expenseList) {
                if (isExpense != null && e.getExpense().isExpense() == isExpense) {
                    typeFilteredList.add(e);
                }
            }

            List<ExpenseWithCategory> filteredList = getExpensesFilteredByDay(typeFilteredList);
            
            // Calcular total solo para la selección filtrada
            double total = 0;
            for (ExpenseWithCategory e : filteredList) {
                total += e.getExpense().getAmount();
            }

            expenses.postValue(filteredList);
            totalAmount.postValue(String.format("%.2f", total));
            totalRecords.postValue(filteredList.size());

            setDaysWithExpenses(typeFilteredList);
        });
    }

    /**
     * Guarda en el mapa de días los días que tienen gastos de una lista dada
     * @param expenseList lista de gastos con la que trabajar
     */
    private void setDaysWithExpenses(List<ExpenseWithCategory> expenseList) {
        // Procesar días con gastos para el calendario (siempre sobre la lista completa del mes)
        Map<Integer, Boolean> daysMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        for (ExpenseWithCategory e : expenseList) {
            cal.setTimeInMillis(e.getExpense().getTimestamp());
            daysMap.put(cal.get(Calendar.DAY_OF_MONTH), true);
        }
        daysWithExpenses.postValue(daysMap);
    }

    /**
     * Filtra de una lista de gastos los del día seleccionado
     * @param expenseList lista de gastos a filtrar
     * @return lista de gastos filtrados
     */
    @NonNull
    private List<ExpenseWithCategory> getExpensesFilteredByDay(List<ExpenseWithCategory> expenseList) {
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
        return filteredList;
    }

    /**
     * Obtiene la lista de gastos con su categoría del mes corriente
     * @return lista de gastos con categoría
     */
    private List<ExpenseWithCategory> getExpensesCurrentMonth() {
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
        return expenseList;
    }

    /**
     * Marca el día dado y carga los datos de ese día
     * @param day día seleccionado
     *
     */
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

    /**
     * Moverse al mes siguiente y cargar datos
     */
    public void nextMonth() {
        currentMonthCalendar.add(Calendar.MONTH, 1);
        updateMonthYear();
        loadData();
    }

    /**
     * Moverse al mes anterior y cargar datos
     */
    public void previousMonth() {
        currentMonthCalendar.add(Calendar.MONTH, -1);
        updateMonthYear();
        loadData();
    }

    /**
     * Actualizar mes y año del currentMonthCalendar
     */
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

    public void setIsExpenseMode(boolean isExpense) {
        isExpenseMode.setValue(isExpense);
        loadData();
    }

    public LiveData<Boolean> getIsExpenseMode() {
        return isExpenseMode;
    }

    /**
     * Borra un gasto concreto y recarga los datos
     * @param expense gasto a borrar
     */
    public void deleteExpense(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() -> {
            controller.deleteExpense(expense, this::loadData);
        });
    }

    public String getCurrentMonthYearStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM 'de' yyyy", new Locale("es", "ES"));
        String monthYear = sdf.format(currentMonthCalendar.getTime());
        return monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1);
    }
}
