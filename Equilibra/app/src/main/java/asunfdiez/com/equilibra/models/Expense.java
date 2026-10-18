package asunfdiez.com.equilibra.models;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que representa una transacción financiera individual.
 * 
 * <p>Almacena la información de un gasto o ingreso, incluyendo el importe, la categoría
 * a la que pertenece, una descripción opcional y la fecha y hora de la transacción. 
 * Esta entidad es el núcleo de la persistencia financiera en la base de datos Room.</p>
 */
@Entity(
        tableName = Constants.TABLE_EXPENSES,
        foreignKeys = {
                @ForeignKey(
                        entity = UserProfile.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = ExpenseCategory.class,
                        parentColumns = "id",
                        childColumns = "category",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {@Index("userId"), @Index("category")}
)
public class Expense {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    private long id;

    private long userId;
    
    // Tipo de transacción: "expense" (gasto) o "income" (ingreso)
    private String type; 
    
    // Importe numérico de la transacción
    private double amount;
    
    // ID de la categoría asociada
    @Nullable
    private Long category;
    
    // Nota adicional sobre el gasto
    private String description;
    
    // Fecha en formato yyyy-MM-dd
    private String date;
    
    // Hora en formato HH:mm
    private String time;
    
    // Marca de tiempo Unix en milisegundos para ordenación y comparaciones rápidas
    private long timestamp;
    private boolean isRecurring; // Si es un gasto recurrente
    private int patternCount; // Cuántas veces se ha repetido este patrón
    private String notes; // Notas adicionales

    // Constructor vacío requerido por Room si hay otros constructores
    public Expense() {}

    @Ignore
    public Expense(String type, double amount, int category, String description, String date)  {
        this.type = type;
        this.amount = amount;
        this.category = (long) category;
        this.description = description;
        this.date = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(date);
            this.timestamp = parsedDate.getTime();
        } catch (ParseException e) {
            Log.e("Expense", "Error al parsear la fecha: " + e.getMessage());
        }

    }

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

    @Nullable
    public Long getCategory() {
        return category;
    }

    public void setCategory(@Nullable Long category) {
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

    public String getFormattedDateTime() {
        if (this.time == null || this.date == null) return "";
        return this.date + " - " + this.time;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", userId=" + userId +
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
