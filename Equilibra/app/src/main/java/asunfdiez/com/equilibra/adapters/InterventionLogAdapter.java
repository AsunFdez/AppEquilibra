package asunfdiez.com.equilibra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.controllers.InterventionLogViewHolder;
import asunfdiez.com.equilibra.databinding.ItemInterventionLogBinding;
import asunfdiez.com.equilibra.models.InterventionLog;

public class InterventionLogAdapter extends RecyclerView.Adapter<InterventionLogViewHolder> {
    private List<InterventionLog> interventionLogs = new ArrayList<>();
    private final Context context;
    private final InterventionLogAdapter.OnLogClickListener listener;

    public InterventionLogAdapter(Context context, InterventionLogAdapter.OnLogClickListener listener) {
        this.context = context;
        this.interventionLogs = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public InterventionLogViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        ItemInterventionLogBinding binding = ItemInterventionLogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new InterventionLogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull InterventionLogViewHolder holder, int position) {
        holder.bind(interventionLogs.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return interventionLogs.size();
    }

    public void setInterventionLogs(List<InterventionLog> newLogs) {
        interventionLogs.clear();
        if (newLogs != null) {
            interventionLogs.addAll(newLogs);
        }
        notifyDataSetChanged();
    }

    /**
     * Interfaz para la gestión de eventos de usuario sobre los logs.
     */
    public interface OnLogClickListener {
        void onInterventionLogClick(InterventionLog log);
        void onLogDelete(InterventionLog log);
    }
}
