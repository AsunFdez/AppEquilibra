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

import asunfdiez.com.equilibra.controllers.AnalyticsController;
import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.AnalysisState;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.UserProfile;

/**
 * ViewModel que gestiona la lógica de análisis financiero y datos estadísticos.
 * 
 * <p>Coordina la obtención y el procesamiento de los datos de gastos para generar 
 * el estado de análisis, el cual incluye los totales para los gráficos de barras 
 * y circulares. Permite alternar entre vistas semanales y mensuales.</p>
 */
public class AnalysisViewModel extends AndroidViewModel {

    // Controlador de análisis para acceder a los datos de la base de datos
    private final AnalyticsController analyticsController;

    private final UserProfileController userProfileController;
    
    // Estado consolidado del análisis (totales, porcentajes, datos de gráficos)
    private final MutableLiveData<AnalysisState> analysisState = new MutableLiveData<>();
    
    // Indicador de la escala temporal del análisis (verdadero para semanal)
    private final MutableLiveData<Boolean> isWeekly = new MutableLiveData<>(true);

    // Estados de carga o disponibilidad de datos para los gráficos
    private final MutableLiveData<Boolean> pieChartData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> barChartData = new MutableLiveData<>(false);

    private final MutableLiveData<String[]> chartLabels = new MutableLiveData<>();
    private final MutableLiveData<Double[]> chartValues = new MutableLiveData<>();

    public AnalysisViewModel(@NonNull Application application) {
        super(application);
        this.analyticsController = new AnalyticsController(application);
        this.userProfileController = new UserProfileController(application);
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

    public LiveData<String[]> getChartLabels() {
        return chartLabels;
    }

    public LiveData<Double[]> getChartValues() {
        return chartValues;
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

            double totalExpenses = analyticsController.getTotalExpensesInRange(start, end);
            double totalIncome = analyticsController.getTotalIncomeInRange(start, end);

            // Si el totalIncome es 0, intentar usar el ingreso mensual del perfil
            if (totalIncome == 0) {
                UserProfile profile = userProfileController.getUserProfileSync();
                if (profile != null) totalIncome = profile.getMonthlyIncome();
                if (weekly) totalIncome = totalIncome /4;
            }

            double savings = Math.max(0, totalIncome - totalExpenses);

            // Obtener todos los análisis de categoría para agrupar el "Resto"
            List<CategoryAnalysis> allEntries = analyticsController.getTopCategoriesAnalysis(start, end, 100); // Suficientemente grande para traer todos
            List<CategoryAnalysis> pieEntries;

            if (allEntries.size() > 5) {
                pieEntries = new java.util.ArrayList<>(allEntries.subList(0, 5));
                double otherAmount = 0;
                int otherCount = 0;
                for (int i = 5; i < allEntries.size(); i++) {
                    otherAmount += allEntries.get(i).getAmount();
                    otherCount += allEntries.get(i).getCount();
                }
                CategoryAnalysis others = new CategoryAnalysis();
                others.setCategory(getApplication().getString(asunfdiez.com.equilibra.R.string.rest_category));
                others.setAmount(otherAmount);
                others.setCount(otherCount);
                pieEntries.add(others);
            } else {
                pieEntries = allEntries;
            }

            // Calcular porcentajes
            for (CategoryAnalysis entry : pieEntries) {
                if (totalExpenses > 0) {
                    entry.setPercentage((entry.getAmount() / totalExpenses) * 100);
                } else {
                    entry.setPercentage(0);
                }
            }

            pieChartData.postValue(!pieEntries.isEmpty());

            double[] barValues = weekly ? analyticsController.getWeeklyExpenses(start) : analyticsController.getMonthlyExpenses(start);
            
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

            if (barValues != null) {
                Double[] barValuesDouble = new Double[barValues.length];
                for (int i = 0; i < barValues.length; i++) {
                    barValuesDouble[i] = barValues[i];
                }
                chartValues.postValue(barValuesDouble);
            }

            String[] labels = weekly
                    ? new String[]{
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_mon),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_tue),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_wed),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_thu),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_fri),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_sat),
                    getApplication().getString(asunfdiez.com.equilibra.R.string.day_sun)}
                    : new String[]{
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
                    getApplication().getString(asunfdiez.com.equilibra.R.string.month_dec)};

            chartLabels.postValue(labels);

            analysisState.postValue(new AnalysisState(totalExpenses, totalIncome, savings, pieEntries, barValues));

        });
    }


}

