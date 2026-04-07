package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * InterventionLog - Modelo de datos para registro de intervenciones
 * Almacena las intervenciones inteligentes sugeridas al usuario
 */

@Entity(tableName = Constants.TABLE_INTERVENTION_LOG)
public class InterventionLog {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private long userId;
    private long expenseId;
    private String interventionType; // "goal_impact", "saving_alternative", "emotional_trigger"
    private String message;
    private String impactOnGoal;
    private String alternativeSuggestion;
    private long timestamp;

    // Constructor vacío
    public InterventionLog() {
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

    public long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(long expenseId) {
        this.expenseId = expenseId;
    }

    public String getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(String interventionType) {
        this.interventionType = interventionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImpactOnGoal() {
        return impactOnGoal;
    }

    public void setImpactOnGoal(String impactOnGoal) {
        this.impactOnGoal = impactOnGoal;
    }

    public String getAlternativeSuggestion() {
        return alternativeSuggestion;
    }

    public void setAlternativeSuggestion(String alternativeSuggestion) {
        this.alternativeSuggestion = alternativeSuggestion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "InterventionLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", expenseId=" + expenseId +
                ", interventionType='" + interventionType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
