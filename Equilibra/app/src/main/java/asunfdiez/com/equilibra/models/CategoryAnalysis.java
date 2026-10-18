package asunfdiez.com.equilibra.models;

import androidx.room.Ignore;

/**
 * Clase que representa el resultado de un análisis financiero agrupado por categoría.
 * 
 * <p>Proporciona información consolidada sobre el importe total gastado, el número de
 * transacciones realizadas y el porcentaje que representa esta categoría sobre el 
 * total de gastos. Se utiliza principalmente para alimentar los gráficos de análisis.</p>
 */
public class CategoryAnalysis {

    private long categoryId;

    // Nombre de la categoría analizada
    private String category;
    
    // Suma total de los importes de los gastos en esta categoría
    private double amount;
    
    // Cantidad total de registros (gastos) en esta categoría
    private int count; 
    
    @Ignore
    // Proporción porcentual respecto al gasto total consolidado
    private double percentage;

    private double averageAmount;
    private double highestAmount;
    private double lowestAmount;

    // Constructor vacío
    public CategoryAnalysis() {
    }

    // Getters y Setters
    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category;
    }

    public void setCategoryName(String categoryName) {
        this.category = categoryName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTotalAmount() {
        return amount;
    }

    public void setTotalAmount(double totalAmount) {
        this.amount = totalAmount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTransactionCount() {
        return count;
    }

    public void setTransactionCount(int transactionCount) {
        this.count = transactionCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(double averageAmount) {
        this.averageAmount = averageAmount;
    }

    public double getHighestAmount() {
        return highestAmount;
    }

    public void setHighestAmount(double highestAmount) {
        this.highestAmount = highestAmount;
    }

    public double getLowestAmount() {
        return lowestAmount;
    }

    public void setLowestAmount(double lowestAmount) {
        this.lowestAmount = lowestAmount;
    }

    @Override
    public String toString() {
        return "CategoryAnalysis{" +
                "category='" + category + '\'' +
                ", amount=" + amount +
                ", count=" + count +
                ", percentage=" + percentage +
                '}';
    }
}