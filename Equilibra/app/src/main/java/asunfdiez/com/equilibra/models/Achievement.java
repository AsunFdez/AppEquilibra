package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Achievement - Modelo de datos para logros gamificados
 * Representa un logro que el usuario puede desbloquear
 */
@Entity(tableName = Constants.TABLE_ACHIEVEMENTS)
public class Achievement {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    private long id;
    private String title;
    private String description;
    private int points;
    private String key; // Identificador único del logro
    private boolean unlocked;
    private long unlockedAt;// Timestamp cuando se desbloqueó

    private int categoryColor; // color del icono
    private int iconRes; // recurso del icono



    // Constructor completo
    public Achievement(String title, String description, int points,
                       String key, boolean unlocked, int categoryColor, int iconRes) {
        this.title = title;
        this.description = description;
        this.points = points;
        this.key = key;
        this.unlocked = unlocked;
        this.unlockedAt = unlocked ? System.currentTimeMillis() : 0;
        this.categoryColor = categoryColor;
        this.iconRes = iconRes;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        if (unlocked && unlockedAt == 0) {
            this.unlockedAt = System.currentTimeMillis();
        }
    }

    public long getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(long unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", points=" + points +
                ", key='" + key + '\'' +
                ", unlocked=" + unlocked +
                ", unlockedAt=" + unlockedAt +
                '}';
    }
}
