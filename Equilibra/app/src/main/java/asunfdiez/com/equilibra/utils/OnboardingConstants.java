package asunfdiez.com.equilibra.utils;

public final class OnboardingConstants {
    private OnboardingConstants() {
        throw new AssertionError("No se pueden instanciar constantes");
    }

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

    // IDs de respuestas
    public static final String ANSWER_ID_SAVE_SPECIFIC = "save-specific";
    public static final String ANSWER_TEXT_SAVE_SPECIFIC = "Ahorrar para algo específico";

    // Formato de moneda
    public static final String CURRENCY_FORMAT = "€%.2f";

    // Factor de división para ahorro mensual
    public static final int MONTHLY_DIVISION_FACTOR = 6;
}
