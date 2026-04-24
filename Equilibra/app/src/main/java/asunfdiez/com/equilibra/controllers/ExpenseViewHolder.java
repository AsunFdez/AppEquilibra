package asunfdiez.com.equilibra.controllers;

import androidx.recyclerview.widget.RecyclerView;

import asunfdiez.com.equilibra.adapters.ExpenseAdapter;
import asunfdiez.com.equilibra.databinding.ItemExpenseBinding;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;

/**
 * ViewHolder que representa visualmente un registro de gasto en una lista.
 * 
 * <p>Gestiona la vinculación de datos entre el modelo enriquecido ExpenseWithCategory 
 * y la vista del elemento, configurando además los escuchadores de eventos para 
 * acciones como clic simple, edición o eliminación del gasto.</p>
 */
public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    // Vinculación de la vista del elemento de gasto
    private final ItemExpenseBinding binding;

    public ExpenseViewHolder(ItemExpenseBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Vincula los datos de una transacción y configura los eventos de interacción.
     * 
     * @param expense El gasto con información de categoría a visualizar.
     * @param listener El escuchador de eventos para gestionar las acciones del usuario.
     */
    public void bind(ExpenseWithCategory expense, ExpenseAdapter.OnExpenseClickListener listener) {
        binding.setExpense(expense);

        // Listeners
        binding.cardExpense.setOnClickListener(v -> {
            if (listener != null) listener.onExpenseClick(expense);
        });

        binding.imgEdit.setOnClickListener(v -> {
            if (listener != null) listener.onExpenseEdit(expense);
        });

        binding.imgDelete.setOnClickListener(v -> {
            if (listener != null) listener.onExpenseDelete(expense);
        });

        // Forzar actualización de bindings
        binding.executePendingBindings();
    }
}
