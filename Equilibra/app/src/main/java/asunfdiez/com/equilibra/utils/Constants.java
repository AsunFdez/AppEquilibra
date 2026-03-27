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


    // Formato de moneda
    public static final String CURRENCY_FORMAT = "€%.2f";

    // Factor de división para ahorro mensual
    public static final int MONTHLY_DIVISION_FACTOR = 6;




}
