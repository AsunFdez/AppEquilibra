package asunfdiez.com.equilibra.models;

/**
 * Expense - Modelo de datos para gastos e ingresos
 * Representa una transacción financiera en la aplicación
 */
public class Expense {

    private long id;
    private String type; // "expense" o "income"
    private double amount;
    private String description;
    private String category;
    private String date; // Formato: dd/MM/yyyy
    private long timestamp; // Timestamp en milisegundos
    private String notes;

    // Constructor vacío
    public Expense() {
    }

    // Constructor completo
    public Expense(long id, String type, double amount, String description,
                   String category, String date, long timestamp) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.timestamp = timestamp;
    }

    // Getters y Setters
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Verifica si es un gasto
     */
    public boolean isExpense() {
        return "expense".equals(type);
    }

    /**
     * Verifica si es un ingreso
     */
    public boolean isIncome() {
        return "income".equals(type);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
