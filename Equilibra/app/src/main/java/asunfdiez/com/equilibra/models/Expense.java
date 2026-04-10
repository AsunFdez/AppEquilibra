package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Expense - Modelo para representar un gasto o ingreso
 */
@Entity(tableName = Constants.TABLE_EXPENSES)
public class Expense {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    private long id;
    private String type; // "expense" o "income"
    private double amount;
    private String category;
    private String description;
    private String date;
    private String time;
    private long timestamp;
    private boolean isRecurring; // Si es un gasto recurrente
    private int patternCount; // Cuántas veces se ha repetido este patrón
    private String notes; // Notas adicionales

    // Constructor vacío requerido por Room si hay otros constructores
    public Expense() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public int getPatternCount() {
        return patternCount;
    }

    public void setPatternCount(int patternCount) {
        this.patternCount = patternCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isExpense() {
        return "expense".equals(type);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", timestamp=" + timestamp +
                ", isRecurring=" + isRecurring +
                ", patternCount=" + patternCount +
                ", notes='" + notes + '\'' +
                '}';
    }
}
