package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.ExpenseCategoryDao;
import asunfdiez.com.equilibra.database.ExpenseDao;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * ExpenseRepository - Repositorio para gestionar datos de gastos
 * Intermedia entre ViewModel y Room DAOs
 */
public class ExpenseRepository {

    private ExpenseCategoryDao expenseCategoryDao;
    private ExpenseDao expenseDao;
    private Context context;

    public ExpenseRepository(Context context) {
        this.context = context;
        this.expenseCategoryDao = AppDatabase.getInstance(context).expenseCategoryDao();
        this.expenseDao = AppDatabase.getInstance(context).expenseDao();
    }

    /**
     * Guarda un nuevo gasto en la base de datos (Room)
     */
    public void saveExpense(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() -> {
            expenseDao.insertExpense(expense);
        });
    }

    /**
     * Obtiene todos los gastos del mes actual (Room)
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
     * Obtiene el total de gastos del mes actual (Room)
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
     * Obtiene el total de ingresos del mes actual (Room)
     */
    public double getTotalIncomeThisMonth() {
        long startTime = getStartOfMonth();
        long endTime = getEndOfMonth();

        Double total = expenseDao.getTotalForTypeInRange(Constants.TYPE_INCOME, startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene la categoría con más gastos en el mes actual (Room)
     */
    public String getTopExpenseCategoryThisMonth() {
        return expenseDao.getTopExpenseCategoryInRange(getStartOfMonth(), getEndOfMonth());
    }

    /**
     * Obtiene el total gastado en una categoría específica este mes (Room)
     */
    public double getCategoryExpensesThisMonth(String category) {
        Double total = expenseDao.getCategoryTotalInRange(category, getStartOfMonth(), getEndOfMonth());
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de gastos de los últimos N días
     */
    public double getExpensesLastNDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        long startTime = calendar.getTimeInMillis();
        Double total = expenseDao.getTotalForTypeSince(Constants.TYPE_EXPENSE, startTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el número total de gastos
     */
    public int getExpenseCount() {
        return expenseDao.getExpenseCount();
    }

    private long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Obtiene el total de gastos del mes anterior (Room)
     */
    public double getTotalExpensesLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
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
     */
    public List<CategoryAnalysis> getCategoryAnalysis() {
        return expenseDao.getCategoryAnalysisSince(Constants.TYPE_EXPENSE, getStartOfMonth());
    }

    /**
     * Calcula el promedio de gasto diario en el mes actual
     */
    public double getAverageDailyExpense() {
        double total = getTotalExpensesThisMonth();
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return total / dayOfMonth;
    }

    /**
     * Obtiene las categorías disponibles (Room)
     */
    public List<ExpenseCategory> getCategories() {
        return expenseCategoryDao.getCategories();
    }

    /**
     * Detecta patrones de gastos repetitivos (Smart Reminder)
     * @param category Categoría del gasto
     * @param amount Monto del gasto
     * @return SmartReminder si hay patrón, null si no
     */
    public SmartReminder detectExpensePattern(String category, double amount) {
        // Verificar cuántas veces se ha gastado en esta categoría esta semana
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfWeek = calendar.getTimeInMillis();

        int weekCount = expenseDao.countCategoryExpensesSince(category, startOfWeek);

        // Si es la 3ra vez o más, generar recordatorio
        if (weekCount >= 3) {
            Double weekTotalObj = expenseDao.getTotalSpentInCategorySince(category, startOfWeek);
            double weekTotal = weekTotalObj != null ? weekTotalObj : 0.0;

            SmartReminder reminder = new SmartReminder();
            reminder.setCategory(category);
            reminder.setOccurrenceCount(weekCount + 1); // +1 porque aún no se ha guardado
            reminder.setTotalAmount(weekTotal + amount);
            reminder.setTimeFrame("esta semana");

            // Generar sugerencias personalizadas
            List<String> suggestions = generateSuggestions(category, weekTotal + amount);
            reminder.setSuggestions(suggestions);

            // Mensaje del patrón
            String patternMsg = String.format("Es tu %d° %s esta semana. Total: €%.2f. ¿Todo bien?",
                    weekCount + 1, category, weekTotal + amount);
            reminder.setPatternMessage(patternMsg);

            return reminder;
        }

        return null; // No hay patrón detectado
    }

    /**
     * Genera sugerencias personalizadas según la categoría
     */
    private List<String> generateSuggestions(String category, double totalAmount) {
        List<String> suggestions = new ArrayList<>();

        if ("Comida".equalsIgnoreCase(category) || "Café".equalsIgnoreCase(category)) {
            suggestions.add("Lleva termo de casa: ahorra 40€/mes");
            suggestions.add("Máquina de oficina: ahorro del 70%");
        } else if ("Transporte".equalsIgnoreCase(category)) {
            suggestions.add("Bicicleta o caminar: ahorro del 100%");
            suggestions.add("Transporte compartido: ahorro del 50%");
        } else if ("Ocio".equalsIgnoreCase(category)) {
            suggestions.add("Buscar actividades gratuitas");
            suggestions.add("Esperar ofertas o descuentos");
        } else if ("Ropa".equalsIgnoreCase(category)) {
            suggestions.add("Esperar rebajas: ahorro del 30-50%");
            suggestions.add("Comprar de segunda mano");
        } else {
            suggestions.add("Buscar alternativas más económicas");
            suggestions.add("Revisar si es realmente necesario");
        }

        return suggestions;
    }

    /**
     * Verifica si un gasto es "importante" (muy alto)
     * @param amount Monto del gasto
     * @return true si es un gasto importante (>20% del ingreso mensual)
     */
    public boolean isImportantExpense(double amount) {
        double monthlyIncome = getTotalIncomeThisMonth();

        if (monthlyIncome == 0) {
            // Si no hay ingresos, considerar gasto importante si es >100€
            return amount >= 100;
        }

        // Gasto importante si representa más del 20% del ingreso mensual
        return (amount / monthlyIncome) >= 0.20;
    }

    /**
     * Calcula el impacto de un gasto en objetivos
     * Por ejemplo: "Vacaciones se retrasa 2 semanas"
     */
    public String calculateGoalImpact(double amount) {
        // TODO: Implementar lógica de objetivos cuando se cree GoalsActivity
        // Por ahora, estimación simple

        double monthlySavings = getTotalIncomeThisMonth() - getTotalExpensesThisMonth();

        if (monthlySavings <= 0) {
            return "No podrás ahorrar este mes";
        }

        // Calcular cuántos días de ahorro equivale este gasto
        double dailySavings = monthlySavings / 30;
        int daysDelay = (int) (amount / dailySavings);

        if (daysDelay < 7) {
            return String.format("Se retrasa %d días tu ahorro", daysDelay);
        } else if (daysDelay < 30) {
            int weeks = daysDelay / 7;
            return String.format("Se retrasa %d %s tu objetivo",
                    weeks, weeks == 1 ? "semana" : "semanas");
        } else {
            int months = daysDelay / 30;
            return String.format("Se retrasa %d %s tu objetivo",
                    months, months == 1 ? "mes" : "meses");
        }
    }

    /**
     * Genera alternativas para un gasto grande
     */
    public List<String> generateAlternatives(String category, double amount) {
        List<String> alternatives = new ArrayList<>();

        if ("Ropa".equalsIgnoreCase(category)) {
            alternatives.add(String.format("Esperar a rebajas: ahorra del 50%% (€%.2f)", amount * 0.5));
            alternatives.add(String.format("Buscar similar online: ahorro del 30%% (€%.2f)", amount * 0.3));
            alternatives.add("Revisar armario primero");
        } else if ("Ocio".equalsIgnoreCase(category)) {
            alternatives.add("Buscar actividades gratuitas");
            alternatives.add("Esperar ofertas o descuentos");
            alternatives.add("Posponer para el próximo mes");
        } else if ("Hogar".equalsIgnoreCase(category)) {
            alternatives.add("Comparar precios en línea");
            alternatives.add("Comprar de segunda mano");
            alternatives.add("Buscar promociones");
        } else {
            alternatives.add(String.format("Buscar alternativas: ahorro estimado del 30%% (€%.2f)", amount * 0.3));
            alternatives.add("Posponer compra 1 semana");
            alternatives.add("Buscar opciones de segunda mano");
        }

        return alternatives;
    }

    /**
     * Calcula el porcentaje de presupuesto que representa un gasto
     */
    public int calculateBudgetPercentage(double amount) {
        double weeklyIncome = getTotalIncomeThisMonth() / 4.0; // Ingreso semanal estimado
        double weeklyExpenses = getTotalExpensesThisMonth() / 4.0;
        double weeklyBudget = weeklyIncome - weeklyExpenses;

        if (weeklyBudget <= 0) {
            return 100; // Ya se gastó todo el presupuesto
        }

        return (int) ((amount / weeklyBudget) * 100);
    }

    /**
     * Verifica si se debe mostrar confirmación antes de guardar
     */
    public boolean shouldShowConfirmation(double amount) {
        int percentage = calculateBudgetPercentage(amount);

        // Mostrar confirmación si el gasto representa más del 10% del presupuesto semanal
        return percentage >= 10;
    }
}
