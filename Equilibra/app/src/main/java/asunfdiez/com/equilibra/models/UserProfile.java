package asunfdiez.com.equilibra.models;
import asunfdiez.com.equilibra.models.EmotionalState;


public class UserProfile {

    private EmotionalState emotion;
    private String mainConcern;
    private String timeAvailable;
    private String savingGoal;
    private String savingAmount;
    private String savingDate;
    private final long completedAt;

    public UserProfile() {
        this.completedAt = System.currentTimeMillis();
    }

    // Getters y Setters
    public EmotionalState getEmotion() {
        return emotion;
    }

    public void setEmotion(EmotionalState emotion) {
        this.emotion = emotion;
    }

    public String getMainConcern() {
        return mainConcern;
    }

    public void setMainConcern(String mainConcern) {
        this.mainConcern = mainConcern;
    }

    public String getTimeAvailable() {
        return timeAvailable;
    }

    public void setTimeAvailable(String timeAvailable) {
        this.timeAvailable = timeAvailable;
    }

    public String getSavingGoal() {
        return savingGoal;
    }

    public void setSavingGoal(String savingGoal) {
        this.savingGoal = savingGoal;
    }

    public String getSavingAmount() {
        return savingAmount;
    }

    public void setSavingAmount(String savingAmount) {
        this.savingAmount = savingAmount;
    }

    public String getSavingDate() {
        return savingDate;
    }

    public void setSavingDate(String savingDate) {
        this.savingDate = savingDate;
    }

    public long getCompletedAt() {
        return completedAt;
    }
}
