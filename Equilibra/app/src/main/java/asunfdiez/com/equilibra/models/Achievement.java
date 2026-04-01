package asunfdiez.com.equilibra.models;

/**
 * Achievement - Modelo de datos para logros gamificados
 * Representa un logro que el usuario puede desbloquear
 */
public class Achievement {

    private int id;
    private String title;
    private String description;
    private int points;
    private String key; // Identificador único del logro
    private boolean unlocked;
    private long unlockedAt; // Timestamp cuando se desbloqueó

    // Constructor vacío
    public Achievement() {
    }

    // Constructor completo
    public Achievement(int id, String title, String description, int points,
                       String key, boolean unlocked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.points = points;
        this.key = key;
        this.unlocked = unlocked;
        this.unlockedAt = unlocked ? System.currentTimeMillis() : 0;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
