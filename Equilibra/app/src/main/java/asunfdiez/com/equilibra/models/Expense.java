package asunfdiez.com.equilibra.models;

/**
 * Expense - Modelo para representar un gasto o ingreso
 */
public class Expense {

    private long id;
    private String type; // "expense" o "income"
    private double amount;
    private String category;
    private String description;
    private String date;
    private long timestamp;
    private boolean isRecurring; // Si es un gasto recurrente
    private int patternCount; // Cuántas veces se ha repetido este patrón
    private String notes; // Notas adicionales

    // Constructor vacío
    public Expense() {
        this.timestamp = System.currentTimeMillis();
        this.type = "expense";
        this.isRecurring = false;
        this.patternCount = 0;
        this.notes = "";
    }

    // Constructor completo
    public Expense(long id, String type, double amount, String category,
                   String description, String date, long timestamp,
                   boolean isRecurring, int patternCount, String notes) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.timestamp = timestamp;
        this.isRecurring = isRecurring;
        this.patternCount = patternCount;
        this.notes = notes;
    }

    // Constructor sin ID (para nuevos gastos)
    public Expense(String type, double amount, String category,
                   String description, String date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.timestamp = System.currentTimeMillis();
        this.isRecurring = false;
        this.patternCount = 0;
        this.notes = "";
    }

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
