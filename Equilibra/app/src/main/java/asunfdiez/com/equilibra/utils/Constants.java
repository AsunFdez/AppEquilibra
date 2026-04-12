package asunfdiez.com.equilibra.utils;

public final class Constants {
    private Constants() {
        throw new AssertionError("No se pueden instanciar constantes");
    }


    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "dd/MM/yyyy";


    //---- SHARED PREFERENCES KEYS
    public static final String PREFS_NAME = "EquilibraPrefs";
    public static final String PREF_MONTHLY_BUDGET = "monthly_budget";
    public static final String PREF_WEEKLY_BUDGET = "weekly_budget";
    public static final String PREF_CURRENCY = "currency";
    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String PREF_BUDGET_NOTIFICATIONS = "budget_notifications";
    public static final String PREF_ACHIEVEMENT_NOTIFICATIONS = "achievement_notifications";
    public static final String PREF_VIEW = "beginner";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_DARK_MODE = "dark_mode";

    // Onboarding
    public static final String KEY_ONBOARDING_COMPLETE = "onboardingComplete";
    public static final String KEY_USER_LEVEL = "userLevel";
    public static final String USER_LEVEL_BEGINNER = "beginner";
    public static final String USER_LEVEL_INTERMEDIATE = "intermediate";
    public static final String USER_LEVEL_ADVANCED = "advanced";
    public static final String KEY_ONBOARDING_COMPLETED_AT = "onboardingCompletedAt";

    // FAB
    public static final long FAB_ANIM_DURATION = 100;

    // Perfil de usuario
    public static final String KEY_USER_PROFILE = "userProfile";

    // Intervenciones
    public static final String KEY_INTERVENTION_LAST_SEEN = "interventionLastSeen";

    // Logros
    public static final String KEY_ACHIEVEMENTS = "achievement_";

    // Configuración
    public static final String KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled";
    public static final String KEY_CURRENCY_SYMBOL = "currencySymbol";
    public static final String KEY_THEME_MODE = "themeMode";
    public static final String KEY_LANGUAGE = "language";

    // Gamificación
    public static final String KEY_TOTAL_XP = "totalXP";
    public static final String KEY_CURRENT_MISSION = "currentMission";
    public static final String KEY_MISSIONS_COMPLETED = "missionsCompleted";




    // Duraciones de animación
    public static final int DURATION_SHORT = 100;
    public static final int DURATION_MEDIUM = 300;
    public static final int DURATION_LONG = 600;
    public static final int DURATION_PROGRESS = 500;

    // Delays
    public static final int DELAY_SHORT = 100;
    public static final int DELAY_MEDIUM = 200;
    public static final int DELAY_LONG = 300;

    // Valores de escala
    public static final float SCALE_PRESSED = 0.95f;
    public static final float SCALE_NORMAL = 1.0f;

    // Valores de transición
    public static final float TRANSITION_DISTANCE = 100f;
    public static final float OPTION_TRANSLATION_X = -20f;

    // Índices de preguntas
    public static final int QUESTION_INDEX_FIRST = 0;
    public static final int QUESTION_INDEX_SECOND = 1;
    public static final int TOTAL_QUESTIONS = 2;

    // IDs de respuesta ahorrar para algo específico
    public static final String ANSWER_ID_SAVE_SPECIFIC = "save-specific";


    // Formato de
    public static final String CURRENCY_FORMAT = "%.2f";
    //public static final String CURRENCY_FORMAT = "%.2f€";

    // Factor de división para ahorro mensual
    public static final int MONTHLY_DIVISION_FACTOR = 6;


    // ========================================
    // DATABASE CONSTANTS
    // ========================================
    public static final String DATABASE_NAME = "equilibra.db";
    public static final int DATABASE_VERSION = 2;

    //Tabla UserProfile
    public static final String TABLE_USER_PROFILE = "user_profile";
    public static final String COLUMN_USER_WEEK_PROGRESS = "weekProgress";
    public static final String COLUMN_USER_DAY_COUNT = "dayCount";
    public static final String COLUMN_USER_MONTHLY_INCOME = "monthlyIncome";
    public static final String COLUMN_USER_NAME = "name";




    // Tabla Expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ID = "id";
    public static final String COLUMN_EXPENSE_TYPE = "type";
    // Tipos de transacción
    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_CATEGORY = "category";
    public static final String COLUMN_EXPENSE_DESCRIPTION = "description";
    public static final String COLUMN_EXPENSE_NOTES = "notes";
    public static final String COLUMN_EXPENSE_DATE = "date";
    public static final String COLUMN_EXPENSE_TIMESTAMP = "timestamp";

    public static final String COLUMN_EXPENSE_PATTERN_COUNT = "pattern_count";
    public static final String COLUMN_EXPENSE_IS_RECURRING = "is_recurring";


    // Tabla Budgets
    public static final String TABLE_BUDGETS = "budgets";
    public static final String COLUMN_BUDGET_ID = "id";
    public static final String COLUMN_BUDGET_CATEGORY = "category";
    public static final String COLUMN_BUDGET_AMOUNT = "amount";
    public static final String COLUMN_BUDGET_PERIOD = "period";
    public static final String COLUMN_BUDGET_MONTH = "month";
    public static final String COLUMN_BUDGET_YEAR = "year";

    // Tabla Achievements
    public static final String TABLE_ACHIEVEMENTS = "achievements";
    public static final String COLUMN_ACHIEVEMENT_ID = "id";
    public static final String COLUMN_ACHIEVEMENT_TITLE = "title";
    public static final String COLUMN_ACHIEVEMENT_DESCRIPTION = "description";
    public static final String COLUMN_ACHIEVEMENT_XP = "xp";
    public static final String COLUMN_ACHIEVEMENT_ICON = "icon";
    public static final String COLUMN_ACHIEVEMENT_UNLOCKED = "unlocked";
    public static final String COLUMN_ACHIEVEMENT_DATE_UNLOCKED = "date_unlocked";

    // Tabla Categories
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_ICON = "iconName";
    public static final String COLUMN_CATEGORY_COLOR = "color";

    // Tabla Saving Goals
    public static final String TABLE_SAVING_GOALS = "saving_goals";
    public static final String COLUMN_SAVING_GOAL_ID = "_id";
    public static final String COLUMN_SAVING_GOAL_USER_ID = "user_id";
    public static final String COLUMN_SAVING_GOAL_NAME = "name";
    public static final String COLUMN_SAVING_GOAL_TARGET_AMOUNT = "target_amount";
    public static final String COLUMN_SAVING_GOAL_CURRENT_AMOUNT = "current_amount";
    public static final String COLUMN_SAVING_GOAL_DEADLINE = "deadline";
    public static final String COLUMN_SAVING_GOAL_COMPLETED = "completed";
    public static final String COLUMN_SAVING_GOAL_CREATED_AT = "created_at";
    public static final String COLUMN_SAVING_GOAL_UPDATED_AT = "updated_at";

    // Tabla User XP
    public static final String TABLE_USER_XP = "user_xp";
    public static final String COLUMN_USER_XP_ID = "_id";
    public static final String COLUMN_USER_XP_USER_ID = "user_id";
    public static final String COLUMN_USER_XP_TOTAL_XP = "total_xp";
    public static final String COLUMN_USER_XP_CURRENT_LEVEL = "current_level";
    public static final String COLUMN_USER_XP_XP_FOR_NEXT_LEVEL = "xp_for_next_level";
    public static final String COLUMN_USER_XP_ACHIEVEMENTS_UNLOCKED = "achievements_unlocked";
    public static final String COLUMN_USER_XP_LAST_XP_EARNED_AT = "last_xp_earned_at";

    // Tabla Streak
    public static final String TABLE_STREAK = "streak";
    public static final String COLUMN_STREAK_ID = "id";
    public static final String COLUMN_STREAK_USER_ID = "userId";
    public static final String COLUMN_STREAK_CONSECUTIVE_DAYS = "consecutiveDays";
    public static final String COLUMN_STREAK_START_DATE = "startDate";
    public static final String COLUMN_STREAK_LAST_ACTIVITY_DATE = "lastActivityDate";
    public static final String COLUMN_STREAK_MAX_RECORD = "maxStreakRecord";
    public static final String COLUMN_STREAK_IS_ACTIVE = "isActive";

    // Tabla Challenges
    public static final String TABLE_CHALLENGES = "challenges";
    public static final String COLUMN_CHALLENGE_ID = "id";
    public static final String COLUMN_CHALLENGE_USER_ID = "userId";
    public static final String COLUMN_CHALLENGE_TITLE = "title";
    public static final String COLUMN_CHALLENGE_DESCRIPTION = "description";
    public static final String COLUMN_CHALLENGE_CATEGORY = "category";
    public static final String COLUMN_CHALLENGE_TARGET_AMOUNT = "targetAmount";
    public static final String COLUMN_CHALLENGE_CURRENT_AMOUNT = "currentAmount";
    public static final String COLUMN_CHALLENGE_START_DATE = "startDate";
    public static final String COLUMN_CHALLENGE_END_DATE = "endDate";
    public static final String COLUMN_CHALLENGE_COMPLETED = "completed";
    public static final String COLUMN_CHALLENGE_XP_REWARD = "xpReward";

    // Tabla Intervention Log
    public static final String TABLE_INTERVENTION_LOG = "intervention_log";
    public static final String COLUMN_INTERVENTION_ID = "id";
    public static final String COLUMN_INTERVENTION_USER_ID = "userId";
    public static final String COLUMN_INTERVENTION_EXPENSE_ID = "expenseId";
    public static final String COLUMN_INTERVENTION_TYPE = "interventionType";
    public static final String COLUMN_INTERVENTION_MESSAGE = "message";
    public static final String COLUMN_INTERVENTION_IMPACT_ON_GOAL = "impactOnGoal";
    public static final String COLUMN_INTERVENTION_ALTERNATIVE_SUGGESTION = "alternativeSuggestion";
    public static final String COLUMN_INTERVENTION_TIMESTAMP = "timestamp";

    // Tabla Daily Analysis
    public static final String TABLE_DAILY_ANALYSIS = "daily_analysis";
    public static final String COLUMN_DAILY_ANALYSIS_ID = "id";
    public static final String COLUMN_DAILY_ANALYSIS_USER_ID = "userId";
    public static final String COLUMN_DAILY_ANALYSIS_DATE = "analysisDate";
    public static final String COLUMN_DAILY_ANALYSIS_TOTAL_SPENT = "totalSpent";
    public static final String COLUMN_DAILY_ANALYSIS_TOTAL_INCOME = "totalIncome";
    public static final String COLUMN_DAILY_ANALYSIS_NET_BALANCE = "netBalance";
    public static final String COLUMN_DAILY_ANALYSIS_CATEGORY_BREAKDOWN = "categoryBreakdown";
    public static final String COLUMN_DAILY_ANALYSIS_ANOMALY_DETECTED = "anomalyDetected";

    // Tabla Alternatives
    public static final String TABLE_ALTERNATIVES = "alternatives";
    public static final String COLUMN_ALTERNATIVE_ID = "id";
    public static final String COLUMN_ALTERNATIVE_CATEGORY = "category";
    public static final String COLUMN_ALTERNATIVE_KEYWORD = "descriptionKeyword";
    public static final String COLUMN_ALTERNATIVE_SUGGESTION = "suggestion";
    public static final String COLUMN_ALTERNATIVE_SAVINGS = "savingsPercentage";


    // Umbrales para intervenciones
    public static final double HIGH_EXPENSE_THRESHOLD = 0.8; // 80% del ingreso
    public static final int CONSECUTIVE_HIGH_DAYS = 3; // 3 días seguidos con gastos altos


    // ========== ACHIEVEMENT KEYS ==========
    // Conteo de gastos
    public static final String ACHIEVEMENT_FIRST_EXPENSE = "first_expense";
    public static final String ACHIEVEMENT_10_EXPENSES = "10_expenses";
    public static final String ACHIEVEMENT_50_EXPENSES = "50_expenses";
    public static final String ACHIEVEMENT_100_EXPENSES = "100_expenses";
    public static final String ACHIEVEMENT_500_EXPENSES = "500_expenses";

    // Rachas
    public static final String ACHIEVEMENT_WEEK_STREAK = "week_streak";
    public static final String ACHIEVEMENT_MONTH_STREAK = "month_streak";
    public static final String ACHIEVEMENT_100_DAYS_STREAK = "100_days_streak";

    // Presupuesto
    public static final String ACHIEVEMENT_UNDER_BUDGET_WEEK = "under_budget_week";
    public static final String ACHIEVEMENT_UNDER_BUDGET_MONTH = "under_budget_month";

    // Categorías
    public static final String ACHIEVEMENT_CATEGORY_EXPLORER = "category_explorer";
    public static final String ACHIEVEMENT_CATEGORY_MASTER = "category_master";

    // Ahorro
    public static final String ACHIEVEMENT_SAVER_50 = "saver_50";
    public static final String ACHIEVEMENT_SAVER_100 = "saver_100";
    public static final String ACHIEVEMENT_SAVER_500 = "saver_500";


}
