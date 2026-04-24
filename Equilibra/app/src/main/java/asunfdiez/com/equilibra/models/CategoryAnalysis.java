package asunfdiez.com.equilibra.models;

import androidx.room.Ignore;

/**
 * Clase que representa el resultado de un análisis financiero agrupado por categoría.
 * 
 * <p>Proporciona información consolidada sobre el monto total gastado, el número de 
 * transacciones realizadas y el porcentaje que representa esta categoría sobre el 
 * total de gastos. Se utiliza principalmente para alimentar los gráficos de análisis.</p>
 */
public class CategoryAnalysis {

    // Nombre de la categoría analizada
    private String category;
    
    // Suma total de los montos de los gastos en esta categoría
    private double amount;
    
    // Cantidad total de registros (gastos) en esta categoría
    private int count; 
    
    @Ignore
    // Proporción porcentual respecto al gasto total consolidado
    private double percentage;

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