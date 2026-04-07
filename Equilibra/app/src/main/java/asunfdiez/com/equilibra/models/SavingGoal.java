package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * SavingGoal - Modelo de datos para objetivos de ahorro
 * Representa un objetivo de ahorro del usuario con meta y plazo
 */
@Entity(tableName = Constants.TABLE_SAVING_GOALS)
public class SavingGoal {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private long userId;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private String deadline; // Formato: yyyy-MM-dd
    private long createdAt;
    private long updatedAt;
    private boolean completed;

    // Constructor vacío
    public SavingGoal() {
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
