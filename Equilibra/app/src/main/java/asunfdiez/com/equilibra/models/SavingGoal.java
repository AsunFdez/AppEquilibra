package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que representa una meta u objetivo de ahorro establecido por el usuario.
 * 
 * <p>Contiene información sobre el monto objetivo, el progreso actual, la fecha límite 
 * y el ahorro mensual necesario para alcanzar la meta. Es una entidad persistida 
 * que ayuda a motivar al usuario mediante el seguimiento de sus propósitos financieros.</p>
 */
@Entity(tableName = Constants.TABLE_SAVING_GOALS)
public class SavingGoal {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    
    // ID del usuario dueño de la meta
    private long userId;
    
    // Nombre descriptivo de la meta (ej. "Coche", "Viaje")
    private String name;
    
    // Monto total que se desea ahorrar
    private double targetAmount;
    
    // Monto acumulado hasta el momento
    private double currentAmount;
    
    // Fecha límite para alcanzar el objetivo (formato yyyy-MM-dd)
    private String deadline; 
    
    // Marcas de tiempo para auditoría
    private long createdAt;
    private long updatedAt;
    
    // Indica si la meta ya ha sido alcanzada
    private boolean completed;
    
    // Monto calculado que debería ahorrarse mensualmente
    private double monthlySavingAmount;
    
    // Cantidad de meses que faltan para llegar a la fecha límite
    private int monthsRemaining;

    // Constructor vacío
    public SavingGoal() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.currentAmount = 0;
        this.completed = false;
    }


    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getMonthlySavingAmount() {
        return monthlySavingAmount;
    }

    public void setMonthlySavingAmount(double monthlySavingAmount) {
        this.monthlySavingAmount = monthlySavingAmount;
    }

    public int getMonthsRemaining() {
        return monthsRemaining;
    }

    public void setMonthsRemaining(int monthsRemaining) {
        this.monthsRemaining = monthsRemaining;
    }

    /**
     * Calcula el porcentaje de avance del objetivo
     */
    public double getProgressPercentage() {
        if (targetAmount <= 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }

    /**
     * Calcula cuánto falta para alcanzar el objetivo
     */
    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentAmount);
    }

    @Override
    public String toString() {
        return "SavingGoal{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", deadline='" + deadline + '\'' +
                ", completed=" + completed +
                '}';
    }
}
