package asunfdiez.com.equilibra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ItemCategoryBinding;
import asunfdiez.com.equilibra.models.ExpenseCategory;

/**
 * Adaptador encargado de mostrar la cuadrícula de categorías de gastos.
 * 
 * <p>Permite al usuario seleccionar una categoría durante el registro de un nuevo gasto. 
 * Utiliza Data Binding para actualizar el estado visual de selección y mostrar el 
 * nombre e icono correspondiente a cada categoría.</p>
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    
    // Lista de categorías disponibles para clasificar gastos
    private List<ExpenseCategory> categories;
    
    // Escuchador de eventos de selección de categoría
    private OnCategoryClickListener listener;

    /**
     * Interfaz para gestionar la interacción del usuario con las categorías.
     */
    public interface OnCategoryClickListener {
        void onCategoryClick(ExpenseCategory category);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemCategoryBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_category,
                parent,
                false
        );

        // Ajustar el ancho para que quepan exactamente 3 columnas en la pantalla
        int parentWidth = parent.getMeasuredWidth();
        if (parentWidth <= 0) {
            // Fallback: usar el ancho de la pantalla menos el padding estimado (16dp * 2)
            float density = context.getResources().getDisplayMetrics().density;
            parentWidth = (int) (context.getResources().getDisplayMetrics().widthPixels - (32 * density));
        }
        
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        layoutParams.width = parentWidth / 3;
        binding.getRoot().setLayoutParams(layoutParams);

        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ExpenseCategory category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Actualiza la lista de categorías
     */
    public void updateCategories(List<ExpenseCategory> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder con Data Binding
     */
    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private ItemCategoryBinding binding;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExpenseCategory category, OnCategoryClickListener listener) {
            binding.setCategory(category);
            binding.executePendingBindings();

            // Configurar click listener
            binding.layoutCategory.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}

