package asunfdiez.com.equilibra.models;

/**
 * InterventionMessage - Modelo para mensajes de intervención suave
 * Contiene el título, mensaje y consejos personalizados
 */
public class InterventionMessage {

    private String title;
    private String message;
    private String[] tips;
    private String severity; // "low", "medium", "high"

    // Constructor vacío
    public InterventionMessage() {
        this.tips = new String[3];
        this.severity = "medium";
    }

    // Constructor completo
    public InterventionMessage(String title, String message, String[] tips, String severity) {
        this.title = title;
        this.message = message;
        this.tips = tips;
        this.severity = severity;
    }

    // Getters y Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getTips() {
        return tips;
    }

    public void setTips(String[] tips) {
        this.tips = tips;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("InterventionMessage{");
        sb.append("title='").append(title).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", severity='").append(severity).append('\'');
        sb.append(", tips=[");
        if (tips != null) {
            for (int i = 0; i < tips.length; i++) {
                sb.append(tips[i]);
                if (i < tips.length - 1) sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
