package asunfdiez.com.equilibra.utils;

public final class Constants {
    private Constants() {
        throw new AssertionError("No se pueden instanciar constantes");
    }


    //---- SHARED PREFERENCES KEYS

    public static final String PREFS_NAME = "EquilibraPrefs";

    // Onboarding
    public static final String KEY_ONBOARDING_COMPLETE = "onboardingComplete";
    public static final String KEY_USER_LEVEL = "userLevel";
    public static final String USER_LEVEL_BEGINNER = "userBeginner";
    public static final String USER_LEVEL_INTERMEDIATE = "userIntermediate";
    public static final String USER_LEVEL_ADVANCED = "userAdvanced";
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
    public static final int DATABASE_VERSION = 1;

    // Tabla Expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ID = "_id";
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

    // Tabla Budgets
    public static final String TABLE_BUDGETS = "budgets";
    public static final String COLUMN_BUDGET_ID = "_id";
    public static final String COLUMN_BUDGET_CATEGORY = "category";
    public static final String COLUMN_BUDGET_AMOUNT = "amount";
    public static final String COLUMN_BUDGET_PERIOD = "period";
    public static final String COLUMN_BUDGET_MONTH = "month";
    public static final String COLUMN_BUDGET_YEAR = "year";

    // Tabla Achievements
    public static final String TABLE_ACHIEVEMENTS = "achievements";
    public static final String COLUMN_ACHIEVEMENT_ID = "_id";
    public static final String COLUMN_ACHIEVEMENT_TITLE = "title";
    public static final String COLUMN_ACHIEVEMENT_DESCRIPTION = "description";
    public static final String COLUMN_ACHIEVEMENT_XP = "xp";
    public static final String COLUMN_ACHIEVEMENT_ICON = "icon";
    public static final String COLUMN_ACHIEVEMENT_UNLOCKED = "unlocked";
    public static final String COLUMN_ACHIEVEMENT_DATE_UNLOCKED = "date_unlocked";

    // ========================================
    // CATEGORÍAS DE GASTOS
    // ========================================
    public static final String CATEGORY_FOOD = "Comida";
    public static final String CATEGORY_TRANSPORT = "Transporte";
    public static final String CATEGORY_ENTERTAINMENT = "Entretenimiento";
    public static final String CATEGORY_SHOPPING = "Compras";
    public static final String CATEGORY_HEALTH = "Salud";
    public static final String CATEGORY_EDUCATION = "Educación";
    public static final String CATEGORY_BILLS = "Servicios";
    public static final String CATEGORY_OTHERS = "Otros";

}
