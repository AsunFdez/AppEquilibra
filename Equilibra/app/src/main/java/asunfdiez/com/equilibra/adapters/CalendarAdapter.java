package asunfdiez.com.equilibra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import asunfdiez.com.equilibra.databinding.ItemCalendarDayBinding;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<Integer> days = new ArrayList<>();
    private Map<Integer, Boolean> daysWithExpenses;
    private int selectedDay = -1;

    public void setCalendar(Calendar calendar) {
        days.clear();
        // Obtener el primer día de la semana para el desplazamiento
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK) - 1; // 0 para Domingo, 1 para Lunes...
        
        // Rellenar días vacíos al inicio
        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add(0); // 0 representa día vacío
        }

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDays; i++) {
            days.add(i);
        }
        notifyDataSetChanged();
    }

    public void setDaysWithExpenses(Map<Integer, Boolean> daysWithExpenses) {
        this.daysWithExpenses = daysWithExpenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarDayBinding binding = ItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int day = days.get(position);
        boolean hasExpense = daysWithExpenses != null && daysWithExpenses.getOrDefault(day, false);
        holder.bind(day, hasExpense, day == selectedDay);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCalendarDayBinding binding;

        public ViewHolder(ItemCalendarDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int day, boolean hasExpense, boolean isSelected) {
            if (day == 0) {
                binding.tvDay.setText("");
                binding.viewIndicator.setVisibility(android.view.View.GONE);
                binding.getRoot().setEnabled(false);
            } else {
                binding.setDay(day);
                binding.setHasExpense(hasExpense);
                binding.setIsSelected(isSelected);
                binding.getRoot().setEnabled(true);
            }
            
            binding.getRoot().setOnClickListener(v -> {
                if (day == 0) return;
                int previousSelected = selectedDay;
                selectedDay = day;
                // Re-bind affected items
                notifyDataSetChanged(); // Simple para este grid pequeño
            });
            
            binding.executePendingBindings();
        }
    }
}
