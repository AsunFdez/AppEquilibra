package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.repositories.BudgetRepository;

/**
 * BudgetController - Controlador para la gestión de presupuestos
 * Intermedia entre las Activities y el Repositorio (Capa de Datos)
 */
public class BudgetController {
    private final BudgetRepository budgetRepository;

    public BudgetController(Context context) {
        this.budgetRepository = new BudgetRepository(context);
    }

    /**
     * Guarda un presupuesto
     * @param budget Presupuesto a guardar
     */
    public void saveBudget(Budget budget) {
        budgetRepository.saveBudget(budget);
    }

    /**
     * Actualiza un presupuesto
     * @param budget Presupuesto a actualizar
     */
    public void updateBudget(Budget budget) {
        budgetRepository.updateBudget(budget);
    }

    /**
     * Obtiene el presupuesto activo
     * @return Presupuesto activo
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

}
