package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import java.util.List;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;

/**
 * ExpenseController - Controlador para la gestión de gastos e ingresos
 * Intermedia entre las Activities y el Repositorio (Capa de Datos)
 */
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(Context context) {
        this.expenseRepository = new ExpenseRepository(context);
    }

    public void addExpense(Expense expense) {
        expenseRepository.saveExpense(expense);
    }

    public List<Expense> getExpensesThisMonth() {
        return expenseRepository.getExpensesThisMonth();
    }

    public double getTotalIncomeThisMonth() {
        return expenseRepository.getTotalIncomeThisMonth();
    }

    public double getTotalExpensesThisMonth() {
        return expenseRepository.getTotalExpensesThisMonth();
    }

    public double getTotalExpensesLastMonth() {
        return expenseRepository.getTotalExpensesLastMonth();
    }

    public List<CategoryAnalysis> getCategoryAnalysis() {
        return expenseRepository.getCategoryAnalysis();
    }

    public double getAverageDailyExpense() {
        return expenseRepository.getAverageDailyExpense();
    }

    public double getExpensesLastNDays(int days) {
        return expenseRepository.getExpensesLastNDays(days);
    }

    public double getTotalIncomeInRange(long start, long end) {
        return expenseRepository.getTotalIncomeInRange(start, end);
    }

    public double getTotalExpensesInRange(long start, long end) {
        return expenseRepository.getTotalExpensesInRange(start, end);
    }

    public List<CategoryAnalysis> getTopCategoriesAnalysis(long start, long end, int limit) {
        return expenseRepository.getTopCategoriesAnalysis(start, end, limit);
    }

    public double[] getWeeklyExpenses() {
        return expenseRepository.getWeeklyExpenses();
    }

    public double[] getMonthlyExpenses() {
        return expenseRepository.getMonthlyExpenses();
    }

    public boolean hasExpenses() {
        return expenseRepository.getExpenseCount() > 0;
    }
}
