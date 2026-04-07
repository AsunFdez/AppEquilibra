package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.utils.Constants;

@Dao
public interface ExpenseDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertListOfExpenses (List<Expense> expenseList);
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertExpense (Expense expense);
    @Delete
    void deleteExpense (Expense expense);
    @Update
    void updateExpenses (Expense expense);

    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    List<Expense> getAllExpenses();

    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC LIMIT :limit")
    List<Expense> getRecentExpenses(int limit);

    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_ID + " = :id")
    Expense getExpenseById(long id);

    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime" +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    List<Expense> getExpensesInRange(long startTime, long endTime);

    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime")
    Double getTotalForTypeInRange(String type, long startTime, long endTime);

    @Query("SELECT " + Constants.COLUMN_EXPENSE_CATEGORY + ", " +
            "SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") as amount, " +
            "COUNT(*) as count " +
            "FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :startTime " +
            "GROUP BY " + Constants.COLUMN_EXPENSE_CATEGORY +
            " ORDER BY amount DESC")
    List<CategoryAnalysis> getCategoryAnalysisSince(String type, long startTime);

    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :startTime")
    Double getTotalForTypeSince(String type, long startTime);

    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES)
    int getExpenseCount();

    @Query("SELECT " + Constants.COLUMN_EXPENSE_CATEGORY + " FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime " +
            "GROUP BY " + Constants.COLUMN_EXPENSE_CATEGORY + " ORDER BY SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") DESC LIMIT 1")
    String getTopExpenseCategoryInRange(long startTime, long endTime);

    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = :category AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime")
    Double getCategoryTotalInRange(String category, long startTime, long endTime);

    /**
     * Cuenta cuántas veces se ha gastado en una categoría desde un timestamp dado
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = :category AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :sinceTimestamp")
    int countCategoryExpensesSince(String category, long sinceTimestamp);

    /**
     * Obtiene el total gastado en una categoría desde un timestamp dado
     */
    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = :category AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :sinceTimestamp")
    Double getTotalSpentInCategorySince(String category, long sinceTimestamp);
}
