package asunfdiez.com.equilibra.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import asunfdiez.com.equilibra.models.EmotionalState;

/**
 * Clase de utilidad para la conversión de tipos de datos en la base de datos Room.
 * 
 * <p>Proporciona convertidores para tipos complejos que no pueden almacenarse 
 * de forma nativa en SQLite, como el objeto {@link EmotionalState}, convirtiéndolos 
 * a una representación en cadena (JSON) y viceversa utilizando la librería Gson.</p>
 */
public class Converters {
    // Instancia de Gson para la serialización y deserialización de objetos
    private static final Gson gson = new Gson();

    /**
     * Convierte una cadena JSON en un objeto EmotionalState.
     */
    @TypeConverter
    public static EmotionalState fromString(String value) {
        return value == null ? null : gson.fromJson(value, EmotionalState.class);
    }

    /**
     * Convierte un objeto EmotionalState en su representación JSON como cadena.
     */
    @TypeConverter
    public static String fromEmotionalState(EmotionalState emotionalState) {
        return emotionalState == null ? null : gson.toJson(emotionalState);
    }
}
