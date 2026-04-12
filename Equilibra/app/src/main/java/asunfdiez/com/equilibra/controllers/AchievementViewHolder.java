package asunfdiez.com.equilibra.controllers;

import androidx.recyclerview.widget.RecyclerView;

import asunfdiez.com.equilibra.databinding.ItemAchievementBinding;
import asunfdiez.com.equilibra.models.Achievement;

/**
 * ViewHolder para Achievement
 */
public class AchievementViewHolder extends RecyclerView.ViewHolder {
    private final ItemAchievementBinding binding;

    public AchievementViewHolder(ItemAchievementBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind( Achievement achievement) {
        binding.setAchievement(achievement);
        binding.executePendingBindings();
    }
}