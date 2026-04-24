package asunfdiez.com.equilibra.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.databinding.ItemCategoryLegendBinding;
import asunfdiez.com.equilibra.models.CategoryAnalysis;

/**
 * Adaptador para mostrar la leyenda explicativa de los gráficos de análisis por categoría.
 * 
 * <p>Muestra una lista de categorías con sus respectivos colores, porcentajes de gasto 
 * y montos totales, ayudando al usuario a interpretar la información visual 
 * presentada en los gráficos circulares o de barras.</p>
 */
public class CategoryLegendAdapter extends RecyclerView.Adapter<CategoryLegendAdapter.ViewHolder> {

    // Lista de resultados del análisis por categoría
    private List<CategoryAnalysis> items = new ArrayList<>();
    
    // Matriz de colores asociados a las porciones del gráfico
    private int[] colors = new int[0];

    public void setItems(List<CategoryAnalysis> items, int[] colors) {
        this.items = items;
        this.colors = colors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryLegendBinding binding = ItemCategoryLegendBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryAnalysis item = items.get(position);
        holder.binding.setCategory(item);
        
        if (colors != null && position < colors.length) {
            GradientDrawable background = (GradientDrawable) holder.binding.colorIndicator.getBackground();
            background.setColor(colors[position]);
        }
        
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCategoryLegendBinding binding;

        ViewHolder(ItemCategoryLegendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}