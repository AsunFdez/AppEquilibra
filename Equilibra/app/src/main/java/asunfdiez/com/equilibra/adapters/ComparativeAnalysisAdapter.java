package asunfdiez.com.equilibra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.databinding.ItemComparativeAnalysisBinding;
import asunfdiez.com.equilibra.models.ComparativeAnalysisState;

public class ComparativeAnalysisAdapter extends RecyclerView.Adapter<ComparativeAnalysisAdapter.ViewHolder> {

    private List<ComparativeAnalysisState> items = new ArrayList<>();

    public void setItems(List<ComparativeAnalysisState> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemComparativeAnalysisBinding binding = ItemComparativeAnalysisBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemComparativeAnalysisBinding binding;

        ViewHolder(ItemComparativeAnalysisBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ComparativeAnalysisState state) {
            binding.setState(state);
            binding.executePendingBindings();
        }
    }
}