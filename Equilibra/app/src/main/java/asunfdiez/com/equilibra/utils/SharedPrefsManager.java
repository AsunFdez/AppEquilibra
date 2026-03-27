package asunfdiez.com.equilibra.utils;

import android.content.Context;
import android.content.SharedPreferences;
     /**
     * Gestor centralizado de SharedPreferences
     * con patrón Singleton
     */
    public final class SharedPrefsManager {

        private static SharedPrefsManager instance;
        private final SharedPreferences sharedPreferences;

        private SharedPrefsManager(Context context) {
            this.sharedPreferences = context.getApplicationContext()
                    .getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
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


        //---- ONBOARDING
        public void setOnboardingComplete(boolean complete) {
            sharedPreferences.edit()
                    .putBoolean(Constants.KEY_ONBOARDING_COMPLETE, complete)
                    .apply();
        }

        public boolean isOnboardingComplete() {
            return sharedPreferences.getBoolean(Constants.KEY_ONBOARDING_COMPLETE, false);
        }

        public void setOnboardingCompletedAt(long timestamp) {
            sharedPreferences.edit()
                    .putLong(Constants.KEY_ONBOARDING_COMPLETED_AT, timestamp)
                    .apply();
        }

        public long getOnboardingCompletedAt() {
            return sharedPreferences.getLong(Constants.KEY_ONBOARDING_COMPLETED_AT, 0L);
        }


        //--- PERFIL DE USUARIO

        public void setUserName(String name) {
            sharedPreferences.edit()
                    .putString(Constants.KEY_USER_NAME, name)
                    .apply();
        }

        public String getUserName() {
            return sharedPreferences.getString(Constants.KEY_USER_NAME, "Usuario");
        }

        public void setUserLevel(String level) {
            sharedPreferences.edit()
                    .putString(Constants.KEY_USER_LEVEL, level)
                    .apply();
        }

        public String getUserLevel() {
            return sharedPreferences.getString(Constants.KEY_USER_LEVEL, Constants.USER_LEVEL_BEGINNER);
        }

        public void setMonthlyIncome(String income) {
            sharedPreferences.edit()
                    .putString(Constants.KEY_MONTHLY_INCOME, income)
                    .apply();
        }

        public String getMonthlyIncome() {
            return sharedPreferences.getString(Constants.KEY_MONTHLY_INCOME, "0");
        }

        public void setSavingsGoal(String goal) {
            sharedPreferences.edit()
                    .putString(Constants.KEY_SAVINGS_GOAL, goal)
                    .apply();
        }

        public String getSavingsGoal() {
            return sharedPreferences.getString(Constants.KEY_SAVINGS_GOAL, "");
        }

        public void setSavingsPlan(String plan) {
            sharedPreferences.edit()
                    .putString(Constants.KEY_SAVINGS_PLAN, plan)
                    .apply();
        }

        public String getSavingsPlan() {
            return sharedPreferences.getString(Constants.KEY_SAVINGS_PLAN, "");
        }


        //---- CUESTIONARIO PSICOLÓGICO
        public void setFinancialKnowledge(int level) {
            sharedPreferences.edit()
                    .putInt(Constants.KEY_FINANCIAL_KNOWLEDGE, level)
                    .apply();
        }

        public int getFinancialKnowledge() {
            return sharedPreferences.getInt(Constants.KEY_FINANCIAL_KNOWLEDGE, 0);
        }

        public void setBudgetExperience(int level) {
            sharedPreferences.edit()
                    .putInt(Constants.KEY_BUDGET_EXPERIENCE, level)
                    .apply();
        }

        public int getBudgetExperience() {
            return sharedPreferences.getInt(Constants.KEY_BUDGET_EXPERIENCE, 0);
        }

        public void setSavingsHabit(int level) {
            sharedPreferences.edit()
                    .putInt(Constants.KEY_SAVINGS_HABIT, level)
                    .apply();
        }

        public int getSavingsHabit() {
            return sharedPreferences.getInt(Constants.KEY_SAVINGS_HABIT, 0);
        }

        /**
         * Calcula el nivel del usuario basado en sus respuestas
         *
         * @return USER_LEVEL_BEGINNER, USER_LEVEL_INTERMEDIATE o USER_LEVEL_ADVANCED
         */
        public String calculateUserLevel() {
            int totalScore = getFinancialKnowledge() + getBudgetExperience() + getSavingsHabit();

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


