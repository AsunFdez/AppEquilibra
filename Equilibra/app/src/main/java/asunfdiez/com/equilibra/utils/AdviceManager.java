package asunfdiez.com.equilibra.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.Calendar;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.Advice;

/**
 * Gestor responsable de la selección y rotación de consejos financieros diarios.
 * 
 * <p>Proporciona una lógica determinística para asignar un consejo diferente a cada día 
 * del mes, asegurando variedad en el contenido presentado al usuario sin necesidad 
 * de almacenamiento complejo. Utiliza recursos de cadenas (strings) para facilitar 
 * la localización.</p>
 */
public class AdviceManager {

    /**
     * Identificadores de recursos para los títulos de los consejos.
     */
    private static final int[] ADVICE_TITLES = {
            R.string.advice_0,  R.string.advice_1,  R.string.advice_2,  R.string.advice_3,
            R.string.advice_4,  R.string.advice_5,  R.string.advice_6,  R.string.advice_7,
            R.string.advice_8,  R.string.advice_9,  R.string.advice_10, R.string.advice_11,
            R.string.advice_12, R.string.advice_13, R.string.advice_14, R.string.advice_15,
            R.string.advice_16, R.string.advice_17, R.string.advice_18, R.string.advice_19,
            R.string.advice_20, R.string.advice_21, R.string.advice_22, R.string.advice_23,
            R.string.advice_24
    };

    /**
     * Array de IDs de recursos para las descripciones de consejos
     * El índice debe coincidir con ADVICE_TITLES
     */
    private static final int[] ADVICE_DESCRIPTIONS = {
            R.string.advice_0_desc,  R.string.advice_1_desc,  R.string.advice_2_desc,  R.string.advice_3_desc,
            R.string.advice_4_desc,  R.string.advice_5_desc,  R.string.advice_6_desc,  R.string.advice_7_desc,
            R.string.advice_8_desc,  R.string.advice_9_desc,  R.string.advice_10_desc, R.string.advice_11_desc,
            R.string.advice_12_desc, R.string.advice_13_desc, R.string.advice_14_desc, R.string.advice_15_desc,
            R.string.advice_16_desc, R.string.advice_17_desc, R.string.advice_18_desc, R.string.advice_19_desc,
            R.string.advice_20_desc, R.string.advice_21_desc, R.string.advice_22_desc, R.string.advice_23_desc,
            R.string.advice_24_desc
    };

    private static final int TOTAL_ADVICES = ADVICE_TITLES.length;

    /**
     * Obtiene el consejo del día actual
     * 
     * Algoritmo:
     * 1. Calcula el número de día desde epoch (01/01/1970)
     * 2. Usa módulo para obtener índice de consejo (0-24)
     * 3. Asegura que sea diferente al del día anterior
     * 
     * @param context Contexto para acceder a strings.xml
     * @return Advice del día actual
     */
    public static Advice getDailyAdvice(@NonNull Context context) {
        int todayIndex = calculateAdviceIndex();
        return buildAdvice(context, todayIndex);
    }

    /**
     * Obtiene el consejo del día anterior
     * Útil para verificar que hoy es diferente
     * 
     * @param context Contexto
     * @return Advice del día anterior
     */
    public static Advice getYesterdayAdvice(@NonNull Context context) {
        int yesterdayIndex = calculateAdviceIndex(1); // 1 día atrás
        return buildAdvice(context, yesterdayIndex);
    }

    /**
     * Obtiene un consejo específico por índice
     * 
     * @param context Contexto
     * @param index Índice del consejo (0-24)
     * @return Advice del índice especificado
     */
    public static Advice getAdviceByIndex(@NonNull Context context, int index) {
        if (index < 0 || index >= TOTAL_ADVICES) {
            index = 0; // Fallback al primer consejo
        }
        return buildAdvice(context, index);
    }

    /**
     * Obtiene un consejo aleatorio (1 de 25)
     * 
     * @param context Contexto
     * @return Advice aleatorio
     */
    public static Advice getRandomAdvice(@NonNull Context context) {
        int randomIndex = (int) (Math.random() * TOTAL_ADVICES);
        return buildAdvice(context, randomIndex);
    }

    /**
     * Calcula el índice del consejo para una fecha determinada
     * 
     * @param daysAgo Días atrás (0 = hoy, 1 = ayer, 2 = anteayer, etc.)
     * @return Índice (0-24)
     */
    private static int calculateAdviceIndex(int daysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);

        // Número de días desde epoch hasta hoy
        long daysSinceEpoch = calendar.getTimeInMillis() / (1000 * 60 * 60 * 24);

        // Usa módulo para obtener un índice cíclico
        return (int) (daysSinceEpoch % TOTAL_ADVICES);
    }

    /**
     * Calcula el índice del consejo para hoy
     * @return Índice del consejo de hoy
     */
    private static int calculateAdviceIndex() {
        return calculateAdviceIndex(0);
    }

    /**
     * Construye un objeto Advice a partir de un índice
     * 
     * @param context Contexto
     * @param index Índice válido (0-24)
     * @return Advice construido
     */
    private static Advice buildAdvice(@NonNull Context context, int index) {
        if (index < 0 || index >= TOTAL_ADVICES) {
            index = 0;
        }

        String title = context.getString(ADVICE_TITLES[index]);
        String description = context.getString(ADVICE_DESCRIPTIONS[index]);

        return new Advice(title, description);
    }

    /**
     * Retorna el número total de consejos disponibles
     * @return 25 (número de consejos)
     */
    public static int getTotalAdvices() {
        return TOTAL_ADVICES;
    }

    /**
     * Verifica si el consejo de hoy es diferente al de ayer
     * Debería ser siempre true si TOTAL_ADVICES > 1
     * 
     * @param context Contexto
     * @return true si son diferentes
     */
    public static boolean isDifferentFromYesterday(@NonNull Context context) {
        int todayIndex = calculateAdviceIndex(0);
        int yesterdayIndex = calculateAdviceIndex(1);
        return todayIndex != yesterdayIndex || TOTAL_ADVICES == 1;
    }
}
