package asunfdiez.com.equilibra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import asunfdiez.com.equilibra.controllers.ExpenseViewHolder;
import asunfdiez.com.equilibra.databinding.ItemExpenseBinding;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;

/**
 * ExpenseAdapter - Adapter para RecyclerView de gastos
 * Muestra lista de gastos e ingresos con diseño card usando ViewBinding
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

    private final Context context;
    private List<ExpenseWithCategory> expenses;
    private final OnExpenseClickListener listener;
    private final NumberFormat currencyFormat;

    public interface OnExpenseClickListener {
        void onExpenseClick(ExpenseWithCategory expense);
        void onExpenseDelete(ExpenseWithCategory expense);
        void onExpenseEdit(ExpenseWithCategory expense);
    }

    public ExpenseAdapter(Context context, OnExpenseClickListener listener) {
        this.context = context;
        this.expenses = new ArrayList<>();
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseWithCategory expense = expenses.get(position);
        holder.bind(expense, listener);
    }

    @Override
    public int getItemCount() {
        return expenses != null ? expenses.size() : 0;
    }

    /**
     * Actualiza la lista de gastos
     */
    public void setExpenses(List<ExpenseWithCategory> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }


}
