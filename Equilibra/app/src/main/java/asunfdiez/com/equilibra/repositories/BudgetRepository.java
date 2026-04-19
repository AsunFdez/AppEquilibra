package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.BudgetDao;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * Repository para gestionar Presupuestos (Budget)
 * Usa Room Database para persistencia
 */
public class BudgetRepository {

    private static final String TAG = "BudgetRepository";

    private final BudgetDao budgetDao;
    private final ExecutorService executorService;
    private final LiveData<Budget> activeBudget;

    /**
     * Constructor
     */
    public BudgetRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.budgetDao = database.budgetDao();
        this.executorService = Executors.newSingleThreadExecutor();

        // Obtener el presupuesto activo o el último disponible como LiveData
        this.activeBudget = budgetDao.getActiveOrLastBudget();

        // Inicialización asíncrona de datos por defecto si es necesario
        initializeDefaultBudgetIfNeeded();
    }

    // ========== MÉTODOS DE ACCESO A DATOS ==========

    /**
     * Obtiene el presupuesto activo como LiveData
     */
    public LiveData<Budget> getActiveBudget() {
        return activeBudget;
    }


    /**
     * Inserta o actualiza un presupuesto
     */
    public void saveBudget(Budget budget) {
        executorService.execute(() -> {
            try {
                // Desactivar todos los presupuestos anteriores
                budgetDao.deactivateAll();

                budget.setUpdatedAt(System.currentTimeMillis());

                long id = budgetDao.insert(budget);
                Log.d(TAG, "Budget saved with ID: " + id);

            } catch (Exception e) {
                Log.e(TAG, "Error saving budget: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Actualiza un presupuesto existente
     */
    public void updateBudget(Budget budget) {
        executorService.execute(() -> {
            try {
                Budget current = budgetDao.getActiveBudgetSync();
                // Si los valores son idénticos, no actualizamos
                if (current != null && isSameBudget(current, budget)) {
                    return;
                }
                budgetDao.deactivateAll();
                budget.setUpdatedAt(System.currentTimeMillis());
                budgetDao.update(budget);
                Log.d(TAG, "Budget updated");
            } catch (Exception e) {
                Log.e(TAG, "Error updating budget: " + e.getMessage(), e);
            }
        });
    }

    private boolean isSameBudget(Budget b1, Budget b2) {
        return Math.abs(b1.getMonthlyBudget() - b2.getMonthlyBudget()) < 0.01 &&
                Math.abs(b1.getWeeklyBudget() - b2.getWeeklyBudget()) < 0.01 &&
                Math.abs(b1.getDailyBudget() - b2.getDailyBudget()) < 0.01 &&
                b1.isEnabled() == b2.isEnabled() &&
                (b1.getCurrency() != null && b1.getCurrency().equals(b2.getCurrency()));
    }


    /**
     * Actualiza la moneda
     */
    public void updateCurrency(String currency, long budgetId) {
        executorService.execute(() -> {
            try {
                Budget budget = budgetDao.getBudgetById(budgetId);
                if (budget != null) {
                    budget.setCurrency(currency);
                    budgetDao.update(budget);
                    Log.d(TAG, "Moneda actualizada a : " + currency);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al actualizar moneda: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Habilita o deshabilita el presupuesto
     */
    public void setBudgetEnabled(boolean enabled, long budgetId) {
        executorService.execute(() -> {
            try {
                // Intentar por ID, si no por el primero que haya
                Budget budget = budgetDao.getBudgetById(budgetId);
                if (budget == null) {
                    budget = budgetDao.getAnyBudgetSync();
                }

                if (budget != null) {
                    Log.d(TAG, "Cambiando estado de budget ID: " + budget.getId() + " a: " + enabled);

                    budget.setEnabled(enabled);
                    budget.setUpdatedAt(System.currentTimeMillis());

                    // Realizamos la actualización
                    int rowsAffected = budgetDao.update(budget);

                    if (rowsAffected > 0) {
                        Log.d(TAG, "Base de datos actualizada con éxito. Filas afectadas: " + rowsAffected);
                    } else {
                        Log.e(TAG, "No se actualizó ninguna fila. ¿El ID es correcto?");
                    }
                } else {
                    Log.e(TAG, "No se encontró ningún presupuesto para actualizar");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting budget enabled: " + e.getMessage(), e);
            }
        });
    }
    /**
     * Elimina todos los presupuestos
     */
    public void deleteAllBudgets() {
        executorService.execute(() -> {
            try {
                budgetDao.deleteAll();
                Log.d(TAG, "All budgets deleted");
            } catch (Exception e) {
                Log.e(TAG, "Error deleting budgets: " + e.getMessage(), e);
            }
        });
    }

    // ========== INICIALIZACIÓN ==========

    /**
     * Inicializa un presupuesto por defecto si no existe ninguno
     */
    private void initializeDefaultBudgetIfNeeded() {
        executorService.execute(() -> {
            try {
                int budgetCount = budgetDao.countBudgets();
                if (budgetCount > 0) return;

                // Crear presupuesto por defecto
                Budget defaultBudget = new Budget();
                defaultBudget.setMonthlyBudget(Constants.DEFAULT_MONTHLY_BUDGET);
                defaultBudget.calculateWeeklyFromMonthly();
                defaultBudget.calculateDailyFromMonthly();
                defaultBudget.setCurrency(Constants.CURRENCY_EUR);
                defaultBudget.setEnabled(true);

                budgetDao.insert(defaultBudget);
                Log.d(TAG, "Default budget initialized");

            } catch (Exception e) {
                Log.e(TAG, "Error initializing default budget: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
