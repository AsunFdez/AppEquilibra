package asunfdiez.com.equilibra.models;

import java.util.List;

/**
 * Data Transfer Object (DTO) para la exportación e importación de la base de datos completa
 * utilizando Gson.
 */
public class DatabaseBackupDto {
    private List<UserProfile> userProfiles;
    private List<SavingGoal> savingGoals;
    private List<Expense> expenses;
    private List<ExpenseCategory> expenseCategories;
    private List<Achievement> achievements;
    private List<Alternative> alternatives;
    private List<Streak> streaks;
    private List<UserXP> userXPs;
    private List<Budget> budgets;
    private List<InterventionLog> interventionLogs;
    private List<XPAuditLog> xpAuditLogs;

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }

    public List<SavingGoal> getSavingGoals() {
        return savingGoals;
    }

    public void setSavingGoals(List<SavingGoal> savingGoals) {
        this.savingGoals = savingGoals;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<ExpenseCategory> getExpenseCategories() {
        return expenseCategories;
    }

    public void setExpenseCategories(List<ExpenseCategory> expenseCategories) {
        this.expenseCategories = expenseCategories;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public List<Streak> getStreaks() {
        return streaks;
    }

    public void setStreaks(List<Streak> streaks) {
        this.streaks = streaks;
    }

    public List<UserXP> getUserXPs() {
        return userXPs;
    }

    public void setUserXPs(List<UserXP> userXPs) {
        this.userXPs = userXPs;
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public List<InterventionLog> getInterventionLogs() {
        return interventionLogs;
    }

    public void setInterventionLogs(List<InterventionLog> interventionLogs) {
        this.interventionLogs = interventionLogs;
    }

    public List<XPAuditLog> getXpAuditLogs() {
        return xpAuditLogs;
    }

    public void setXpAuditLogs(List<XPAuditLog> xpAuditLogs) {
        this.xpAuditLogs = xpAuditLogs;
    }
}
