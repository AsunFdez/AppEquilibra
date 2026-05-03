package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.models.Alternative;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.ExpenseCategory;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.SmartReminder;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.AchievementRepository;
import asunfdiez.com.equilibra.repositories.AlternativeRepository;
import asunfdiez.com.equilibra.repositories.ExpenseCategoryRepository;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.SavingGoalRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

/**
 * Controlador de alto nivel que gestiona la lógica de negocio para gastos e ingresos.
 * 
 * <p>Además de las operaciones básicas CRUD, este controlador integra la lógica de 
 * recordatorios inteligentes, detección de patrones de gasto, impacto en metas de 
 * ahorro y desbloqueo de logros relacionados con las transacciones financieras. 
 * Actúa como el orquestador principal de la actividad económica del usuario.</p>
 */
public class ExpenseController {

    // Repositorio principal para la gestión de transacciones
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserProfileRepository userProfileRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final AlternativeRepository alternativeRepository;
    private final AchievementRepository achievementRepository;
    private final StreakController streakController;
    private final Context context;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler;

    public ExpenseController(Context context) {
        this.context = context;
        this.expenseRepository = new ExpenseRepository(context);
        this.expenseCategoryRepository = new ExpenseCategoryRepository(context);
        this.userProfileRepository = new UserProfileRepository(context);
        this.savingGoalRepository = new SavingGoalRepository(context);
        this.alternativeRepository = new AlternativeRepository(context);
        this.achievementRepository = new AchievementRepository(context);
        this.streakController = new StreakController(context);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Guarda un nuevo gasto
     */
    public void addExpense(Expense expense) {
        saveExpense(expense);
    }

    /**
     * Guarda un nuevo gasto o actualiza uno existente
     */
    public void saveExpense(Expense expense) {
        executor.execute(()-> {
            if (expense.getId() == 0) {
                expenseRepository.insertExpense(expense);
                // Incrementar el contador de la semana solo si es nuevo
                userProfileRepository.incrementWeekProgress();
            } else {
                expenseRepository.updateExpenses(expense);
            }
            checkAchievements();
        });
    }

    /**
     * Elimina un gasto de la base de datos
     * @param expense gasto a borrar
     * @param onComplete CallBack a ejecturar después del borrado
     */
    public void deleteExpense(Expense expense, @Nullable Runnable onComplete) {
        executor.execute(() -> {
            expenseRepository.deleteExpense(expense);
            // Decrementar el contador de la semana si era un gasto (opcional, según lógica de negocio)
            if (expense.isExpense()) {
                userProfileRepository.decrementWeekProgress();
                streakController.decrementActivity();
            }
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    /**
     * Obtiene un gasto por su ID
     * @param expenseId
     * @return gasto
     */
    public Expense getExpenseById(long expenseId) {
        return expenseRepository.getExpenseById(expenseId);
    }

    /**
    * Obtiene los gastos del mes actual
    * @return lista de gastos
    */
    public List<Expense> getExpensesThisMonth() {
        return expenseRepository.getExpensesThisMonth();
    }

    /**
    * Obtiene el total de los ingresos del mes actual
    * @return suma de ingresos
    */
    public double getTotalIncomeThisMonth() {
        return expenseRepository.getTotalIncomeThisMonth();
    }

    /**
    * Obtiene el total de los gastos del mes actual
    * @return suma de gastos
    */
    public double getTotalExpensesThisMonth() {
        return expenseRepository.getTotalExpensesThisMonth();
    }

    /**
    * Obtiene el total de los gastos del mes anterior
    * @return suma de gastos
    */
    public double getTotalExpensesLastMonth() {
        return expenseRepository.getTotalExpensesLastMonth();
    }

    /**
     * Obtiene el análisis por categoría del mes actual
     * @return lista de análisis
     */
    public List<CategoryAnalysis> getCategoryAnalysis() {
        return expenseRepository.getCategoryAnalysis();
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
     * Obtiene los gastos de los últimos N días
     * @param days Número de días
     * @return suma de gastos
     */
    public double getExpensesLastNDays(int days) {
        return expenseRepository.getExpensesLastNDays(days);
    }


    /**
     * Obtiene el total de ingresos en un rango de fechas
     * @param start fecha de inicio del rango
     * @param end fecha de fin del rango
     * @return suma de ingresos
     */
    public double getTotalIncomeInRange(long start, long end) {
        return expenseRepository.getTotalIncomeInRange(start, end);
    }

    /**
     * Obtiene el total de gastos en un rango de fechas
     * @param start fecha de inicio del rango
     * @param end fecha de fin del rango
     * @return suma de gastos
     */
    public double getTotalExpensesInRange(long start, long end) {
        return expenseRepository.getTotalExpensesInRange(start, end);
    }

    /**
     * Obtiene los gastos agrupados por categoría en un rango de fechas
     * @param start fecha de inicio del rango
     * @param end fecha de fin del rango
     * @return lista de análisis
     */
    public List<CategoryAnalysis> getTopCategoriesAnalysis(long start, long end, int limit) {
        return expenseRepository.getTopCategoriesAnalysis(start, end, limit);
    }

    /**
     * Obtiene los gastos de la una semana
     * @param start Fecha de inicio de la semana
     * @return lista de gastos
     */
    public double[] getWeeklyExpenses(long start) {
        return expenseRepository.getWeeklyExpenses(start);
    }

    /**
     * Obtiene los gastos del mes
     * @param start Fecha de inicio del mes
     * @return lista de gastos
     */
    public double[] getMonthlyExpenses(long start) {
        return expenseRepository.getMonthlyExpenses(start);
    }


    /**
     * Obtiene las categorías disponibles
     * @return lista de categorías
     */
    public List<ExpenseCategory> getCategories() {
        return expenseCategoryRepository.getCategories();
    }

    /**
     * Verifica si un gasto es "importante" (muy alto)
     *
     * @param amount importe del gasto
     * @return true si es un gasto importante (>20% del ingreso mensual
     * o si no hay ingresos y el gasto es >100€)
     */
    public boolean isImportantExpense(double amount) {
        //TODO implementar con Budget
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
     * @param amount importe del gasto
     * @return String con el impacto del gasto en objetivos
     * Por ejemplo: "Vacaciones se retrasa 2 semanas"
     */
    public String calculateGoalImpact(double amount) {
        // Buscar el primer objetivo de ahorro activo
        UserProfile profile = userProfileRepository.getUserProfileSync();
        if (profile == null) return "";
        long userId = profile.getId();
        SavingGoal targetGoal = savingGoalRepository.getActiveSavingGoalSync(userId);

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

    /**
     * Formatea el número de días de retraso
     * @param daysDelay
     * @return String con el número de días de retraso
     */
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
     * @param categoryId ID de la categoría del gasto
     * @param amount importe del gasto
     * @param description descripción del gasto
     * @return lista de alternativas
     */
    public List<String> generateAlternatives(long categoryId, double amount, String description) {
        List<String> suggestions = new ArrayList<>();
        String categoryName = expenseCategoryRepository.getCategoryNameById(categoryId);
        if (categoryName == null) categoryName = "";

        // 1. Buscar en la base de datos alternativas por categoría y descripción
        List<Alternative> dbAlternatives = alternativeRepository.getAlternativesByCategoryAndDescription(categoryName, description);

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
     * Comprueba si se ha desbloqueado un logro
     * 1. Primer registro de gasto
     * 2. Gastos durante 7 días seguidos
     */
    public void checkAchievements() {
        int expenseCount = expenseRepository.getExpenseCount();
        UserProfile profile = userProfileRepository.getUserProfileSync();
        if (profile == null) return;
        int dayCount = profile.getDayCount();
        //registrar el primer gasto
        if (expenseCount >= 1) {
            Achievement achievement = achievementRepository.getAchievementByTitle(context.getString(R.string.first_step_title));
            if (achievement != null && !achievement.isUnlocked()) {
                achievement.setUnlocked(true);
                achievementRepository.updateAchievement(achievement);
            }
        }
        //registrar gastos durante 7 dias seguidos
        if (dayCount >= 7 && expenseCount >= 7) {
            Achievement achievement = achievementRepository.getAchievementByTitle(context.getString(R.string.constant_title));
            if (achievement != null && !achievement.isUnlocked()) {
                achievement.setUnlocked(true);
                achievementRepository.updateAchievement(achievement);
            }
        }
    }

    /**
     * Obtiene los gastos con su información de categoría en un rango de fechas
     * @param startTime Fecha de inicio
     * @param endTime Fecha de fin
     * @return Lista de gastos con información de categoría
     */
    public List<ExpenseWithCategory> getExpensesWithCategoryInRange(long startTime, long endTime) {
        return expenseRepository.getExpensesWithCategoryInRange(startTime, endTime);
    }

    /**
     * Detecta patrones de gastos repetitivos (Smart Reminder)
     * @param categoryId Categoría del gasto
     * @param amount     importe del gasto
     * @return SmartReminder si hay patrón, null si no
     */
    public SmartReminder detectExpensePattern(long categoryId, double amount) {
        // Verificar cuántas veces se ha gastado en esta categoría esta semana
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfWeek = calendar.getTimeInMillis();

        int weekCount = expenseRepository.countCategoryExpensesSince(categoryId, startOfWeek);

        // Si es la 3ra vez o más, generar recordatorio
        if (weekCount >= 3) {
            Double weekTotalObj = expenseRepository.getCategoryTotalInRange(categoryId, startOfWeek, System.currentTimeMillis());
            double weekTotal = weekTotalObj != null ? weekTotalObj : 0.0;

            String categoryName = expenseCategoryRepository.getCategoryNameById(categoryId);
            if (categoryName == null) categoryName = String.valueOf(categoryId);

            SmartReminder reminder = new SmartReminder();
            reminder.setCategory(categoryName);
            reminder.setOccurrenceCount(weekCount + 1); // +1 porque aún no se ha guardado
            reminder.setTotalAmount(weekTotal + amount);
            reminder.setTimeFrame(context.getString(R.string.this_week));

            // Generar sugerencias personalizadas
            List<String> suggestions = generateSuggestions(categoryName, weekTotal + amount);
            reminder.setSuggestions(suggestions);

            // Mensaje del patrón
            String patternMsg = String.format(context.getString(R.string.expense_pattern),
                    weekCount + 1, categoryName, weekTotal + amount);
            reminder.setPatternMessage(patternMsg);

            return reminder;
        }

        return null; // No hay patrón detectado
    }

    /**
     * Genera sugerencias personalizadas según la categoría
     * @param category Categoría del gasto
     * @param totalAmount importe del gasto
     * @return Lista de sugerencias
     */
    private List<String> generateSuggestions(String category, double totalAmount) {
        List<String> suggestions = new ArrayList<>();
        //TODO reemplezar con getString
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
     * Calcula cuántas semanas el gasto fue inferior al presupuesto semanal
     * @return número de semanas
     */
    public int getWeeksUnderBudget() {
        //TODO implementar con Budget
        double monthlyBudget = userProfileRepository.getUserMonthlyIncome();
        double weeklyBudgetLimit = monthlyBudget / 4.0;

        List<Expense> allExpenses = expenseRepository.getAllExpenses();

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
     * @return ahorro total
     */
    public double getTotalSavedAmount() {
        double monthlyBudget = userProfileRepository.getUserMonthlyIncome();
        List<Expense> allExpenses = expenseRepository.getAllExpenses();

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

    /**
     * Calcula el porcentaje de presupuesto que representa un gasto
     * @param amount importe del gasto
     * @return número entero del porcentaje
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
     * @param amount importe del gasto
     * @return true si se debe mostrar confirmación, false en caso contrario
     * Mostrará confirmación si el gasto representa más del 10% del presupuesto semanal
     */
    public boolean shouldShowConfirmation(double amount) {
        int percentage = calculateBudgetPercentage(amount);
        return percentage >= 10;
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public long getTopExpenseCategoryThisMonth() {
        return expenseRepository.getTopExpenseCategoryThisMonth();
    }

    public double getCategoryExpensesThisMonth(long topCategoryId) {
        return expenseRepository.getCategoryExpensesThisMonth(topCategoryId);
    }
}
