package asunfdiez.com.equilibra.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;

public record EmotionalState(String id, String text, String emoji, String validation) {

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
                .filter(e -> e.id().equals(id))
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
}