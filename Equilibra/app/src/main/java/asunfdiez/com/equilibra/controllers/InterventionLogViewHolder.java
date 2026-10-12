package asunfdiez.com.equilibra.controllers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import asunfdiez.com.equilibra.databinding.ItemInterventionLogBinding;
import asunfdiez.com.equilibra.models.InterventionLog;

public class InterventionLogViewHolder extends RecyclerView.ViewHolder {
    private final ItemInterventionLogBinding binding;

    public InterventionLogViewHolder(@NonNull ItemInterventionLogBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull InterventionLog log, asunfdiez.com.equilibra.adapters.InterventionLogAdapter.OnLogClickListener listener) {
        binding.setInterventionLog(log);

        binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onInterventionLogClick(log);
        });

        binding.imgDelete.setOnClickListener(v -> {
            if (listener != null) listener.onLogDelete(log);
        });

        binding.executePendingBindings();
    }
}
