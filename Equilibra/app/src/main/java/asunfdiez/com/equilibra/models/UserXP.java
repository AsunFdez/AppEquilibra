package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que gestiona el progreso de gamificación del usuario.
 * 
 * <p>Registra el total de puntos de experiencia (XP) acumulados, el nivel actual 
 * alcanzado y los requisitos para el siguiente nivel. Es el núcleo que permite 
 * visualizar el avance del usuario en el sistema de recompensas.</p>
 */
@Entity(
        tableName = Constants.TABLE_USER_XP,
        foreignKeys = @ForeignKey(
                entity = UserProfile.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class UserXP {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    
    // ID del usuario asociado a estos puntos
    private long userId;
    
    // Total acumulado de puntos de experiencia
    private int totalXP;
    
    // Nivel actual calculado del usuario
    private int currentLevel;
    
    // Puntos necesarios para alcanzar el nivel superior
    private int xpForNextLevel;
    
    // Conteo total de logros conseguidos
    private int achievementsUnlocked;
    
    // Marca de tiempo de la última vez que se otorgaron puntos
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
