package asunfdiez.com.equilibra.adapters;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.OnboardingItemQuestionOptionBinding;
import asunfdiez.com.equilibra.models.QuestionOption;

/**
 * Adaptador personalizado para gestionar la selección de opciones en los cuestionarios.
 *
 * <p>Aunque no extiende de RecyclerView.Adapter, esta clase gestiona dinámicamente
 * la creación y visualización de tarjetas de opción en un contenedor lineal (LinearLayout),
 * manejando el estado de selección única y notificando a través de un escuchador.
 * Utiliza ViewBinding para acceso seguro a las vistas.</p>
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

    /**
     * Añade las opciones de pregunta al contenedor usando ViewBinding.
     * @param container Contenedor LinearLayout donde se añadirán las tarjetas
     * @param inflater LayoutInflater para inflar los layouts
     */
    public void addOptionsToContainer(@NonNull LinearLayout container, @NonNull LayoutInflater inflater) {
        if (container == null || inflater == null) {
            return;
        }

        container.removeAllViews();

        for (QuestionOption option : options) {
            // Usar el método inflate propio de la clase generada para evitar errores de tipos
            OnboardingItemQuestionOptionBinding binding = OnboardingItemQuestionOptionBinding.inflate(
                    inflater,
                    container,
                    false
            );

            binding.optionText.setText(option.text());

            final String optionId = option.id();
            final String optionTextValue = option.text();

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(optionId, optionTextValue);
                }
            });

            container.addView(binding.getRoot());
        }
    }
}