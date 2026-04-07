package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Challenge - Modelo de datos para desafíos semanales/mensuales
 * Representa un reto temporal con recompensa en XP
 */
@Entity (tableName = Constants.TABLE_CHALLENGES)
public class Challenge {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private long userId;
    private String title;
    private String description;
    private String category;
    private double targetAmount;
    private double currentAmount;
    private String startDate; // Formato: yyyy-MM-dd
    private String endDate; // Formato: yyyy-MM-dd
    private boolean completed;
    private int xpReward;

    // Constructor vacío
    public Challenge() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    /**
     * Calcula el porcentaje de progreso del desafío
     */
    public double getProgressPercentage() {
        if (targetAmount <= 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }

    /**
     * Verifica si el desafío está completado
     */
    public void updateCompletionStatus() {
        if (currentAmount >= targetAmount) {
            this.completed = true;
        }
    }

    /**
     * Suma cantidad al avance actual
     */
    public void addProgress(double amount) {
        this.currentAmount += amount;
        updateCompletionStatus();
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", completed=" + completed +
                ", xpReward=" + xpReward +
                '}';
    }
}
