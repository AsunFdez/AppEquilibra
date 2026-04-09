package asunfdiez.com.equilibra.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import asunfdiez.com.equilibra.models.EmotionalState;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static EmotionalState fromString(String value) {
        return value == null ? null : gson.fromJson(value, EmotionalState.class);
    }

    @TypeConverter
    public static String fromEmotionalState(EmotionalState emotionalState) {
        return emotionalState == null ? null : gson.toJson(emotionalState);
    }
}
