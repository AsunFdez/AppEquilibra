package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que registra las intervenciones inteligentes realizadas por la app.
 * 
 * <p>Almacena el historial de avisos y sugerencias presentados al usuario, vinculándolos 
 * a gastos específicos o situaciones financieras. Permite auditar qué tipo de intervenciones 
 * (impacto en metas, alternativas de ahorro, etc.) son más frecuentes.</p>
 */

@Entity(tableName = Constants.TABLE_INTERVENTION_LOG)
public class InterventionLog {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    
    // ID del usuario que recibió la intervención
    private long userId;
    
    // ID del gasto que disparó la intervención (opcional)
    private long expenseId;
    
    // Tipo de intervención: "goal_impact", "saving_alternative", etc.
    private String interventionType; 
    
    // Mensaje descriptivo mostrado al usuario
    private String message;
    
    // Descripción del impacto en las metas de ahorro
    private String impactOnGoal;
    
    // Sugerencia alternativa de comportamiento o ahorro
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
