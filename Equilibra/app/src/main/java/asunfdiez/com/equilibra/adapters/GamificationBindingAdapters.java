package asunfdiez.com.equilibra.adapters;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import asunfdiez.com.equilibra.models.Achievement;

/**
 * Adaptadores de vinculación específicos para componentes de gamificación.
 * 
 * <p>Proporciona lógica especializada para el Data Binding en elementos visuales 
 * de logros y progreso, como la configuración automática de adaptadores de lista 
 * en RecyclerViews directamente desde los archivos XML.</p>
 */
public class GamificationBindingAdapters {

    /**
     * Configura y actualiza la lista de logros en un RecyclerView utilizando el mini-adaptador.
     * 
     * @param recyclerView El RecyclerView a configurar.
     * @param achievements Lista de logros a mostrar.
     */
    @BindingAdapter("achievementsList")
    public static void setAchievementsData(RecyclerView recyclerView, List<Achievement> achievements) {
        AchievementMiniAdapter adapter = (AchievementMiniAdapter) recyclerView.getAdapter();
        
        if (adapter == null) {
            adapter = new AchievementMiniAdapter(achievement -> {
                // Click en un logro - puede expandirse con lógica adicional
            });
            recyclerView.setAdapter(adapter);
        }
        
        adapter.setAchievements(achievements);
    }
}
