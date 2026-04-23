package asunfdiez.com.equilibra.models;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.utils.Constants;

public class QuestionOption {
    private final String id;
    private final String text;

    public QuestionOption(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String id() { return id; }
    public String text() { return text; }

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
