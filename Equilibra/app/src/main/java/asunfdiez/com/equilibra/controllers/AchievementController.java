package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import java.util.List;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.repositories.AchievementRepository;

public class AchievementController {
    private final AchievementRepository achievementRepository;

    public AchievementController(Context context) {
        this.achievementRepository = new AchievementRepository(context);
    }

    public List<Achievement> getAllAchievements() {
        // AchievementRepository uses LiveData for allAchievements, 
        // but GamificationViewModel expects a synchronous list in a background thread.
        // We'll need to add a synchronous method to the repository or DAO if not present.
        // For now, let's assume we need to implement it.
        return achievementRepository.getAllAchievementsSync();
    }

    public int getTotalPoints() {
        return achievementRepository.getTotalPointsSync();
    }

    public String getUserLevel() {
        int points = getTotalPoints();
        if (points >= 1000) return "Maestro";
        if (points >= 500) return "Experto";
        if (points >= 200) return "Avanzado";
        if (points >= 50) return "Iniciado";
        return "Principiante";
    }

    public int getProgressToNextLevel() {
        int points = getTotalPoints();
        if (points >= 1000) return 100;
        if (points >= 500) return (points - 500) * 100 / 500;
        if (points >= 200) return (points - 200) * 100 / 300;
        if (points >= 50) return (points - 50) * 100 / 150;
        return points * 100 / 50;
    }

    public void unlockAchievement(String achievementKey) {
        achievementRepository.unlockAchievement(achievementKey);
    }

    public void checkAndUnlockAchievements() {
        achievementRepository.checkAndUnlockAchievements();
    }
}
