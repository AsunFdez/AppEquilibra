package asunfdiez.com.equilibra.models;

import java.util.List;

/**
 * Modelo de datos para la representación de recordatorios generados por detección de patrones.
 * 
 * <p>Esta clase encapsula la información sobre comportamientos de gasto repetitivos 
 * identificados por el sistema, ofreciendo mensajes personalizados y sugerencias 
 * de ahorro basadas en la frecuencia y el monto acumulado en una categoría.</p>
 */
public class SmartReminder {

    // Categoría donde se ha detectado el patrón
    private String category;
    
    // Cantidad de transacciones que forman parte del patrón detectado
    private int occurrenceCount; 
    
    // Suma total de dinero involucrado en estas transacciones
    private double totalAmount; 
    
    // Marco temporal del análisis (ej. "en los últimos 7 días")
    private String timeFrame; 
    
    // Lista de acciones sugeridas para mitigar este gasto
    private List<String> suggestions; 
    
    // Mensaje descriptivo del patrón detectado (ej. "Has gastado mucho en café esta semana")
    private String patternMessage;

    public SmartReminder() {
    }

    public SmartReminder(String category, int occurrenceCount, double totalAmount,
                         String timeFrame, List<String> suggestions, String patternMessage) {
        this.category = category;
        this.occurrenceCount = occurrenceCount;
        this.totalAmount = totalAmount;
        this.timeFrame = timeFrame;
        this.suggestions = suggestions;
        this.patternMessage = patternMessage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getPatternMessage() {
        return patternMessage;
    }

    public void setPatternMessage(String patternMessage) {
        this.patternMessage = patternMessage;
    }

    /**
     * Genera el mensaje del patrón detectado
     */
    public String generatePatternMessage() {
        if (category != null && !category.isEmpty()) {
            return String.format("Es tu %d° %s %s",
                    occurrenceCount,
                    category.toLowerCase(),
                    timeFrame);
        }
        return "";
    }

    @Override
    public String toString() {
        return "SmartReminder{" +
                "category='" + category + '\'' +
                ", occurrenceCount=" + occurrenceCount +
                ", totalAmount=" + totalAmount +
                ", timeFrame='" + timeFrame + '\'' +
                ", patternMessage='" + patternMessage + '\'' +
                '}';
    }
}