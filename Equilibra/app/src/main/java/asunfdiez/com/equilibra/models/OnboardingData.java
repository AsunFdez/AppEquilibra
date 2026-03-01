package asunfdiez.com.equilibra.models;

public class OnboardingData {

    private String emotion;
    private String mainConcern;
    private String timeAvailable;
    private String savingGoal;
    private String savingAmount;
    private String savingDate;
    private long completedAt;

    public OnboardingData() {
        this.completedAt = System.currentTimeMillis();
    }

    // Getters y Setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
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
