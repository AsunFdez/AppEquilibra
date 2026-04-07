package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * UserXP - Modelo de datos para experiencia y progreso gamificado
 * Almacena puntos de experiencia, nivel actual y logros desbloqueados
 */
@Entity(tableName = Constants.TABLE_USER_XP)
public class UserXP {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private long userId;
    private int totalXP;
    private int currentLevel;
    private int xpForNextLevel;
    private int achievementsUnlocked;
    private long lastXPEarnedAt;

    // Constructor vacío
    public UserXP() {
    }


    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getXpForNextLevel() {
        return xpForNextLevel;
    }

    public void setXpForNextLevel(int xpForNextLevel) {
        this.xpForNextLevel = xpForNextLevel;
    }

    public int getAchievementsUnlocked() {
        return achievementsUnlocked;
    }

    public void setAchievementsUnlocked(int achievementsUnlocked) {
        this.achievementsUnlocked = achievementsUnlocked;
    }

    public long getLastXPEarnedAt() {
        return lastXPEarnedAt;
    }

    public void setLastXPEarnedAt(long lastXPEarnedAt) {
        this.lastXPEarnedAt = lastXPEarnedAt;
    }

    /**
     * Añade XP al usuario y verifica si sube de nivel
     */
    public void addXP(int xp) {
        this.totalXP += xp;
        this.lastXPEarnedAt = System.currentTimeMillis();
        checkLevelUp();
    }

    /**
     * Verifica y otorga niveles basado en XP total
     */
    private void checkLevelUp() {
        // Fórmula: XP necesario = 100 * nivel
        int xpNeeded = 100 * currentLevel;
        if (totalXP >= xpNeeded) {
            currentLevel++;
            xpForNextLevel = 100 * currentLevel - totalXP;
        }
    }

    /**
     * Calcula el porcentaje de progreso para el siguiente nivel
     */
    public double getLevelProgress() {
        int xpForCurrentLevel = 100 * (currentLevel - 1);
        int xpRequiredForLevel = 100 * currentLevel;
        int xpInCurrentLevel = totalXP - xpForCurrentLevel;
        int xpNeededForLevel = xpRequiredForLevel - xpForCurrentLevel;

        if (xpNeededForLevel <= 0) return 0;
        return (xpInCurrentLevel / (double) xpNeededForLevel) * 100;
    }

    @Override
    public String toString() {
        return "UserXP{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalXP=" + totalXP +
                ", currentLevel=" + currentLevel +
                ", achievementsUnlocked=" + achievementsUnlocked +
                '}';
    }
}
