package asunfdiez.com.equilibra.models;

public class UserProfile {

    private String name;
    private EmotionalState emotionalState;
    private String mainConcern;
    private String timeAvailable;
    private String savingGoal;
    private String savingAmount;
    private String savingDate;
    private final long createdAt;

    private String level;
    private String monthlyIncome;
    private String savingsPlan;

    private int financialKnowledge;
    private int budgetExperience;
    private int savingsHabit;

    public UserProfile() {
        this.createdAt = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String a_name) {
        name = a_name;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(EmotionalState a_emotion) {
        emotionalState = a_emotion;
    }

    public String getMainConcern() {
        return mainConcern;
    }

    public void setMainConcern(String a_mainConcern) {
        mainConcern = a_mainConcern;
    }

    public String getTimeAvailable() {
        return timeAvailable;
    }

    public void setTimeAvailable(String a_timeAvailable) {
        timeAvailable = a_timeAvailable;
    }

    public String getSavingGoal() {
        return savingGoal;
    }

    public void setSavingGoal(String a_savingGoal) {
        savingGoal = a_savingGoal;
    }

    public String getSavingAmount() {
        return savingAmount;
    }

    public void setSavingAmount(String a_savingAmount) {
        savingAmount = a_savingAmount;
    }

    public String getSavingDate() {
        return savingDate;
    }

    public void setSavingDate(String a_savingDate) {
        savingDate = a_savingDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String a_level) {
        level = a_level;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String a_monthlyIncome) {
        monthlyIncome = a_monthlyIncome;
    }

    public String getSavingsPlan() {
        return savingsPlan;
    }

    public void setSavingsPlan(String a_savingsPlan) {
        savingsPlan = a_savingsPlan;
    }

    public int getFinancialKnowledge() {
        return financialKnowledge;
    }

    public void setFinancialKnowledge(int a_financialKnowledge) {
        financialKnowledge = a_financialKnowledge;
    }

    public int getBudgetExperience() {
        return budgetExperience;
    }

    public void setBudgetExperience(int a_budgetExperience) {
        budgetExperience = a_budgetExperience;
    }

    public int getSavingsHabit() {
        return savingsHabit;
    }

    public void setSavingsHabit(int a_savingsHabit) {
        savingsHabit = a_savingsHabit;
    }
}
