package asunfdiez.com.equilibra.controllers;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

/**
 * Controlador encargado de la lógica de análisis financiero.
 * 
 * <p>Proporciona métodos para calcular estadísticas de gastos, tendencias, 
 * ahorros y análisis por categoría. Encapsula toda la lógica de negocio 
 * relacionada con el análisis de datos financieros del usuario.</p>
 */
public class AnalyticsController {

    private final Context context;
    private final ExpenseRepository expenseRepository;
    private final UserProfileRepository userProfileRepository;

    /**
     * Constructor que inicializa el controlador con las dependencias necesarias.
     */
    public AnalyticsController(Application application) {
        this.context = application.getApplicationContext();
        this.expenseRepository = new ExpenseRepository(application);
        this.userProfileRepository = new UserProfileRepository(application);
    }

    /**
     * Obtiene el total de gastos en un rango de fechas específico.
     * 
     * @param startTime Timestamp inicial del rango (milisegundos)
     * @param endTime Timestamp final del rango (milisegundos)
     * @return Total de gastos en ese período
     */
    public double getTotalExpensesInRange(long startTime, long endTime) {
        Double total = expenseRepository.getTotalExpensesInRange(startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de ingresos en un rango de fechas específico.
     * 
     * @param startTime Timestamp inicial del rango (milisegundos)
     * @param endTime Timestamp final del rango (milisegundos)
     * @return Total de ingresos en ese período
     */
    public double getTotalIncomeInRange(long startTime, long endTime) {
        Double total = expenseRepository.getTotalIncomeInRange(startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Calcula el monto de ahorros (ingresos - gastos).
     * 
     * @param totalIncome Ingreso total del período
     * @param totalExpenses Gastos totales del período
     * @return Monto ahorrado (mínimo 0)
     */
    public double calculateSavings(double totalIncome, double totalExpenses) {
        return Math.max(0, totalIncome - totalExpenses);
    }

    /**
     * Calcula la tasa de ahorro como porcentaje del ingreso.
     * 
     * @param totalIncome Ingreso total del período
     * @param savings Monto ahorrado
     * @return Porcentaje de ahorro (0-100), o 0 si el ingreso es 0
     */
    public double calculateSavingsRate(double totalIncome, double savings) {
        if (totalIncome <= 0) return 0;
        return (savings / totalIncome) * 100;
    }

    /**
     * Obtiene el gasto promedio de todos los gastos en un período.
     * 
     * @param startTime Timestamp inicial
     * @param endTime Timestamp final
     * @return Gasto promedio, o 0 si no hay gastos
     */
    public double getAverageExpense(long startTime, long endTime) {
        List<Expense> expenses = expenseRepository.getExpensesInRange(startTime, endTime);
        if (expenses == null || expenses.isEmpty()) return 0.0;
        
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total / expenses.size();
    }

    /**
     * Obtiene el gasto máximo registrado en un período.
     * 
     * @param startTime Timestamp inicial
     * @param endTime Timestamp final
     * @return Gasto máximo, o 0 si no hay gastos
     */
    public double getMaxExpense(long startTime, long endTime) {
        List<Expense> expenses = expenseRepository.getExpensesInRange(startTime, endTime);
        if (expenses == null || expenses.isEmpty()) return 0.0;
        
        double max = 0;
        for (Expense expense : expenses) {
            if (expense.getAmount() > max) {
                max = expense.getAmount();
            }
        }
        return max;
    }

    /**
     * Obtiene los gastos desglosados por día de la semana.
     * 
     * @param startOfWeek Timestamp del inicio de la semana (lunes 00:00:00)
     * @return Array de 7 elementos (lunes a domingo) con gastos de cada día
     */
    public double[] getWeeklyExpenses(long startOfWeek) {
        return expenseRepository.getWeeklyExpenses(startOfWeek);
    }

    /**
     * Obtiene los gastos desglosados por día del mes.
     * 
     * @param startOfMonth Timestamp del inicio del mes (1 de mes 00:00:00)
     * @return Array con gastos de cada día del mes (puede tener hasta 31 elementos)
     */
    public double[] getMonthlyExpenses(long startOfMonth) {
        return expenseRepository.getMonthlyExpenses(startOfMonth);
    }

    /**
     * Obtiene el análisis de gastos agrupados por categoría.
     * 
     * @param startTime Timestamp inicial
     * @param endTime Timestamp final
     * @param limit Número máximo de categorías a retornar
     * @return Lista de análisis por categoría (ordenada por monto descendente)
     */
    public List<CategoryAnalysis> getTopCategoriesAnalysis(long startTime, long endTime, int limit) {
        return expenseRepository.getTopCategoriesAnalysis(startTime, endTime, limit);
    }

    /**
     * Obtiene el análisis de categorías con el agrupamiento de "Resto".
     * 
     * <p>Si hay más de 5 categorías, las primeras 5 se muestran individualmente
     * y el resto se agrupa bajo la etiqueta "Resto".</p>
     * 
     * @param startTime Timestamp inicial
     * @param endTime Timestamp final
     * @param totalExpenses Gasto total del período (para calcular porcentajes)
     * @return Lista de categorías con la agrupación aplicada
     */
    public List<CategoryAnalysis> getGroupedCategoriesAnalysis(long startTime, long endTime, double totalExpenses) {
        List<CategoryAnalysis> allEntries = getTopCategoriesAnalysis(startTime, endTime, 100);
        List<CategoryAnalysis> result = new ArrayList<>();

        if (allEntries.size() > 5) {
            // Agregar las primeras 5 categorías
            result.addAll(allEntries.subList(0, 5));

            // Agrupar el resto
            double otherAmount = 0;
            int otherCount = 0;
            for (int i = 5; i < allEntries.size(); i++) {
                otherAmount += allEntries.get(i).getAmount();
                otherCount += allEntries.get(i).getCount();
            }

            if (otherAmount > 0) {
                CategoryAnalysis others = new CategoryAnalysis();
                others.setCategory(context.getString(asunfdiez.com.equilibra.R.string.rest_category));
                others.setAmount(otherAmount);
                others.setCount(otherCount);
                result.add(others);
            }
        } else {
            result.addAll(allEntries);
        }

        // Calcular porcentajes
        for (CategoryAnalysis entry : result) {
            if (totalExpenses > 0) {
                entry.setPercentage((entry.getAmount() / totalExpenses) * 100);
            } else {
                entry.setPercentage(0);
            }
        }

        return result;
    }

    /**
     * Analiza la tendencia de gastos comparando dos períodos.
     * 
     * @param currentPeriodTotal Gasto total del período actual
     * @param previousPeriodTotal Gasto total del período anterior
     * @return Porcentaje de variación (positivo = aumento, negativo = reducción)
     */
    public double calculateTrend(double currentPeriodTotal, double previousPeriodTotal) {
        if (previousPeriodTotal <= 0) return 0;
        return ((currentPeriodTotal - previousPeriodTotal) / previousPeriodTotal) * 100;
    }

    /**
     * Obtiene la descripción textual del estado financiero del usuario.
     * 
     * @param savingsRate Tasa de ahorro como porcentaje
     * @param trend Tendencia de gastos como porcentaje
     * @return Descripción del estado financiero
     */
    public String getFinancialHealthStatus(double savingsRate, double trend) {
        if (savingsRate >= 30) {
            return context.getString(R.string.excellent_savings);
        } else if (savingsRate >= 20) {
            return context.getString(R.string.good_savings);
        } else if (savingsRate >= 10) {
            return context.getString(R.string.aceptable_savings);
        } else {
            return context.getString(R.string.alert_savings);
        }
    }

    /**
     * Calcula el rango de fechas para la semana actual.
     * 
     * @return Array de 2 elementos [startTime, endTime]
     */
    public long[] getCurrentWeekRange() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        return new long[]{startTime, endTime};
    }

    /**
     * Calcula el rango de fechas para el mes actual.
     * 
     * @return Array de 2 elementos [startTime, endTime]
     */
    public long[] getCurrentMonthRange() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        return new long[]{startTime, endTime};
    }

    /**
     * Obtiene el rango de fechas del período anterior (semana o mes).
     * 
     * @param isWeekly true para semana anterior, false para mes anterior
     * @return Array de 2 elementos [startTime, endTime]
     */
    public long[] getPreviousPeriodRange(boolean isWeekly) {
        Calendar cal = Calendar.getInstance();

        if (isWeekly) {
            // Semana anterior
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startTime = cal.getTimeInMillis();

            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            long endTime = cal.getTimeInMillis();

            return new long[]{startTime, endTime};
        } else {
            // Mes anterior
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startTime = cal.getTimeInMillis();

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            long endTime = cal.getTimeInMillis();

            return new long[]{startTime, endTime};
        }
    }
}
