package asunfdiez.com.equilibra.models;

import androidx.room.Ignore;

/**
 * CategoryAnalysis - Modelo para análisis por categoría
 * Contiene el total gastado por categoría para gráficos y estadísticas
 */
public class CategoryAnalysis {

    private String category;
    private double amount;
    private int count; // Número de transacciones en esta categoría
    @Ignore
    private double percentage; // Porcentaje del total

    // Constructor vacío
    public CategoryAnalysis() {
    }

    // Getters y Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
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