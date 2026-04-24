package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.repositories.BudgetRepository;

/**
 * Controlador que orquestra las operaciones relacionadas con el presupuesto del usuario.
 * 
 * <p>Actúa como la capa intermedia que invoca las funciones del repositorio de 
 * presupuestos, aislando la lógica de negocio de la implementación de persistencia 
 * y facilitando la gestión del estado financiero global de la aplicación.</p>
 */
public class BudgetController {
    // Referencia al repositorio de presupuestos para el acceso a datos
    private final BudgetRepository budgetRepository;

    public BudgetController(Context context) {
        this.budgetRepository = new BudgetRepository(context);
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

}
