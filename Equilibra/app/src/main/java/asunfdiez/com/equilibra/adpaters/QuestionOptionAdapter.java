package asunfdiez.com.equilibra.adapters;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.QuestionOption;

public class QuestionOptionAdapter {
    private final List<QuestionOption> options = new ArrayList<>();
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String id, String text);
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.listener = listener;
    }

    public void setOptions(List<QuestionOption> newOptions) {
        this.options.clear();
        if (newOptions != null) {
            this.options.addAll(newOptions);
        }
    }

    public void addOption(QuestionOption option) {
        if (option != null) {
            this.options.add(option);
        }
    }

    public void addOptionsToContainer(LinearLayout container, LayoutInflater inflater) {
        if (container == null || inflater == null) {
            return;
        }

        container.removeAllViews();

        for (QuestionOption option : options) {
            CardView card = (CardView) inflater.inflate(R.layout.onboarding_item_question_option, container, false);
            TextView optionText = card.findViewById(R.id.optionText);
            optionText.setText(option.getText());

            final String optionId = option.getId();
            final String optionTextValue = option.getText();

            card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(optionId, optionTextValue);
                }
            });

            container.addView(card);
        }
    }
}
