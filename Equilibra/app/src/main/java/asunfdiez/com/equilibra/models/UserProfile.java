package asunfdiez.com.equilibra.models;

public class UserProfile {

    private long id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;

    private EmotionalState emotionalState;
    private String mainConcern;
    private String timeAvailable;
    private String savingGoal;
    private String savingAmount;
    private String savingDate;
    private final long createdAt;
    private long updatedAt;

    private String level;
    private double monthlyIncome;
    private String savingsPlan;

    private int financialKnowledge;
    private int budgetExperience;
    private int savingsHabit;

    public UserProfile() {
        this.id = -1;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long a_id) {
        id = a_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String a_name) {
        name = a_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String a_email) {
        email = a_email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String a_phone) {
        phone = a_phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String a_passwordHash) {
        passwordHash = a_passwordHash;
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

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long a_updatedAt) {
        updatedAt = a_updatedAt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String a_level) {
        level = a_level;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double a_monthlyIncome) {
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
