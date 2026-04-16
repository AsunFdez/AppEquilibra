package asunfdiez.com.equilibra.controllers;

import androidx.recyclerview.widget.RecyclerView;

import asunfdiez.com.equilibra.adapters.ExpenseAdapter;
import asunfdiez.com.equilibra.databinding.ItemExpenseBinding;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    private final ItemExpenseBinding binding;

    public ExpenseViewHolder(ItemExpenseBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

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
