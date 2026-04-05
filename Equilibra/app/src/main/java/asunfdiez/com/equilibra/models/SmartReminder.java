package asunfdiez.com.equilibra.models;

import java.util.List;

/**
 * SmartReminder - Modelo para recordatorios inteligentes
 * Detecta patrones de gastos repetitivos
 *
 */
public class SmartReminder {

    private String category;
    private int occurrenceCount; // Número de veces que se ha gastado en esta categoría
    private double totalAmount; // Total gastado en esta categoría
    private String timeFrame; // "esta semana", "este mes", etc.
    private List<String> suggestions; // Sugerencias de ahorro
    private String patternMessage; // Mensaje personalizado del patrón

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