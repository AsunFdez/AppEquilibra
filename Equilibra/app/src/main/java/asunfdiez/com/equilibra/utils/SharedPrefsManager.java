package asunfdiez.com.equilibra.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Gestor centralizado de SharedPreferences
 * con patrón Singleton
 */
public final class SharedPrefsManager {

    private static SharedPrefsManager instance;
    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.Editor editor;

    private final MutableLiveData<Boolean> onboardingCompleted = new MutableLiveData<>();

    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> budgetNotifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> achievementNotifications = new MutableLiveData<>();
    private final MutableLiveData<String> theme = new MutableLiveData<>();
    private final MutableLiveData<String> language = new MutableLiveData<>();
    private final MutableLiveData<Boolean> darkMode = new MutableLiveData<>();

    private SharedPrefsManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        loadInitialValues();
    }

    /**
     * Carga los valores iniciales en LiveData
     */
    private void loadInitialValues() {
        onboardingCompleted.postValue(sharedPreferences.getBoolean(
                Constants.KEY_ONBOARDING_COMPLETE, false));
        notificationsEnabled.postValue(sharedPreferences.getBoolean(
                Constants.PREF_NOTIFICATIONS_ENABLED, true));
        budgetNotifications.postValue(sharedPreferences.getBoolean(
                Constants.PREF_BUDGET_NOTIFICATIONS, true));
        achievementNotifications.postValue(sharedPreferences.getBoolean(
                Constants.PREF_ACHIEVEMENT_NOTIFICATIONS, true));
        theme.postValue(sharedPreferences.getString(Constants.PREF_VIEW, "beginner"));
        language.postValue(sharedPreferences.getString(Constants.PREF_LANGUAGE, "es"));
        darkMode.postValue(sharedPreferences.getBoolean(Constants.PREF_DARK_MODE, false));
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



    // ==================== ONBOARDING ====================
    /**
     * Verifica si el onboarding está completo
     */
    public boolean isOnboardingComplete() {
        if (!sharedPreferences.contains(Constants.KEY_ONBOARDING_COMPLETE)) {
            return false;
        }
        return sharedPreferences.getBoolean(Constants.KEY_ONBOARDING_COMPLETE, false);

    }



    public long getOnboardingCompletedAt() {
        if (!sharedPreferences.contains(Constants.KEY_ONBOARDING_COMPLETED_AT)) {
            return 0L;
        }
        return sharedPreferences.getLong(Constants.KEY_ONBOARDING_COMPLETED_AT, 0L);
    }

    /**
     * Marca el onboarding como completo
     */
    public void setOnboardingComplete(boolean complete) {
        editor.putBoolean(Constants.KEY_ONBOARDING_COMPLETE, complete).apply();
        // Sincronizamos el LiveData para que la UI reaccione
        onboardingCompleted.setValue(complete);
    }

    public void setOnboardingCompletedAt(long timestamp) {
        editor.putLong(Constants.KEY_ONBOARDING_COMPLETED_AT, timestamp).apply();
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


    //--- CONFIGURACIÓN

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


    public LiveData<Boolean> getOnboardingCompleted() {
        return onboardingCompleted;
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public LiveData<Boolean> getBudgetNotifications() {
        return budgetNotifications;
    }

    public LiveData<Boolean> getAchievementNotifications() {
        return achievementNotifications;
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public LiveData<String> getLanguage() {
        return language;
    }

    public LiveData<Boolean> getDarkMode() {
        return darkMode;
    }

    public void setNotificationsEnabled(boolean enabled) {
        editor.putBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, enabled).apply();
        notificationsEnabled.setValue(enabled);
    }

    public void setBudgetNotifications(boolean enabled) {
        editor.putBoolean(Constants.PREF_BUDGET_NOTIFICATIONS, enabled).apply();
        budgetNotifications.setValue(enabled);
    }

    public void setAchievementNotifications(boolean enabled) {
        editor.putBoolean(Constants.PREF_ACHIEVEMENT_NOTIFICATIONS, enabled).apply();
        achievementNotifications.setValue(enabled);
    }

    public void setTheme(String themeValue) {
        editor.putString(Constants.PREF_VIEW, themeValue).apply();
        theme.setValue(themeValue);
    }

    public void setLanguage(String lang) {
        editor.putString(Constants.PREF_LANGUAGE, lang).apply();
        language.setValue(lang);
    }

    public void setDarkMode(boolean enabled) {
        editor.putBoolean(Constants.PREF_DARK_MODE, enabled).apply();
        darkMode.setValue(enabled);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Limpia todas las preferencias (excepto onboarding)
     */
    public void clearAll() {
        boolean onboarding = sharedPreferences.getBoolean(
                Constants.KEY_ONBOARDING_COMPLETE, false);

        editor.clear().apply();

        // Restaurar onboarding
        editor.putBoolean(Constants.KEY_ONBOARDING_COMPLETE, onboarding).apply();

        // Recargar valores
        loadInitialValues();
    }

    /**
     * Reset completo (incluye onboarding)
     */
    public void resetAll() {
        sharedPreferences.edit().clear().commit(); // commit() para que sea inmediato en este caso
        loadInitialValues();
    }

    /**
     * Exporta todas las preferencias como String
     */
    public String exportPreferences() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notifications: ").append(notificationsEnabled.getValue()).append("\n");
        sb.append("Theme: ").append(theme.getValue()).append("\n");
        sb.append("Language: ").append(language.getValue()).append("\n");
        return sb.toString();
    }


}
