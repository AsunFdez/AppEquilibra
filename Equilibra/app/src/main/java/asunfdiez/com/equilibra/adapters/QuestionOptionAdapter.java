package asunfdiez.com.equilibra.adapters;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.QuestionOption;

/**
 * Adaptador personalizado para gestionar la selección de opciones en los cuestionarios.
 * 
 * <p>Aunque no extiende de RecyclerView.Adapter, esta clase gestiona dinámicamente 
 * la creación y visualización de tarjetas de opción en un contenedor lineal (LinearLayout), 
 * manejando el estado de selección única y notificando a través de un escuchador.</p>
 */
public class QuestionOptionAdapter {
    // Lista de opciones de respuesta a renderizar
    private final List<QuestionOption> options = new ArrayList<>();
    
    // Escuchador para la selección de una opción
    private OnOptionSelectedListener listener;

    /**
     * Interfaz para notificar la selección de una opción de respuesta.
     */
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
            optionText.setText(option.text());

            final String optionId = option.id();
            final String optionTextValue = option.text();

            card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(optionId, optionTextValue);
                }
            });

            container.addView(card);
        }
    }
}
