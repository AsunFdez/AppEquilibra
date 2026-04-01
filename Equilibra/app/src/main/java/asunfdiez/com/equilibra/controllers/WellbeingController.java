package asunfdiez.com.equilibra.controllers;
import android.content.Context;
import asunfdiez.com.equilibra.database.DatabaseHelper;
import asunfdiez.com.equilibra.models.InterventionMessage;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * WellbeingController - Controlador para el bienestar financiero
 * Determina cuándo mostrar intervenciones suaves y genera mensajes personalizados
 */
public class WellbeingController {

    private Context context;
    private DatabaseHelper databaseHelper;
    private SharedPrefsManager prefsManager;


    public WellbeingController(Context context) {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance(context);
        this.prefsManager = SharedPrefsManager.getInstance(context);
    }

    /**
     * Determina si se debe mostrar una intervención suave
     */
    public boolean shouldShowIntervention() {
        // No mostrar si ya se vio hoy
        if (prefsManager.wasInterventionSeenToday()) {
            return false;
        }

        // Verificar si los gastos son altos
        return areExpensesHigh();
    }

    /**
     * Verifica si los gastos están altos
     */
    private boolean areExpensesHigh() {
        double monthlyIncome = databaseHelper.getTotalIncomeThisMonth();
        double monthlyExpenses = databaseHelper.getTotalExpensesThisMonth();

        // Si no hay ingresos registrados, no mostrar intervención
        if (monthlyIncome == 0) {
            return false;
        }

        // Calcular ratio de gastos
        double expenseRatio = monthlyExpenses / monthlyIncome;

        // Verificar si supera el umbral
        if (expenseRatio >= Constants.HIGH_EXPENSE_THRESHOLD) {
            return true;
        }

        // Verificar gastos de últimos 3 días
        double last3DaysExpenses = databaseHelper.getExpensesLastNDays(3);
        double dailyBudget = monthlyIncome / 30;
        double threeDayBudget = dailyBudget * 3;

        return last3DaysExpenses > (threeDayBudget * 1.5);
    }

    /**
     * Obtiene el mensaje de intervención personalizado
     */
    public InterventionMessage getInterventionMessage() {
        double monthlyIncome = databaseHelper.getTotalIncomeThisMonth();
        double monthlyExpenses = databaseHelper.getTotalExpensesThisMonth();
        double expenseRatio = monthlyIncome > 0 ? monthlyExpenses / monthlyIncome : 0;

        InterventionMessage message = new InterventionMessage();

        if (expenseRatio >= 0.9) {
            // Gastos muy altos (90%+)
            message.setTitle("Un momento para reflexionar 🌱");
            message.setMessage(
                    "Hemos notado que tus gastos están muy cerca de tus ingresos este mes. " +
                            "No te preocupes, esto puede pasar. Lo importante es que estás consciente de ello. " +
                            "Vamos a trabajar juntos para encontrar el equilibrio."
            );
            message.setTips(new String[]{
                    "💡 Revisa tus gastos de los últimos días e identifica cuáles eran realmente necesarios",
                    "🎯 Considera establecer un límite diario para los próximos días",
                    "🌟 Pequeños ajustes pueden hacer una gran diferencia"
            });
        } else if (expenseRatio >= 0.8) {
            // Gastos altos (80-90%)
            message.setTitle("Momento de ajustar el rumbo 🧭");
            message.setMessage(
                    "Tus gastos han sido altos últimamente. Esto es completamente normal, " +
                            "pero es un buen momento para hacer una pausa y revisar tu situación."
            );
            message.setTips(new String[]{
                    "📊 Revisa tus categorías de gasto principales",
                    "💰 Intenta identificar gastos que puedas posponer",
                    "🎯 Recuerda tu meta de ahorro, cada paso cuenta"
            });
        } else {
            // Gastos concentrados en pocos días
            message.setTitle("Gastos concentrados 📈");
            message.setMessage(
                    "Has tenido varios gastos importantes en los últimos días. " +
                            "Puede que sean necesarios, pero es bueno estar consciente del impacto en tu presupuesto."
            );
            message.setTips(new String[]{
                    "📝 Revisa si estos gastos eran planificados",
                    "🗓️ Intenta espaciar gastos grandes cuando sea posible",
                    "💪 Mantén el equilibrio en los próximos días"
            });
        }

        return message;
    }

    /**
     * Marca la intervención como vista
     */
    public void markInterventionAsSeen() {
        prefsManager.markInterventionSeenToday();
    }

    /**
     * Calcula el nivel de bienestar financiero (0-100)
     */
    public int getWellbeingScore() {
        double monthlyIncome = databaseHelper.getTotalIncomeThisMonth();
        double monthlyExpenses = databaseHelper.getTotalExpensesThisMonth();

        if (monthlyIncome == 0) {
            return 50; // Score neutral si no hay datos
        }

        double savingsRate = (monthlyIncome - monthlyExpenses) / monthlyIncome;

        // Convertir a score 0-100
        int score;
        if (savingsRate >= 0.3) {
            score = 100; // Excelente
        } else if (savingsRate >= 0.2) {
            score = 85; // Muy bien
        } else if (savingsRate >= 0.1) {
            score = 70; // Bien
        } else if (savingsRate >= 0) {
            score = 55; // Regular
        } else {
            score = 30; // Necesita atención
        }

        return score;
    }

    /**
     * Obtiene mensaje de bienestar según el score
     */
    public String getWellbeingMessage(int score) {
        if (score >= 85) {
            return "¡Excelente! Tu bienestar financiero es muy bueno 🌟";
        } else if (score >= 70) {
            return "Buen trabajo. Vas por buen camino 👍";
        } else if (score >= 55) {
            return "Vas bien, pero hay oportunidades de mejora 💪";
        } else {
            return "Enfoquémonos en recuperar el equilibrio 🌱";
        }
    }
}
