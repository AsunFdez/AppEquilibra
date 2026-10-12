package asunfdiez.com.equilibra.models;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Keep;
import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import asunfdiez.com.equilibra.R;

/**
 * Clase que representa el estado emocional del usuario durante el registro inicial.
 * 
 * <p>Contiene un identificador, el texto descriptivo del sentimiento, un emoji asociado 
 * y un mensaje de validación o consejo específico para ese estado emocional. 
 * Se utiliza para personalizar la experiencia desde el onboarding.</p>
 */
@Keep
public class EmotionalState implements Serializable {
    // Identificador único del estado emocional (ej. "happy", "stressed")
    @PropertyName("id")
    public String id;
    
    // Texto descriptivo del sentimiento
    @PropertyName("text")
    public String text;
    
    // Representación visual (emoji)
    @PropertyName("emoji")
    public String emoji;
    
    // Mensaje de respuesta o validación asociado al estado
    @PropertyName("validation")
    public String validation;

    /**
     * Constructor vacío requerido para la serialización de Firebase Firestore.
     */
    public EmotionalState() {
    }

    public EmotionalState(String id, String text, String emoji, String validation) {
        this.id = id;
        this.text = text;
        this.emoji = emoji;
        this.validation = validation;
    }

    @PropertyName("id")
    public String getId() { return id; }
    @PropertyName("id")
    public void setId(String id) { this.id = id; }

    @PropertyName("text")
    public String getText() { return text; }
    @PropertyName("text")
    public void setText(String text) { this.text = text; }

    @PropertyName("emoji")
    public String getEmoji() { return emoji; }
    @PropertyName("emoji")
    public void setEmoji(String emoji) { this.emoji = emoji; }

    @PropertyName("validation")
    public String getValidation() { return validation; }
    @PropertyName("validation")
    public void setValidation(String validation) { this.validation = validation; }

    /**
     * Devuelve la lista de las 4 emociones predefinidas
     */
    public static List<EmotionalState> getDefaultEmotions(Context context) {
        List<EmotionalState> emotions = new ArrayList<>();

        emotions.add(new EmotionalState("tranquilo",
                context.getString(R.string.emotion_tranquilo_text),
                context.getString(R.string.emotion_tranquilo_emoji),
                context.getString(R.string.validation_calm)));

        emotions.add(new EmotionalState("ansioso",
                context.getString(R.string.emotion_ansioso_text),
                context.getString(R.string.emotion_ansioso_emoji),
                context.getString(R.string.validation_anxious)));

        emotions.add(new EmotionalState("estresado",
                context.getString(R.string.emotion_estresado_text),
                context.getString(R.string.emotion_estresado_emoji),
                context.getString(R.string.validation_stressed)));

        emotions.add(new EmotionalState("confundido",
                context.getString(R.string.emotion_confundido_text),
                context.getString(R.string.emotion_confundido_emoji),
                context.getString(R.string.validation_confused)));

        return emotions;
    }

    public static EmotionalState getById(Context context, String id) {
        return getDefaultEmotions(context).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "EmotionalState{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", emoji='" + emoji + '\'' +
                ", validation='" + validation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmotionalState that = (EmotionalState) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text) &&
                Objects.equals(emoji, that.emoji) &&
                Objects.equals(validation, that.validation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, emoji, validation);
    }
}
