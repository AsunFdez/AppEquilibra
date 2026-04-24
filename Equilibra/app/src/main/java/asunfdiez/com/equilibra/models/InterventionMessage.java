package asunfdiez.com.equilibra.models;

/**
 * Clase que estructura los mensajes de intervención suave presentados al usuario.
 * 
 * <p>Contiene el título, el cuerpo del mensaje, una lista de consejos personalizados 
 * y un nivel de severidad. Se utiliza para representar visualmente las alertas 
 * en la pantalla de intervención suave.</p>
 */
public class InterventionMessage {

    // Título de la alerta o consejo
    private String title;
    
    // Cuerpo principal del mensaje informativo
    private String message;
    
    // Lista de pequeños consejos prácticos (tips)
    private String[] tips;
    
    // Nivel de urgencia o severidad ("low", "medium", "high")
    private String severity; 

    // Constructor vacío inicializa con valores por defecto
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
