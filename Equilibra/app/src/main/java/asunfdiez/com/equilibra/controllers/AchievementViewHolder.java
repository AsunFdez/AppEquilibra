package asunfdiez.com.equilibra.controllers;

import androidx.recyclerview.widget.RecyclerView;

import asunfdiez.com.equilibra.databinding.ItemAchievementBinding;
import asunfdiez.com.equilibra.models.Achievement;

/**
 * Clase que gestiona la visualización individual de un logro dentro de un RecyclerView.
 * 
 * <p>Esta clase utiliza Data Binding para vincular dinámicamente el objeto Achievement 
 * con la vista del elemento de la lista, asegurando que la interfaz refleje el 
 * estado (bloqueado/desbloqueado) y los detalles del logro.</p>
 */
public class AchievementViewHolder extends RecyclerView.ViewHolder {
    // Vinculación de la vista del elemento del logro
    private final ItemAchievementBinding binding;

    public AchievementViewHolder(ItemAchievementBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Vincula los datos de un logro con los componentes visuales del ViewHolder.
     * @param achievement El objeto logro a visualizar.
     */
    public void bind( Achievement achievement) {
        binding.setAchievement(achievement);
        binding.executePendingBindings();
    }
}