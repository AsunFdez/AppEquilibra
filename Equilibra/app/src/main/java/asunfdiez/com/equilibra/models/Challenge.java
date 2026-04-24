package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que representa un desafío o reto financiero para el usuario.
 * 
 * <p>Los desafíos son objetivos temporales que el usuario puede cumplir para ganar 
 * recompensas en experiencia (XP). Pueden estar vinculados a categorías específicas 
 * y tienen una fecha de inicio y fin determinada.</p>
 */
@Entity (tableName = Constants.TABLE_CHALLENGES)
public class Challenge {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    
    // ID del usuario al que pertenece el desafío
    private long userId;
    
    // Título descriptivo del reto
    private String title;
    
    // Detalle de las condiciones para completar el reto
    private String description;
    
    // Categoría de gasto a la que aplica el reto (opcional)
    private String category;
    
    // Monto objetivo (ej. no gastar más de X, o ahorrar Y)
    private double targetAmount;
    
    // Progreso actual del usuario hacia el objetivo
    private double currentAmount;
    
    // Fecha de inicio del desafío (formato yyyy-MM-dd)
    private String startDate; 
    
    // Fecha de vencimiento del desafío (formato yyyy-MM-dd)
    private String endDate; 
    
    // Estado de cumplimiento del desafío
    private boolean completed;
    
    // Cantidad de puntos de experiencia otorgados al finalizar con éxito
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
