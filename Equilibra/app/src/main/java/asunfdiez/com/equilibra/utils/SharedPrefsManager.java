package asunfdiez.com.equilibra.utils;

import android.content.Context;
import android.content.SharedPreferences;
import asunfdiez.com.equilibra.models.UserProfile;
import com.google.gson.Gson;

/**
 * Gestor centralizado de SharedPreferences
 * con patrón Singleton
 */
public final class SharedPrefsManager {

    private static SharedPrefsManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private long currentUserId;

    private SharedPrefsManager(Context context) {
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Obtiene la instancia única del SharedPrefsManager
     */
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context);
        }
        return instance;
    }

    public long getCurrentUserId() {
        return currentUserId;
    }
    public void setCurrentUserId(long userId) {
        currentUserId = userId;
    }


    // ==================== ONBOARDING ====================
    /**
     * Verifica si el onboarding está completo
     */
    public boolean isOnboardingComplete() {
        return sharedPreferences.getBoolean(Constants.KEY_ONBOARDING_COMPLETE, false);
    }

    public long getOnboardingCompletedAt() {
        return sharedPreferences.getLong(Constants.KEY_ONBOARDING_COMPLETED_AT, 0L);
    }

    /**
     * Marca el onboarding como completo
     */
    public void setOnboardingComplete(boolean complete) {
        sharedPreferences.edit()
                .putBoolean(Constants.KEY_ONBOARDING_COMPLETE, complete)
                .apply();
    }

    public void setOnboardingCompletedAt(long timestamp) {
        sharedPreferences.edit()
                .putLong(Constants.KEY_ONBOARDING_COMPLETED_AT, timestamp)
                .apply();
    }

    // ==================== PERFIL DE USUARIO ====================

    /**
     * Guarda el perfil del usuario
     */
    public void saveUserProfile(UserProfile profile) {
        String json = gson.toJson(profile);
        sharedPreferences.edit().putString(Constants.KEY_USER_PROFILE, json).apply();
        System.out.println("Perfil de usuario guardado: " + json);
    }

    /**
     * Obtiene el perfil del usuario
     */



    public UserProfile getUserProfile() {
        String json = sharedPreferences.getString(Constants.KEY_USER_PROFILE, null);
        if (json != null) {
            return gson.fromJson(json, UserProfile.class);
        }
        return new UserProfile(); // Perfil vacío si no existe
    }

    public long gettUserId() {
        UserProfile profile = getUserProfile();
        return profile.getId();
    }

    public void setUserName(String name) {
        UserProfile profile = getUserProfile();
        profile.setName(name);
        saveUserProfile(profile);
    }

    public String getUserName() {
        UserProfile profile = getUserProfile();
        return profile.getName();
    }

    public void setUserEmail(String email) {
        UserProfile profile = getUserProfile();
        profile.setEmail(email);
        saveUserProfile(profile);
    }

    public String getUserEmail() {
        UserProfile profile = getUserProfile();
        return profile.getEmail();
    }

    public void setIdNameEmailIncomeUserData(long id, String name, String email, double income) {
        UserProfile profile = getUserProfile();
        profile.setId(id);
        profile.setName(name);
        profile.setEmail(email);
        profile.setMonthlyIncome(income);
        saveUserProfile(profile);
    }

    public void setUserLevel(String level) {
        UserProfile profile = getUserProfile();
        profile.setLevel(level);
        saveUserProfile(profile);
    }

    public String getUserLevel() {
        UserProfile profile = getUserProfile();
        return profile.getLevel();
    }

    public void setMonthlyIncome(double income) {
        UserProfile profile = getUserProfile();
        profile.setMonthlyIncome(income);
        saveUserProfile(profile);
    }

    public double getMonthlyIncome() {
        UserProfile profile = getUserProfile();
        return profile.getMonthlyIncome();
    }

    /**
     * Actualiza la meta de ahorro
     */
    public void updateSavingGoal(String goal, double amount, String date) {
        UserProfile profile = getUserProfile();
        profile.setSavingGoal(goal);
        profile.setSavingAmount(String.valueOf(amount));
        profile.setSavingDate(date);
        saveUserProfile(profile);
    }

    public String getSavingsGoal() {
        UserProfile profile = getUserProfile();
        return profile.getSavingGoal();
    }

    public void setBudgetExperience(int level) {
        UserProfile profile = getUserProfile();
        profile.setBudgetExperience(level);
        saveUserProfile(profile);
    }

    public int getBudgetExperience() {
        UserProfile profile = getUserProfile();
        return profile.getBudgetExperience();
    }

    public void setSavingsHabit(int level) {
        UserProfile profile = getUserProfile();
        profile.setSavingsHabit(level);
        saveUserProfile(profile);
    }

    public int getSavingsHabit() {
        UserProfile profile = getUserProfile();
        return profile.getSavingsHabit();
    }

    // ==================== INTERVENCIONES ====================

    /**
     * Verifica si la intervención ya fue vista hoy
     */
    public boolean wasInterventionSeenToday() {
        long lastSeen = sharedPreferences.getLong(Constants.KEY_INTERVENTION_LAST_SEEN, 0);
        long today = getTodayTimestamp();
        return lastSeen >= today;
    }

    /**
     * Marca la intervención como vista hoy
     */
    public void markInterventionSeenToday() {
        long now = System.currentTimeMillis();
        sharedPreferences.edit().putLong(Constants.KEY_INTERVENTION_LAST_SEEN, now).apply();
    }

    /**
     * Obtiene el timestamp del inicio del día actual
     */
    private long getTodayTimestamp() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // ==================== LOGROS ====================

    /**
     * Verifica si un logro está desbloqueado
     */
    public boolean isAchievementUnlocked(String achievementKey) {
        return sharedPreferences.getBoolean(Constants.KEY_ACHIEVEMENTS + achievementKey, false);
    }

    /**
     * Desbloquea un logro
     */
    public void unlockAchievement(String achievementKey) {
        sharedPreferences.edit().putBoolean(Constants.KEY_ACHIEVEMENTS + achievementKey, true).apply();
    }

    /**
     * Resetea todos los logros (para testing)
     */
    public void resetAchievements() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith(Constants.KEY_ACHIEVEMENTS)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }

    /**
     * Calcula el nivel del usuario basado en sus respuestas
     *
     * @return USER_LEVEL_BEGINNER, USER_LEVEL_INTERMEDIATE o USER_LEVEL_ADVANCED
     */
    public String calculateUserLevel() {
        UserProfile profile = getUserProfile();
        int totalScore = profile.getFinancialKnowledge() + profile.getBudgetExperience() + profile.getSavingsHabit();

        if (totalScore <= 7) {
            return Constants.USER_LEVEL_BEGINNER;
        } else if (totalScore <= 11) {
            return Constants.USER_LEVEL_INTERMEDIATE;
        } else {
            return Constants.USER_LEVEL_ADVANCED;
        }
    }

    //--- CONFIGURACIÓN
    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, enabled)
                .apply();
    }

    public boolean areNotificationsEnabled() {
        return sharedPreferences.getBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setCurrencySymbol(String symbol) {
        sharedPreferences.edit()
                .putString(Constants.KEY_CURRENCY_SYMBOL, symbol)
                .apply();
    }

    public String getCurrencySymbol() {
        return sharedPreferences.getString(Constants.KEY_CURRENCY_SYMBOL, "$");
    }

    public void setThemeMode(String mode) {
        sharedPreferences.edit()
                .putString(Constants.KEY_THEME_MODE, mode)
                .apply();
    }

    public String getThemeMode() {
        return sharedPreferences.getString(Constants.KEY_THEME_MODE, "light");
    }

    public void setLanguage(String language) {
        sharedPreferences.edit()
                .putString(Constants.KEY_LANGUAGE, language)
                .apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(Constants.KEY_LANGUAGE, "es");
    }

    //--- GAMIFICACIÓN
    public void setTotalXP(int xp) {
        sharedPreferences.edit()
                .putInt(Constants.KEY_TOTAL_XP, xp)
                .apply();
    }

    public int getTotalXP() {
        return sharedPreferences.getInt(Constants.KEY_TOTAL_XP, 0);
    }

    public void addXP(int xp) {
        int currentXP = getTotalXP();
        setTotalXP(currentXP + xp);
    }

    public void setCurrentMission(String mission) {
        sharedPreferences.edit()
                .putString(Constants.KEY_CURRENT_MISSION, mission)
                .apply();
    }

    public String getCurrentMission() {
        return sharedPreferences.getString(Constants.KEY_CURRENT_MISSION, "");
    }

    public void setMissionsCompleted(int count) {
        sharedPreferences.edit()
                .putInt(Constants.KEY_MISSIONS_COMPLETED, count)
                .apply();
    }

    public int getMissionsCompleted() {
        return sharedPreferences.getInt(Constants.KEY_MISSIONS_COMPLETED, 0);
    }

    public void incrementMissionsCompleted() {
        int current = getMissionsCompleted();
        setMissionsCompleted(current + 1);
    }

    //--- UTILIDADES

    /**
     * Limpia todos los datos de SharedPreferences
     * Útil para testing o reset de la aplicación
     */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Limpia solo los datos de onboarding
     * Permite al usuario volver a hacer el onboarding
     */
    public void resetOnboarding() {
        sharedPreferences.edit()
                .remove(Constants.KEY_ONBOARDING_COMPLETE)
                .remove(Constants.KEY_ONBOARDING_COMPLETED_AT)
                .apply();
    }

    /**
     * Obtiene el SharedPreferences
     */
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
