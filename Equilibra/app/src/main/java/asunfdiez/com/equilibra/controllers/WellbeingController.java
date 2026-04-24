package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.InterventionMessage;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * Controlador especializado en la evaluación y fomento del bienestar financiero del usuario.
 * 
 * <p>Analiza el comportamiento de gasto actual comparándolo con el presupuesto y 
 * determina el nivel de "estrés" financiero. Es responsable de decidir cuándo 
 * activar una intervención suave y de generar los mensajes motivadores o de 
 * alerta correspondientes.</p>
 */
public class WellbeingController {

    private Context context;
    
    // Repositorio de gastos para el análisis de comportamiento
    private ExpenseRepository expenseRepository;
    
    // Gestor de preferencias para controlar la frecuencia de las intervenciones
    private SharedPrefsManager prefsManager;


    public WellbeingController(Context context) {
        this.context = context;
        this.expenseRepository = new ExpenseRepository (context);
        this.prefsManager = SharedPrefsManager.getInstance(context);
    }

    /**
     * Evalúa diversos criterios para determinar si es oportuno presentar una 
     * alerta de intervención al usuario en este momento.
     * 
     * @return Verdadero si se cumplen las condiciones de intervención y no se ha mostrado una hoy.
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
        double monthlyIncome = expenseRepository.getTotalIncomeThisMonth();
        double monthlyExpenses = expenseRepository.getTotalExpensesThisMonth();

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
        double last3DaysExpenses = expenseRepository.getExpensesLastNDays(3);
        double dailyBudget = monthlyIncome / 30;
        double threeDayBudget = dailyBudget * 3;

        return last3DaysExpenses > (threeDayBudget * 1.5);
    }

    /**
     * Obtiene el mensaje de intervención personalizado
     */
    public InterventionMessage getInterventionMessage() {
        double monthlyIncome = expenseRepository.getTotalIncomeThisMonth();
        double monthlyExpenses = expenseRepository.getTotalExpensesThisMonth();
        double expenseRatio = monthlyIncome > 0 ? monthlyExpenses / monthlyIncome : 0;

        InterventionMessage message = new InterventionMessage();

        if (expenseRatio >= 0.9) {
            // Gastos muy altos (90%+)
            message.setTitle(context.getString(R.string.intervention_expenses_very_high_title));
            message.setMessage(context.getString(R.string.intervention_expenses_very_high_description));
            message.setTips(new String[]{
                    context.getString(R.string.intervention_expenses_very_high_tip1),
                    context.getString(R.string.intervention_expenses_very_high_tip2),
                    context.getString(R.string.intervention_expenses_very_high_tip3)
            });

        } else if (expenseRatio >= 0.8) {
            // Gastos altos (80-90%)
            message.setTitle(context.getString(R.string.intervention_expenses_high_title));
            message.setMessage(context.getString(R.string.intervention_expenses_high_description));
            message.setTips(new String[]{
                    context.getString(R.string.intervention_expenses_high_tip1),
                    context.getString(R.string.intervention_expenses_high_tip2),
                    context.getString(R.string.intervention_expenses_high_tip3)
            });
        } else {
            // Gastos concentrados en pocos días
                message.setTitle(context.getString(R.string.intervention_concentrated_expenses_title));
                message.setMessage(context.getString(R.string.intervention_concentrated_expenses_description));
                message.setTips(new String[]{
                        context.getString(R.string.intervention_concentrated_expenses_tip1),
                        context.getString(R.string.intervention_concentrated_expenses_tip2),
                        context.getString(R.string.intervention_concentrated_expenses_tip3)
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
        double monthlyIncome = expenseRepository.getTotalIncomeThisMonth();
        double monthlyExpenses = expenseRepository.getTotalExpensesThisMonth();

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
            return context.getString(R.string.wellbeing_excellent_message);
        } else if (score >= 70) {
            return context.getString(R.string.wellbeing_good_job_message);
        } else if (score >= 55) {
            return context.getString(R.string.wellbeing_improve_message);
        } else {
            return context.getString(R.string.wellbeing_recover_balance_message);
        }
    }
}
