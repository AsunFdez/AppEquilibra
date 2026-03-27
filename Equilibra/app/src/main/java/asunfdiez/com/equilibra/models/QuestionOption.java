package asunfdiez.com.equilibra.models;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.utils.Constants;

public record QuestionOption(String id, String text) {

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
        questions.add(  new QuestionOption("5min", context.getString(R.string.q2_option_1)));
        questions.add(  new QuestionOption("10min", context.getString(R.string.q2_option_2)));
        questions.add(  new QuestionOption("15min", context.getString(R.string.q2_option_3)));

        return questions;
    }


}
