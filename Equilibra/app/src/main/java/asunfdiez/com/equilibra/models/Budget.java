package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que representa el presupuesto configurado por el usuario.
 * 
 * <p>Define los límites de gasto para diferentes periodos (diario, semanal, mensual) 
 * y la moneda de preferencia. Esta entidad es fundamental para el seguimiento 
 * del estado financiero y la generación de alertas de gasto.</p>
 */
@Entity (tableName = Constants.TABLE_BUDGETS)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;

    // Límite de gasto mensual establecido
    private double monthlyBudget;
    
    // Límite de gasto semanal (calculado o establecido)
    private double weeklyBudget;
    
    // Límite de gasto diario (calculado o establecido)
    private double dailyBudget;
    
    // Código de la moneda (ej. "EUR", "USD")
    private String currency;
    
    // Indica si este presupuesto está actualmente activo
    private boolean enabled;
    
    // Marcas de tiempo para auditoría de creación y actualización
    private long createdAt;
    private long updatedAt;

    /**
     * Constructor requerido por Room
     */
    public Budget() {
        this.currency = Constants.CURRENCY_EUR;
        this.enabled = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // ========== GETTERS Y SETTERS ==========

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
        this.updatedAt = System.currentTimeMillis();
    }

    public double getWeeklyBudget() {
        return weeklyBudget;
    }

    public void setWeeklyBudget(double weeklyBudget) {
        this.weeklyBudget = weeklyBudget;
        this.updatedAt = System.currentTimeMillis();
    }

    public double getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(double dailyBudget) {
        this.dailyBudget = dailyBudget;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Calcula el presupuesto semanal basado en el mensual
     */
    public void calculateWeeklyFromMonthly() {
        this.weeklyBudget = monthlyBudget / 4.0;
    }

    /**
     * Calcula el presupuesto diario basado en el mensual
     */
    public void calculateDailyFromMonthly() {
        this.dailyBudget = monthlyBudget / 30.0;
    }

    /**
     * Obtiene el símbolo de moneda
     */
    public String getCurrencySymbol() {
        switch (currency) {
            case Constants.CURRENCY_EUR:
                return "€";
            case Constants.CURRENCY_USD:
                return "$";
            case Constants.CURRENCY_GBP:
                return "£";
            default:
                return currency;
        }
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", monthlyBudget=" + monthlyBudget +
                ", weeklyBudget=" + weeklyBudget +
                ", dailyBudget=" + dailyBudget +
                ", currency='" + currency + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
