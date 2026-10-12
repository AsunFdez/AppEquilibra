package asunfdiez.com.equilibra.models;

import android.content.Context;
import androidx.annotation.Keep;
import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * Clase que representa una opción de respuesta en los cuestionarios de la aplicación.
 * 
 * <p>Utilizada principalmente durante el proceso de onboarding para capturar las 
 * preferencias, misiones o motivaciones del usuario a través de una lista de 
 * opciones seleccionables.</p>
 */
@Keep
public class QuestionOption implements Serializable {
    // Identificador único de la opción
    @PropertyName("id")
    public String id;
    
    // Texto que se muestra al usuario
    @PropertyName("text")
    public String text;

    /**
     * Constructor vacío requerido para la serialización de Firebase Firestore.
     */
    public QuestionOption() {
    }

    public QuestionOption(String id, String text) {
        this.id = id;
        this.text = text;
    }

    @PropertyName("id")
    public String getId() { return id; }
    @PropertyName("id")
    public void setId(String id) { this.id = id; }

    @PropertyName("text")
    public String getText() { return text; }
    @PropertyName("text")
    public void setText(String text) { this.text = text; }

    /**
     * Devuelve la lista de las 3 preguntas predefinidas
     */
    public static List<QuestionOption> getFirstQuestions(Context context) {
        List<QuestionOption> questions = new ArrayList<>();
        questions.add(new QuestionOption("control", context.getString(R.string.q1_option_1)));
        questions.add(new QuestionOption(Constants.ANSWER_ID_SAVE_SPECIFIC, context.getString(R.string.q1_option_2)));
        questions.add(new QuestionOption("reduce-stress", context.getString(R.string.q1_option_3)));
        questions.add(new QuestionOption("learn", context.getString(R.string.q1_option_4)));

        return questions;
    }

    public static List<QuestionOption> getSecondQuestions(Context context) {
        List<QuestionOption> questions = new ArrayList<>();
        questions.add(new QuestionOption("5min", context.getString(R.string.q2_option_1)));
        questions.add(new QuestionOption("10min", context.getString(R.string.q2_option_2)));
        questions.add(new QuestionOption("15min", context.getString(R.string.q2_option_3)));

        return questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionOption that = (QuestionOption) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
