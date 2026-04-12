package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.database.AchievementDao;
import asunfdiez.com.equilibra.database.AlternativeDao;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.ExpenseCategoryDao;
import asunfdiez.com.equilibra.database.ExpenseDao;
import asunfdiez.com.equilibra.database.SavingGoalDao;
import asunfdiez.com.equilibra.database.UserProfileDao;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Alternative;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * ExpenseRepository - Repositorio para gestionar datos de gastos
 * Intermedia entre ViewModel y Room DAOs
 */
public class ExpenseRepository {

    private final ExpenseCategoryDao expenseCategoryDao;
    private final ExpenseDao expenseDao;
    private final AchievementDao achievementDao;

    private final SavingGoalDao savingGoalDao;

    private final AlternativeDao alternativeDao;

    private final UserProfileDao userProfileDao;

    private final Context context;

    public ExpenseRepository(Context context) {
        this.context = context;
        this.expenseCategoryDao = AppDatabase.getInstance(context).expenseCategoryDao();
        this.expenseDao = AppDatabase.getInstance(context).expenseDao();
        this.achievementDao = AppDatabase.getInstance(context).achievementDao();
        this.userProfileDao = AppDatabase.getInstance(context).userProfileDao();
        this.savingGoalDao = AppDatabase.getInstance(context).savingGoalDao();
        this.alternativeDao = AppDatabase.getInstance(context).alternativeDao();
    }

    /**
     * Obtiene un gasto por su ID
     */
    public Expense getExpenseById(long id) {
        return expenseDao.getExpenseById(id);
    }

    /**
     * Guarda un nuevo gasto o actualiza uno existente
     */
    public void saveExpense(Expense expense) {
        if (expense.getId() == 0) {
            expenseDao.insertExpense(expense);
            // Incrementar el contador de la semana solo si es nuevo
            userProfileDao.incrementWeekProgress();
        } else {
            expenseDao.updateExpenses(expense);
        }

        // comprobaciones de logros existentes
        checkAchievements();
    }

    /**
     * Elimina un gasto de la base de datos
     */
    public void deleteExpense(Expense expense) {
        expenseDao.deleteExpense(expense);
        
        // Decrementar el contador de la semana si era un gasto (opcional, según lógica de negocio)
        if (expense.isExpense()) {
            userProfileDao.decrementWeekProgress();
        }
    }

    /**
     * Obtiene los gastos en un rango de fechas
     */
    public List<Expense> getExpensesInRange(long startTime, long endTime) {
        return expenseDao.getExpensesInRange(startTime, endTime);
    }

    /**
     * Obtiene todos los gastos del mes actual
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
     */
    public double getTotalIncomeThisMonth() {
        long startTime = getStartOfMonth();
        long endTime = getEndOfMonth();

        Double total = expenseDao.getTotalForTypeInRange(Constants.TYPE_INCOME, startTime, endTime);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene la categoría con más gastos en el mes actual
     */
    public String getTopExpenseCategoryThisMonth() {
        return expenseDao.getTopExpenseCategoryInRange(getStartOfMonth(), getEndOfMonth());
    }

    /**
     * Obtiene el total gastado en una categoría específica este mes
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

    /**
     * Comprueba si se han cumplido los retos (medallas)
     * 1. Primer registro de gasto
     * 2. Gastos durante 7 días seguidos
     */
    public void checkAchievements () {
        int expenseCount = getExpenseCount();
        UserProfile profile = userProfileDao.getUserProfileSync();
        if (profile == null) return;
        int dayCount = profile.getDayCount();
        //registrar el primer gasto
        if (expenseCount >= 1) {
            Achievement achievement = achievementDao.getAchievementByTitle(context.getString(R.string.first_step_title));
            if (achievement != null && !achievement.isUnlocked()) {
                achievement.setUnlocked(true);
                achievementDao.updateAchievement(achievement);
            }
        }
        //registrar gastos durante 7 dias seguidos
        if (dayCount >= 7 && expenseCount >= 7) {
            Achievement achievement = achievementDao.getAchievementByTitle(context.getString(R.string.constant_title));
            if (achievement != null && !achievement.isUnlocked()) {
                achievement.setUnlocked(true);
                achievementDao.updateAchievement(achievement);
            }
        }
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
     * Obtiene las categorías disponibles
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
            reminder.setTimeFrame(context.getString(R.string.this_week));

            // Generar sugerencias personalizadas
            List<String> suggestions = generateSuggestions(category, weekTotal + amount);
            reminder.setSuggestions(suggestions);

            // Mensaje del patrón
            String patternMsg = String.format(context.getString(R.string.expense_pattern),
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
     * @param amount importe del gasto
     * @return true si es un gasto importante (>20% del ingreso mensual
     * o si no hay ingresos y el gasto es >100€)
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
        // Buscar el primer objetivo de ahorro activo
        UserProfile profile = userProfileDao.getUserProfileSync();
        if (profile == null) return "";
        long userId = profile.getId();
        SavingGoal targetGoal = savingGoalDao.getFirstSavingGoalActiveByUserId(userId);

        double monthlyIncome = getTotalIncomeThisMonth();
        double monthlyExpenses = getTotalExpensesThisMonth();
        double monthlySavings = monthlyIncome - monthlyExpenses;

        if (monthlySavings <= 0) {
            return context.getString(R.string.can_not_save);
        }

        double dailySavings = monthlySavings / 30.0;
        int daysDelay = (int) (amount / dailySavings);

        if (targetGoal != null) {
            // Impacto específico en el objetivo
            String delayStr = formatDelay(daysDelay);
            return String.format(context.getString(R.string.delay_target), targetGoal.getName(), delayStr);
        }

        // Impacto genérico si no hay objetivos
        return String.format(context.getString(R.string.delay_from_saving), daysDelay, context.getString(R.string.days));
    }

    private String formatDelay(int daysDelay) {
        if (daysDelay < 7) {
            return String.format("%d %s", daysDelay, context.getString(R.string.days));
        } else if (daysDelay < 30) {
            int weeks = daysDelay / 7;
            return String.format("%d %s", weeks, weeks == 1 ? context.getString(R.string.week) : context.getString(R.string.weeks));
        } else {
            int months = daysDelay / 30;
            return String.format("%d %s", months, months == 1 ? context.getString(R.string.month) : context.getString(R.string.months));
        }
    }

    /**
     * Genera alternativas para un gasto grande
     */
    public List<String> generateAlternatives(String category, double amount, String description) {
        List<String> suggestions = new ArrayList<>();

        // 1. Buscar en la base de datos alternativas por categoría y descripción
        List<Alternative> dbAlternatives = alternativeDao.getAlternativesByCategoryAndDescription(category, description);

        if (dbAlternatives != null && !dbAlternatives.isEmpty()) {
            for (Alternative alt : dbAlternatives) {
                if (alt.getSavingsPercentage() > 0 && alt.getSavingsPercentage() < 1.0) {
                    suggestions.add(String.format(context.getString(R.string.alternative_suggested),
                            alt.getSuggestion(), alt.getSavingsPercentage() * 100, amount * alt.getSavingsPercentage()));
                } else {
                    suggestions.add(alt.getSuggestion());
                }
            }
        }

        // Si no hay suficientes, añadir genéricas (fallback)
        if (suggestions.size() < 2) {
            suggestions.add(context.getString(R.string.suggested_1));
            suggestions.add(context.getString(R.string.suggested_2));
        }

        return suggestions;
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

    /**
     * Obtiene el total de ingresos en un rango
     */
    public double getTotalIncomeInRange(long start, long end) {
        Double total = expenseDao.getTotalInRange(Constants.TYPE_INCOME, start, end);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el total de gastos en un rango
     */
    public double getTotalExpensesInRange(long start, long end) {
        Double total = expenseDao.getTotalInRange(Constants.TYPE_EXPENSE, start, end);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene el análisis por categoría (Top N) en un rango
     */
    public List<CategoryAnalysis> getTopCategoriesAnalysis(long start, long end, int limit) {
        return expenseDao.getTopCategoryAnalysisInRange(Constants.TYPE_EXPENSE, start, end, limit);
    }

    /**
     * Obtiene los gastos agrupados por día para la semana actual
     */
    public double[] getWeeklyExpenses() {
        double[] weeklyData = new double[7];
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 7; i++) {
            long start = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            long end = cal.getTimeInMillis() - 1;
            
            Double total = expenseDao.getTotalInRange(Constants.TYPE_EXPENSE, start, end);
            weeklyData[i] = total != null ? total : 0.0;
        }
        return weeklyData;
    }

    /**
     * Obtiene los gastos agrupados por mes para el año actual (hasta el mes actual)
     */
    public double[] getMonthlyExpenses() {
        double[] monthlyData = new double[12];
        Calendar cal = Calendar.getInstance();
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


    public void deleteAll() {
        expenseDao.deleteAll();
    }

    public int getTotalExpenseCount() {
        return expenseDao.getTotalExpenseCount();
    }


    /**
     * Calcula cuántas semanas el gasto fue inferior al presupuesto semanal
     */
    public int getWeeksUnderBudget() {
        double monthlyBudget = userProfileDao.getUserMonthlyIncome();
        double weeklyBudgetLimit = monthlyBudget / 4.0;

        List<Expense> allExpenses = expenseDao.getAllExpenses();

        // Agrupar gastos por Año y Semana
        Map<String, Double> weeklyTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        for (Expense e : allExpenses) {
            cal.setTimeInMillis(e.getTimestamp());
            String key = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.WEEK_OF_YEAR);
            weeklyTotals.put(key, weeklyTotals.getOrDefault(key, 0.0) + e.getAmount());
        }

        int weeksUnder = 0;
        for (Double total : weeklyTotals.values()) {
            if (total < weeklyBudgetLimit) {
                weeksUnder++;
            }
        }
        return weeksUnder;
    }

    /**
     * Calcula el ahorro total acumulado basado en el presupuesto mensual
     */
    public double getTotalSavedAmount() {
        double monthlyBudget = userProfileDao.getUserMonthlyIncome();
        List<Expense> allExpenses = expenseDao.getAllExpenses();

        // Agrupar por Año y Mes
        Map<String, Double> monthlyTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        for (Expense e : allExpenses) {
            cal.setTimeInMillis(e.getTimestamp());
            String key = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH);
            monthlyTotals.put(key, monthlyTotals.getOrDefault(key, 0.0) + e.getAmount());
        }

        double totalSaved = 0;
        for (Double totalSpent : monthlyTotals.values()) {
            double saved = monthlyBudget - totalSpent;
            if (saved > 0) {
                totalSaved += saved;
            }
        }
        return totalSaved;
    }

    public double getTotalExpenseAmount() {
        return expenseDao.getTotalExpenseAmount();
    }

    public int getUniqueCategoriesCount() {
        return expenseDao.getUniqueCategoriesCount();
    }
}
