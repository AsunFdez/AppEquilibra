package asunfdiez.com.equilibra.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Gestor centralizado para el acceso y modificación de las preferencias compartidas.
 * 
 * <p>Utiliza el patrón Singleton para asegurar una única instancia en toda la app. 
 * Además de persistir los datos, expone las preferencias como objetos {@link LiveData}, 
 * permitiendo que la interfaz de usuario reaccione automáticamente a cambios en 
 * la configuración (como el modo oscuro o el idioma).</p>
 */
public final class SharedPrefsManager {

    // Instancia única del gestor (Patrón Singleton)
    private static SharedPrefsManager instance;
    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.Editor editor;

    // Observables LiveData para sincronización con la UI en tiempo real
    private final MutableLiveData<Boolean> onboardingCompleted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> budgetNotifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> achievementNotifications = new MutableLiveData<>();
    private final MutableLiveData<String> viewMode = new MutableLiveData<>();
    private final MutableLiveData<String> theme = new MutableLiveData<>();
    private final MutableLiveData<String> language = new MutableLiveData<>();
    private final MutableLiveData<Boolean> darkMode = new MutableLiveData<>();
    private final MutableLiveData<Long> lastSyncTime = new MutableLiveData<>();

    private SharedPrefsManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        loadInitialValues();
    }

    /**
     * Carga los valores persistidos en los objetos LiveData durante la inicialización.
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

        viewMode.postValue(sharedPreferences.getString(Constants.PREF_VIEW, "beginner"));
        theme.postValue(sharedPreferences.getString(Constants.PREF_THEME, "dark"));
        language.postValue(sharedPreferences.getString(Constants.PREF_LANGUAGE, "es"));
        darkMode.postValue(sharedPreferences.getBoolean(Constants.PREF_DARK_MODE, false));
        lastSyncTime.postValue(sharedPreferences.getLong(Constants.PREF_LAST_SYNC_TIME, 0L));
    }

    /**
     * Obtiene la instancia única del SharedPrefsManager
     */
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
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
        editor.putBoolean(Constants.KEY_ONBOARDING_COMPLETE, complete).apply();
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
        editor.putLong(Constants.KEY_INTERVENTION_LAST_SEEN, now).apply();
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
        editor.putString(Constants.KEY_CURRENCY_SYMBOL, symbol).apply();
    }

    public String getCurrencySymbol() {
        return sharedPreferences.getString(Constants.KEY_CURRENCY_SYMBOL, "€");
    }

    //--- GAMIFICACIÓN
    public void setTotalXP(int xp) {
        editor.putInt(Constants.KEY_TOTAL_XP, xp).apply();
    }

    public int getTotalXP() {
        return sharedPreferences.getInt(Constants.KEY_TOTAL_XP, 0);
    }

    public void addXP(int xp) {
        setTotalXP(getTotalXP() + xp);
    }

    public void setCurrentMission(String mission) {
        editor.putString(Constants.KEY_CURRENT_MISSION, mission).apply();
    }

    public String getCurrentMission() {
        return sharedPreferences.getString(Constants.KEY_CURRENT_MISSION, "");
    }

    public void setMissionsCompleted(int count) {
        editor.putInt(Constants.KEY_MISSIONS_COMPLETED, count).apply();
    }

    public int getMissionsCompleted() {
        return sharedPreferences.getInt(Constants.KEY_MISSIONS_COMPLETED, 0);
    }

    public void incrementMissionsCompleted() {
        setMissionsCompleted(getMissionsCompleted() + 1);
    }

    //--- UTILIDADES

    /**
     * Limpia solo los datos de onboarding
     */
    public void resetOnboarding() {
        editor.remove(Constants.KEY_ONBOARDING_COMPLETE)
              .remove(Constants.KEY_ONBOARDING_COMPLETED_AT)
              .apply();
        onboardingCompleted.setValue(false);
    }

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

    public LiveData<String> getViewMode() {
        return viewMode;
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

    public void setViewMode(String viewModeValue) {
        editor.putString(Constants.PREF_VIEW, viewModeValue).apply();
        viewMode.setValue(viewModeValue);
    }

    public void setTheme(String themeValue) {
        editor.putString(Constants.PREF_THEME, themeValue).apply();
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

    public LiveData<Long> getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long timestamp) {
        editor.putLong(Constants.PREF_LAST_SYNC_TIME, timestamp).apply();
        lastSyncTime.postValue(timestamp);
    }

    /**
     * Limpia todas las preferencias (excepto onboarding)
     */
    public void clearAll() {
        boolean onboarding = isOnboardingComplete();
        editor.clear().apply();
        setOnboardingComplete(onboarding);
        loadInitialValues();
    }

    /**
     * Reset completo (incluye onboarding)
     */
    public void resetAll() {
        editor.clear().commit();
        loadInitialValues();
    }

    /**
     * Exporta todas las preferencias como un String
     */
    public String exportPreferences() {
        return sharedPreferences.getAll().toString();
    }
}
