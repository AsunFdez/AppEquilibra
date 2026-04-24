package asunfdiez.com.equilibra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.controllers.AchievementViewHolder;
import asunfdiez.com.equilibra.databinding.ItemAchievementBinding;
import asunfdiez.com.equilibra.models.Achievement;

/**
 * Adaptador para gestionar la visualización de una lista de logros en un RecyclerView.
 * 
 * <p>Esta clase se encarga de inflar el diseño de cada elemento de logro y de vincular 
 * los datos de los modelos {@link Achievement} con sus respectivos ViewHolders. 
 * Soporta clics en los elementos a través de una interfaz de escucha.</p>
 */
public class AchievementAdapter extends RecyclerView.Adapter<AchievementViewHolder> {
    private final Context context;
    
    // Lista de logros a visualizar
    private List<Achievement> achievements;
    
    // Escuchador de eventos de clic
    private OnAchievementClickListener listener;

    public AchievementAdapter(Context context, OnAchievementClickListener listener) {
        this.context = context;
        this.achievements = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAchievementBinding binding = ItemAchievementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AchievementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.bind(achievement);

        // Configurar el click en el item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAchievementClick(achievement);
            }
        });
    }

    @Override
    public int getItemCount() {
        return achievements != null ? achievements.size() : 0;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    public void setOnAchievementClickListener(OnAchievementClickListener listener) {
        this.listener = listener;
    }

    /**
     * Interface para manejar clicks en logros
     */
    public interface OnAchievementClickListener {
        void onAchievementClick(Achievement achievement);
    }
}
