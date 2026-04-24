package asunfdiez.com.equilibra.models;

import androidx.room.Embedded;

/**
 * Objeto de transferencia de datos (DTO) que combina un gasto con su información de categoría.
 * 
 * <p>Esta clase se utiliza para facilitar la representación de gastos en la interfaz 
 * de usuario, evitando realizar múltiples consultas o joins manuales en la capa 
 * de vista al incluir directamente el nombre, icono y color de la categoría.</p>
 */
public class ExpenseWithCategory {
    // Entidad de gasto incrustada
    @Embedded
    private Expense expense;

    // Metadatos de la categoría para visualización directa
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


    public String getFormattedDateTime() {
        return expense != null ? expense.getFormattedDateTime() : "";
    }

    public double getAmount() {
        return expense != null ? expense.getAmount() : 0.0;
    }
}
