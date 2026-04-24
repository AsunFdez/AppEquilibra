package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.ExpenseDao;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.DateUtils;

/**
 * Repositorio central para la gestión de todas las transacciones de gastos e ingresos.
 * 
 * <p>Este repositorio coordina el acceso a la tabla de gastos, proporcionando 
 * métodos para la inserción, eliminación y consulta filtrada de transacciones. 
 * Además, incluye lógica para obtener resúmenes estadísticos y análisis por 
 * categoría utilizados en los informes financieros.</p>
 */
public class ExpenseRepository {
    // DAO para la ejecución de consultas sobre la entidad Expense
    private final ExpenseDao expenseDao;

    private final Context context;

    // Executor para realizar operaciones de escritura de forma asíncrona
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    /**
     * Constructor que inicializa el repositorio con el contexto de la aplicación.
     */
    public ExpenseRepository(Context context) {
        this.context = context;
        this.expenseDao = AppDatabase.getInstance(context).expenseDao();
    }

    /**
     * Registra una nueva transacción en la base de datos de forma persistente.
     * @param expense Gasto a insertar
     */
    public void insertExpense(Expense expense) {
        executor.execute(() -> expenseDao.insertExpense(expense));
    }

    /**
     * Actualiza un gasto en la base de datos
     * @param expense Gasto a actualizar
     */
    public void updateExpenses(Expense expense) {
        executor.execute(() -> expenseDao.updateExpenses(expense));
    }

    /**
     * Elimina un gasto de la base de datos
     * @param expense Gasto a eliminar
     */
    public void deleteExpense(Expense expense) {
        executor.execute(() -> {
            expenseDao.deleteExpense(expense);});
    }
    /**
     * Elimina todos los gastos de la base de datos
     */
    public void deleteAll() {
        executor.execute(() -> expenseDao.deleteAll());
    }

    /**
     * Obtiene el número total de gastos
     * @return Número total de gastos
     */
    public int getTotalExpenseCount() {
        return expenseDao.getTotalExpenseCount();
    }

    /**
     * Obtiene un gasto por su ID
     * @param id ID del gasto
     * @return Gasto
     */
    public Expense getExpenseById(long id) {
        return expenseDao.getExpenseById(id);
    }

    /**
     * Obtiene todos los gastos
     * @return Lista de gastos
     */
    public List<Expense> getAllExpenses() {
        return expenseDao.getAllExpenses();
    }

    /**
     * Obtiene el número total de gastos
     * @return Número total de gastos
     */
    public int getExpenseCount() {
        return expenseDao.getExpenseCount();
    }

    /**
     * Obtiene los gastos en un rango de fechas
     * @param startTime Fecha de inicio
     * @param endTime Fecha de fin
     * @return Lista de gastos
     */
    public List<Expense> getExpensesInRange(long startTime, long endTime) {
        return expenseDao.getExpensesInRange(startTime, endTime);
    }

    /**
     * Obtiene los gastos con su información de categoría en un rango de fechas
     * @param startTime Fecha de inicio
     * @param endTime Fecha de fin
     * @return Lista de gastos con información de categoría
     */
    public List<ExpenseWithCategory> getExpensesWithCategoryInRange(long startTime, long endTime) {
        return expenseDao.getExpensesWithCategoryInRange(startTime, endTime);
    }

    /**
     * Obtiene todos los gastos del mes actual
     * @return Lista de gastos
     */
    public List<Expense> getExpensesThisMonth() {
        Calendar calendar = Calendar.getInstance();
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
        long endTime = calendar.getTimeInMillis();

        return expenseDao.getExpensesInRange(startTime, endTime);
    }

    /**
     * Obtiene el total de gastos del mes actual
     * @return Total de gastos
     */
    public double getTotalExpensesThisMonth() {
        Calendar calendar = Calendar.getInstance();
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
        long endTime = calendar.getTimeInMillis();

        Double total = expenseDao.getTotalForTypeInRange(Constants.TYPE_EXPENSE, startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de ingresos del mes actual
     * @return Total de ingresos
     */
    public double getTotalIncomeThisMonth() {
        long startTime = DateUtils.getStartOfMonth();
        long endTime = DateUtils.getEndOfMonth();

        Double total = expenseDao.getTotalForTypeInRange(Constants.TYPE_INCOME, startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene la categoría con más gastos en el mes actual
     * @return ID de la categoría
     */
    public long getTopExpenseCategoryThisMonth() {
        return expenseDao.getTopExpenseCategoryInRange(DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth());
    }

    /**
     * Obtiene el total gastado en una categoría específica este mes
     * @param category ID de la categoría
     * @return Total gastado
     */
    public double getCategoryExpensesThisMonth(long category) {
        Double total = expenseDao.getCategoryTotalInRange(category, DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth());
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de gastos de los últimos N días
     * @param days Número de días
     * @return Total de gastos
     */
    public double getExpensesLastNDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        long startTime = calendar.getTimeInMillis();
        Double total = expenseDao.getTotalForTypeSince(Constants.TYPE_EXPENSE, startTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de gastos del mes anterior
     * @return Total de gastos
     */
    public double getTotalExpensesLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // mes anterior
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
        long endTime = calendar.getTimeInMillis();

        Double total = expenseDao.getTotalForTypeInRange(Constants.TYPE_EXPENSE, startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el análisis por categoría del mes actual
     * @return Lista de análisis
     */
    public List<CategoryAnalysis> getCategoryAnalysis() {
        return expenseDao.getCategoryAnalysisSince(Constants.TYPE_EXPENSE, DateUtils.getStartOfMonth());
    }

    /**
     * Obtiene el total de ingresos en un rango
     * @param start Fecha de inicio
     * @param end Fecha de fin
     * @return Total de ingresos
     */
    public double getTotalIncomeInRange(long start, long end) {
        Double total = expenseDao.getTotalInRange(Constants.TYPE_INCOME, start, end);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de gastos en un rango
     * @param start Fecha de inicio
     * @param end Fecha de fin
     * @return Total de gastos
     */
    public double getTotalExpensesInRange(long start, long end) {
        Double total = expenseDao.getTotalInRange(Constants.TYPE_EXPENSE, start, end);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el análisis por categoría (Top N) en un rango
     * @param start Fecha de inicio
     * @param end Fecha de fin
     * @return Lista de análisis
     */
    public List<CategoryAnalysis> getTopCategoriesAnalysis(long start, long end, int limit) {
        return expenseDao.getTopCategoryAnalysisInRange(Constants.TYPE_EXPENSE, start, end, limit);
    }

    /**
     * Obtiene los gastos agrupados por día para la semana actual
     * @param dayToStart Fecha de inicio de la semana
     * @return Lista de gastos agrupados por día
     */
    public double[] getWeeklyExpenses(long dayToStart) {
        double[] weeklyData = new double[7];

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(dayToStart);

        for (int i = 0; i < 7; i++) {
            Calendar dayStart = (Calendar) cal.clone();
            long start = dayStart.getTimeInMillis();

            dayStart.add(Calendar.DAY_OF_MONTH, 1);
            long end = dayStart.getTimeInMillis() - 1;

            Double total = expenseDao.getTotalInRange(Constants.TYPE_EXPENSE, start, end);
            weeklyData[i] = total != null ? total : 0.0;

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weeklyData;
    }

    /**
     * Obtiene los gastos agrupados por mes para el año actual (hasta el mes actual)
     * @param dayToStart Fecha de inicio del año
     * @return Lista de gastos agrupados por mes
     */
    public double[] getMonthlyExpenses(long dayToStart) {
        double[] monthlyData = new double[12];
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dayToStart);
        int currentYear = cal.get(Calendar.YEAR);

        for (int i = 0; i < 12; i++) {
            cal.set(currentYear, i, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long start = cal.getTimeInMillis();

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            long end = cal.getTimeInMillis();

            Double total = expenseDao.getTotalInRange(Constants.TYPE_EXPENSE, start, end);
            monthlyData[i] = total != null ? total : 0.0;
        }
        return monthlyData;
    }

    /**
     * Cuenta el número de gastos realizados en la semana actual (empezando el lunes)
     * @return Número de gastos
     */
    public int getExpensesThisWeekCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfWeek = calendar.getTimeInMillis();

        return expenseDao.countExpensesSince(startOfWeek);
    }

    /**
     * Obtiene el total de gastos realizados
     * @return Total de gastos
     */
    public double getTotalExpenseAmount() {
        return expenseDao.getTotalExpenseAmount();
    }

    /**
     * Cuenta el número de categorías únicas con gastos
     * @return Número de categorías
     */
    public int getUniqueCategoriesCount() {
        return expenseDao.getUniqueCategoriesCount();
    }

    /**
     * Cuenta el número de gastos realizados en una categoría en un rango
     * @param categoryId ID de la categoría
     * @param startOfWeek Fecha de inicio de la semana
     * @return Número de gastos
     */
    public int countCategoryExpensesSince(long categoryId, long startOfWeek) {
        return expenseDao.countCategoryExpensesSince(categoryId, startOfWeek);
    }

    /**
     * Obtiene el total gastado en una categoría en un rango
     * @param categoryId ID de la categoría
     * @param startOfWeek Fecha de inicio de la semana
     * @return Total gastado
     */
    public Double getCategoryTotalInRange(long categoryId, long startOfWeek, long l) {
        return expenseDao.getCategoryTotalInRange(categoryId, startOfWeek, l);
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
