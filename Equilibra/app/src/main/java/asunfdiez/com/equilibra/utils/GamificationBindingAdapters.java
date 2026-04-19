package asunfdiez.com.equilibra.utils;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import asunfdiez.com.equilibra.adapters.AchievementMiniAdapter;
import asunfdiez.com.equilibra.models.Achievement;

/**
 * BindingAdapters para Gamificación
 *
 * Responsabilidad:
 * - Configurar RecyclerView de logros desde XML Data Binding
 * - Manejar actualizaciones reactivas de datos
 *
 * Patrón:
 * - Android Data Binding BindingAdapter
 */
public class GamificationBindingAdapters {

    /**
     * Configura el adaptador de logros mini para un RecyclerView
     * Uso en XML: app:achievementsList="@{gamificationViewModel.recentUnlockedAchievements}"
     *
     * @param recyclerView El RecyclerView a configurar
     * @param achievements Lista de logros a mostrar
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
