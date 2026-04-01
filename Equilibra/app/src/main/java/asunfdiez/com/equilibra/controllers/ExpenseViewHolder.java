package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.ExpenseAdapter;
import asunfdiez.com.equilibra.databinding.ItemExpenseBinding;
import asunfdiez.com.equilibra.models.Expense;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    private final ItemExpenseBinding binding;

    public ExpenseViewHolder(ItemExpenseBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Expense expense, ExpenseAdapter.OnExpenseClickListener listener, NumberFormat format, Context context) {
        binding.setExpense(expense);

        // Monto formateado
        String amountText = format.format(expense.getAmount());

        // Estilo según tipo (Gasto/Ingreso)
        if (expense.isExpense()) {
            binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.equilibra_accent));
            binding.tvAmount.setText(String.format("-%s", amountText));
            binding.imgIcon.setImageResource(R.drawable.ic_expense);
            binding.imgIcon.setBackgroundResource(R.drawable.bg_circle_orange_light);
        } else {
            binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.equilibra_primary));
            binding.tvAmount.setText(String.format("+%s", amountText));
            binding.imgIcon.setImageResource(R.drawable.ic_income);
            binding.imgIcon.setBackgroundResource(R.drawable.bg_circle_green_light);
        }

        // Listeners
        binding.cardExpense.setOnClickListener(v -> {
            if (listener != null) listener.onExpenseClick(expense);
        });

        binding.imgDelete.setOnClickListener(v -> {
            if (listener != null) listener.onExpenseDelete(expense);
        });

        // Forzar actualización de bindings
        binding.executePendingBindings();
    }
}
