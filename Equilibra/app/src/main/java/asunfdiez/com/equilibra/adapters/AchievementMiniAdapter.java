package asunfdiez.com.equilibra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.databinding.ItemAchievementMiniBinding;
import asunfdiez.com.equilibra.models.Achievement;

/**
 * AchievementMiniAdapter - Adapter compacto para mostrar logros recientes
 *
 * Responsabilidad:
 * - Mostrar un grid de logros desbloqueados recientemente
 * - Versión compacta del AchievementAdapter para usar en el preview de pantalla principal
 *
 * Patrón:
 * - MVVM con Data Binding
 * - RecyclerView.Adapter estándar
 */
public class AchievementMiniAdapter extends RecyclerView.Adapter<AchievementMiniAdapter.MiniViewHolder> {

    private List<Achievement> achievements;
    private OnAchievementClickListener listener;

    /**
     * Constructor del adaptador
     *
     * @param listener Callback para clicks en logros
     */
    public AchievementMiniAdapter(OnAchievementClickListener listener) {
        this.achievements = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAchievementMiniBinding binding = ItemAchievementMiniBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MiniViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.bind(achievement);

        // Configurar click
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

    /**
     * Actualiza la lista de logros
     *
     * @param achievements Lista de logros a mostrar
     */
    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements != null ? achievements : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Define el listener para clicks
     *
     * @param listener Callback
     */
    public void setOnAchievementClickListener(OnAchievementClickListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder para items mini
     */
    static class MiniViewHolder extends RecyclerView.ViewHolder {
        private final ItemAchievementMiniBinding binding;

        MiniViewHolder(ItemAchievementMiniBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Achievement achievement) {
            binding.setAchievement(achievement);
            binding.executePendingBindings();
        }
    }

    /**
     * Interface para manejar clicks en logros
     */
    public interface OnAchievementClickListener {
        void onAchievementClick(Achievement achievement);
    }
}
