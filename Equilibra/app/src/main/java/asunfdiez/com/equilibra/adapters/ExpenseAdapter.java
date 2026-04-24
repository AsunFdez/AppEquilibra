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
 * Adaptador para gestionar la lista detallada de transacciones financieras (gastos e ingresos).
 * 
 * <p>Se encarga de renderizar cada registro de gasto utilizando el modelo enriquecido 
 * {@link ExpenseWithCategory}, aplicando formatos de moneda locales y gestionando 
 * las interacciones del usuario (clic, edición, eliminación) a través de una 
 * interfaz de escucha específica.</p>
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

    private final Context context;
    
    // Lista de transacciones enriquecidas con información de categoría
    private List<ExpenseWithCategory> expenses;
    
    // Escuchador para acciones sobre los elementos de la lista
    private final OnExpenseClickListener listener;
    
    // Formateador de moneda configurado para la región local
    private final NumberFormat currencyFormat;

    /**
     * Interfaz para la gestión de eventos de usuario sobre los gastos.
     */
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
