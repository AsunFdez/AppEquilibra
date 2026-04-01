package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import java.util.List;

import asunfdiez.com.equilibra.database.DatabaseHelper;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;

/**
 * ExpenseController - Controlador para la gestión de gastos e ingresos
 * Intermedia entre las Activities y la base de datos
 * Implementa lógica de negocio para gastos
 */
public class ExpenseController {

    private DatabaseHelper databaseHelper;
    private Context context;

    public ExpenseController(Context context) {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Añade un nuevo gasto o ingreso
     */
    public long addExpense(Expense expense) {
        return databaseHelper.insertExpense(expense);
    }

    /**
     * Obtiene todos los gastos
     */
    public List<Expense> getAllExpenses() {
        return databaseHelper.getAllExpenses();
    }

    /**
     * Obtiene gastos recientes limitados
     */
    public List<Expense> getRecentExpenses(int limit) {
        return databaseHelper.getRecentExpenses(limit);
    }

    /**
     * Obtiene gastos del mes actual
     */
    public List<Expense> getExpensesThisMonth() {
        return databaseHelper.getExpensesThisMonth();
    }

    /**
     * Obtiene gastos del mes anterior
     */
    public List<Expense> getExpensesLastMonth() {
        return databaseHelper.getExpensesLastMonth();
    }

    /**
     * Obtiene un gasto por ID
     */
    public Expense getExpenseById(long id) {
        return databaseHelper.getExpenseById(id);
    }

    /**
     * Actualiza un gasto existente
     */
    public int updateExpense(Expense expense) {
        return databaseHelper.updateExpense(expense);
    }

    /**
     * Elimina un gasto
     */
    public int deleteExpense(long id) {
        return databaseHelper.deleteExpense(id);
    }

    /**
     * Calcula el total de ingresos del mes actual
     */
    public double getTotalIncomeThisMonth() {
        return databaseHelper.getTotalIncomeThisMonth();
    }

    /**
     * Calcula el total de gastos del mes actual
     */
    public double getTotalExpensesThisMonth() {
        return databaseHelper.getTotalExpensesThisMonth();
    }

    /**
     * Calcula el total de gastos del mes anterior
     */
    public double getTotalExpensesLastMonth() {
        return databaseHelper.getTotalExpensesLastMonth();
    }

    /**
     * Obtiene análisis por categoría
     */
    public List<CategoryAnalysis> getCategoryAnalysis() {
        return databaseHelper.getCategoryAnalysis();
    }

    /**
     * Calcula el promedio de gasto diario
     */
    public double getAverageDailyExpense() {
        return databaseHelper.getAverageDailyExpense();
    }

    /**
     * Obtiene el total de gastos de los últimos N días
     */
    public double getExpensesLastNDays(int days) {
        return databaseHelper.getExpensesLastNDays(days);
    }

    /**
     * Verifica si hay gastos registrados
     */
    public boolean hasExpenses() {
        return databaseHelper.getExpenseCount() > 0;
    }

    /**
     * Obtiene el número total de gastos
     */
    public int getExpenseCount() {
        return databaseHelper.getExpenseCount();
    }
}
