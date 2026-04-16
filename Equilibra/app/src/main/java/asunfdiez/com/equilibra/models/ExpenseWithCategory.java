package asunfdiez.com.equilibra.models;

import androidx.room.Embedded;

/**
 * Pojo para representar un gasto con los metadatos de su categoría
 */
public class ExpenseWithCategory {
    @Embedded
    private Expense expense;

    private String categoryName;
    private int categoryIconResId;
    private String categoryColor;

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryIconResId() {
        return categoryIconResId;
    }

    public void setCategoryIconResId(int categoryIconResId) {
        this.categoryIconResId = categoryIconResId;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    // Métodos delegados para DataBinding
    public String getDescription() {
        return expense != null ? expense.getDescription() : "";
    }

    public String getCategory() {
        return categoryName != null ? categoryName : (expense != null ? String.valueOf(expense.getCategory()) : "");
    }

    public String getTime() {
        return expense != null ? expense.getTime() : "";
    }

    public double getAmount() {
        return expense != null ? expense.getAmount() : 0.0;
    }
}
