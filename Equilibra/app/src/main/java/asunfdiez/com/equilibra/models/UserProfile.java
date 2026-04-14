package asunfdiez.com.equilibra.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;


@Entity(tableName = Constants.TABLE_USER_PROFILE)
public class UserProfile {

    @PrimaryKey
    private long id = 1; // Solo habrá un perfil, usamos un ID fijo.
    private String name;
    private String email;
    private String phone;
    private String passwordHash;

    private EmotionalState emotionalState;
    private String mainConcernId;
    private String mainConcern;
    private String timeAvailable;
    private long createdAt;
    private long updatedAt;

    private double monthlyIncome;

    private int financialKnowledge;
    private int budgetExperience;
    private int savingsHabit;

    private int dayCount = 1;
    private int weekProgress = 0;
    private int weekGoal = 7;

    private int weekCount = 0;

    private long lastResetTimestamp = System.currentTimeMillis();
    private long lastConnectionTimestamp = System.currentTimeMillis();


    public UserProfile() {
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

    public String getMainConcernId() {
        return mainConcernId;
    }

    public void setMainConcernId(String mainConcernId) {
        this.mainConcernId = mainConcernId;
    }

    public String getTimeAvailable() {
        return timeAvailable;
    }

    public void setTimeAvailable(String a_timeAvailable) {
        timeAvailable = a_timeAvailable;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long a_createdAt) {
        createdAt = a_createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long a_updatedAt) {
        updatedAt = a_updatedAt;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double a_monthlyIncome) {
        monthlyIncome = a_monthlyIncome;
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

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public int getWeekProgress() {
        return weekProgress;
    }

    public void setWeekProgress(int weekProgress) {
        this.weekProgress = weekProgress;
    }

    public int getWeekGoal() {
        return weekGoal;
    }

    public void setWeekGoal(int weekGoal) {
        this.weekGoal = weekGoal;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public long getLastResetTimestamp() {
        return lastResetTimestamp;
    }

    public void setLastResetTimestamp(long lastResetTimestamp) {
        this.lastResetTimestamp = lastResetTimestamp;
    }

    public long getLastConnectionTimestamp() {
        return lastConnectionTimestamp;
    }

    public void setLastConnectionTimestamp(long lastConnectionTimestamp) {
        this.lastConnectionTimestamp = lastConnectionTimestamp;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", emotionalState=" + emotionalState +
                ", mainConcernId='" + mainConcernId + '\'' +
                ", mainConcern='" + mainConcern + '\'' +
                ", timeAvailable='" + timeAvailable + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", monthlyIncome=" + monthlyIncome +
                ", financialKnowledge=" + financialKnowledge +
                ", budgetExperience=" + budgetExperience +
                ", savingsHabit=" + savingsHabit +
                ", dayCount=" + dayCount +
                ", weekProgress=" + weekProgress +
                ", weekGoal=" + weekGoal +
                '}';
    }
}
