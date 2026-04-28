package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.BudgetRepository;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.StreakRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.DateUtils;

/**
 * Controlador que orquesta las operaciones relacionadas con el presupuesto del usuario.
 * 
 * <p>Actúa como la capa intermedia que invoca las funciones del repositorio de 
 * presupuestos, aislando la lógica de negocio de la implementación de persistencia 
 * y facilitando la gestión del estado financiero global de la aplicación.</p>
 */
public class BudgetController {
    // Referencia al repositorio de presupuestos para el acceso a datos
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserProfileRepository userProfileRepository;

    private final StreakRepository streakRepository;

    public BudgetController(Context context) {
        this.budgetRepository = new BudgetRepository(context);
        this.expenseRepository = new ExpenseRepository(context);
        this.userProfileRepository = new UserProfileRepository(context);
        this.streakRepository = new StreakRepository(context);
    }

    /**
     * Persiste o crea un nuevo registro de presupuesto en el sistema.
     * @param budget El objeto presupuesto a guardar.
     */
    public void saveBudget(Budget budget) {
        budgetRepository.saveBudget(budget);
    }

    /**
     * Actualiza la información de un presupuesto existente.
     * @param budget El objeto presupuesto con los cambios a aplicar.
     */
    public void updateBudget(Budget budget) {
        budgetRepository.updateBudget(budget);
    }

    /**
     * Recupera el presupuesto que está actualmente marcado como activo.
     * @return Observable LiveData con el presupuesto activo.
     */
    public LiveData<Budget> getActiveBudget() {
        return budgetRepository.getActiveBudget();
    }

    /**
     * Actualiza la moneda del presupuesto
     * @param currency Nueva moneda
     * @param budgetId ID del presupuesto a actualizar
     */
    public void updateCurrency(String currency, long budgetId) {
        budgetRepository.updateCurrency(currency, budgetId);
    }

    /**
     * Habilita o deshabilita el presupuesto
     * @param enabled true para habilitar, false para deshabilitar
     * @param budgetId ID del presupuesto a actualizar
     */
    public void setBudgetEnabled(boolean enabled, long budgetId) {
        budgetRepository.setBudgetEnabled(enabled, budgetId);
    }

    /**
     * Verifica si el usuario se mantiene dentro del presupuesto diario
     * Si es un nuevo día y se mantuvo dentro del presupuesto del día anterior,
     * registra la racha de presupuesto
     */
    public void checkDailyBudgetStreak() {
        new Thread(() -> {
            try {
                UserProfile profile = userProfileRepository.getUserProfile().getValue();
                if (profile == null) return;

                // Verificar si es un nuevo día
                if (!DateUtils.isNewDay(profile.getLastConnectionTimestamp())) {
                    return; // No es un nuevo día, no hacer nada
                }

                // Es un nuevo día - verificar si el usuario se mantuvo dentro del presupuesto ayer
                Budget budget = budgetRepository.getActiveBudget().getValue();
                if (budget == null || budget.getDailyBudget() <= 0) {
                    return; // No hay presupuesto diario configurado
                }

                // Obtener gastos del día anterior
                Calendar yesterdayStart = Calendar.getInstance();
                yesterdayStart.add(Calendar.DAY_OF_YEAR, -1);
                yesterdayStart.set(Calendar.HOUR_OF_DAY, 0);
                yesterdayStart.set(Calendar.MINUTE, 0);
                yesterdayStart.set(Calendar.SECOND, 0);
                yesterdayStart.set(Calendar.MILLISECOND, 0);

                Calendar yesterdayEnd = Calendar.getInstance();
                yesterdayEnd.add(Calendar.DAY_OF_YEAR, -1);
                yesterdayEnd.set(Calendar.HOUR_OF_DAY, 23);
                yesterdayEnd.set(Calendar.MINUTE, 59);
                yesterdayEnd.set(Calendar.SECOND, 59);
                yesterdayEnd.set(Calendar.MILLISECOND, 999);

                List<Expense> yesterdayExpenses =
                        expenseRepository.getExpensesInRange(yesterdayStart.getTimeInMillis(), yesterdayEnd.getTimeInMillis());

                // Calcular gastos del día anterior
                double totalYesterday = 0;
                for (Expense expense : yesterdayExpenses) {
                    if (Constants.TYPE_EXPENSE.equals(expense.getType())) {
                        totalYesterday += expense.getAmount();
                    }
                }

                // Si se mantuvo dentro del presupuesto, registrar la racha
                if (totalYesterday <= budget.getDailyBudget()) {
                    recordBudgetDay(profile);
                }

            } catch (Exception e) {
                Log.e("BudgetController", "Error checking daily budget streak", e);
            }
        }).start();
    }

    /**
     * Incrementar racha de presupuesto
     */
    public void recordBudgetDay(UserProfile user) {
        new Thread(() -> {
            try {
                if (user != null) {
                    Streak streak = streakRepository.getStreakByTypeSync(user.getId(), Streak.TYPE_BUDGET);
                    if (streak != null) {
                        streak.recordActivity();
                        streakRepository.updateStreak(streak);
                    }
                }
            } catch (Exception e) {
                Log.e("BudgetController", "Error recording budget day", e);
            }
        }).start();
    }

}
