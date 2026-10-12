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
import asunfdiez.com.equilibra.models.ExpenseWithCategory;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO principal para la gestión de la tabla de transacciones de gastos.
 * 
 * <p>Define todas las operaciones de persistencia para los gastos individuales, 
 * incluyendo inserciones, eliminaciones, actualizaciones y consultas complejas 
 * para filtrado por fecha, categoría o tipo. También incluye consultas de 
 * agregación para el análisis financiero.</p>
 */
@Dao
public interface ExpenseDao {

    /**
     * Inserta una lista de transacciones en lote.
     */
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertListOfExpenses (List<Expense> expenseList);
    
    /**
     * Registra un nuevo gasto o ingreso.
     */
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insertExpense (Expense expense);
    
    /**
     * Elimina un registro de transacción específico.
     */
    @Delete
    void deleteExpense (Expense expense);
    
    /**
     * Actualiza la información de una transacción existente.
     */
    @Update
    void updateExpenses (Expense expense);

    /**
     * Elimina permanentemente todos los registros de la tabla de gastos.
     */
    @Query("DELETE FROM " + Constants.TABLE_EXPENSES)
    void deleteAll();


    /**
     * Recupera todas las transacciones ordenadas por fecha (más reciente primero).
     */
    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    List<Expense> getAllExpenses();

    /**
     * Recupera todas las transacciones como LiveData para observación reactiva.
     */
    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    androidx.lifecycle.LiveData<List<Expense>> getAllExpensesAsync();

    /**
     * Obtiene una lista limitada de las transacciones más recientes.
     */
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

    @Query("SELECT e." + Constants.COLUMN_EXPENSE_CATEGORY + " as categoryId, " +
            "c." + Constants.COLUMN_CATEGORY_NAME + " as category, " +
            "SUM(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as amount, " +
            "COUNT(*) as count, " +
            "AVG(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as averageAmount, " +
            "MAX(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as highestAmount, " +
            "MIN(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as lowestAmount " +
            "FROM " + Constants.TABLE_EXPENSES + " e " +
            "LEFT JOIN " + Constants.TABLE_CATEGORIES + " c ON e." + Constants.COLUMN_EXPENSE_CATEGORY + " = c.id " +
            "WHERE e." + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            "e." + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime " +
            "GROUP BY e." + Constants.COLUMN_EXPENSE_CATEGORY +
            " ORDER BY amount DESC LIMIT :limit")
    List<CategoryAnalysis> getTopCategoryAnalysisInRange(String type, long startTime, long endTime, int limit);

    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime")
    Double getTotalInRange(String type, long startTime, long endTime);

    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :startTime")
    Double getTotalForTypeSince(String type, long startTime);

    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES)
    int getExpenseCount();

    @Query("SELECT " + Constants.COLUMN_EXPENSE_CATEGORY + " FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime " +
            "GROUP BY " + Constants.COLUMN_EXPENSE_CATEGORY + " ORDER BY SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") DESC LIMIT 1")
    long getTopExpenseCategoryInRange(long startTime, long endTime);

    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = :category AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime")
    Double getCategoryTotalInRange(long category, long startTime, long endTime);

    @Query("SELECT e." + Constants.COLUMN_EXPENSE_CATEGORY + " as categoryId, " +
            "c." + Constants.COLUMN_CATEGORY_NAME + " as category, " +
            "SUM(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as amount, " +
            "COUNT(*) as count, " +
            "AVG(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as averageAmount, " +
            "MAX(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as highestAmount, " +
            "MIN(e." + Constants.COLUMN_EXPENSE_AMOUNT + ") as lowestAmount " +
            "FROM " + Constants.TABLE_EXPENSES + " e " +
            "LEFT JOIN " + Constants.TABLE_CATEGORIES + " c ON e." + Constants.COLUMN_EXPENSE_CATEGORY + " = c.id " +
            "WHERE e." + Constants.COLUMN_EXPENSE_TYPE + " = :type AND " +
            "e." + Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :startTime " +
            "GROUP BY e." + Constants.COLUMN_EXPENSE_CATEGORY +
            " ORDER BY amount DESC")
    List<CategoryAnalysis> getCategoryAnalysisSince(String type, long startTime);

    /**
     * Cuenta cuántas veces se ha gastado en una categoría desde un timestamp dado
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = :category AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :sinceTimestamp")
    int countCategoryExpensesSince(long category, long sinceTimestamp);

    /**
     * Obtiene el total gastado en una categoría desde un timestamp dado
     */
    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_CATEGORY + " = :category AND " +
            Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :sinceTimestamp")
    Double getTotalSpentInCategorySince(long category, long sinceTimestamp);

    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES
    + " WHERE "+Constants.COLUMN_EXPENSE_TYPE+" = '" + Constants.TYPE_EXPENSE + "'")
    int getTotalExpenseCount();


    @Query("SELECT SUM(" + Constants.COLUMN_EXPENSE_AMOUNT + ") FROM " + Constants.TABLE_EXPENSES
    + " WHERE "+Constants.COLUMN_EXPENSE_TYPE+" = '" + Constants.TYPE_EXPENSE + "'")
    double getTotalExpenseAmount();

    @Query("SELECT COUNT(DISTINCT " + Constants.COLUMN_EXPENSE_CATEGORY + ") FROM " + Constants.TABLE_EXPENSES)
    int getUniqueCategoriesCount();

    @Query("SELECT CAST(" + Constants.COLUMN_EXPENSE_TIMESTAMP + " / 86400000 AS INTEGER) as day " +
            "FROM " + Constants.TABLE_EXPENSES +
            " WHERE "+Constants.COLUMN_EXPENSE_TYPE+" = '" + Constants.TYPE_EXPENSE + "'" +
            " GROUP BY day ORDER BY day DESC")
    List<Long> getUniqueDaysWithExpenses();

    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = '" + Constants.TYPE_EXPENSE + "'" +
            " AND " + Constants.COLUMN_EXPENSE_TIMESTAMP + " >= :sinceTimestamp")
    int countExpensesSince(long sinceTimestamp);

    @Query("SELECT e.*, c.name as categoryName, c.iconResId as categoryIconResId, c.color as categoryColor " +
            "FROM " + Constants.TABLE_EXPENSES + " e " +
            "LEFT JOIN " + Constants.TABLE_CATEGORIES + " c ON e." + Constants.COLUMN_EXPENSE_CATEGORY + " = c.id " +
            "WHERE e." + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime " +
            " ORDER BY e." + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    List<ExpenseWithCategory> getExpensesWithCategoryInRange(long startTime, long endTime);

    @Query("SELECT e.*, c.name as categoryName, c.iconResId as categoryIconResId, c.color as categoryColor " +
            "FROM " + Constants.TABLE_EXPENSES + " e " +
            "LEFT JOIN " + Constants.TABLE_CATEGORIES + " c ON e." + Constants.COLUMN_EXPENSE_CATEGORY + " = c.id " +
            "WHERE e.id = :expenseId")
    ExpenseWithCategory getExpenseWithCategoryById(long expenseId);


    /**
     * Obtiene gastos de un mes específico.
     */
    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " WHERE CAST(strftime('%m', datetime(" + Constants.COLUMN_EXPENSE_TIMESTAMP + " / 1000, 'unixepoch')) AS INTEGER) = :month " +
            " AND CAST(strftime('%Y', datetime(" + Constants.COLUMN_EXPENSE_TIMESTAMP + " / 1000, 'unixepoch')) AS INTEGER) = :year " +
            " AND "+Constants.COLUMN_EXPENSE_TYPE+" = '" + Constants.TYPE_EXPENSE + "'" +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    List<Expense> getExpensesByMonthSync(int month, int year);

    /**
     * Obtiene gastos de los últimos N días.
     */
    @Query("SELECT * FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TIMESTAMP + " >= (strftime('%s', 'now', '-' || :days || ' days') * 1000) " +
            " AND "+Constants.COLUMN_EXPENSE_TYPE+" = '" + Constants.TYPE_EXPENSE + "'" +
            " ORDER BY " + Constants.COLUMN_EXPENSE_TIMESTAMP + " DESC")
    List<Expense> getExpensesLastNDaysSync(int days);

    /**
     * Verifica si ya existe un ingreso automático para el mes actual
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_EXPENSES +
            " WHERE " + Constants.COLUMN_EXPENSE_TYPE + " = '" + Constants.TYPE_INCOME + "'" +
            " AND " + Constants.COLUMN_EXPENSE_DESCRIPTION + " = :description" +
            " AND " + Constants.COLUMN_EXPENSE_TIMESTAMP + " BETWEEN :startTime AND :endTime")
    int countAutoIncomeInRange(String description, long startTime, long endTime);

}
