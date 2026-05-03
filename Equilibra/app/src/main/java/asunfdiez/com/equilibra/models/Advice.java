package asunfdiez.com.equilibra.models;

import android.content.Context;
import java.util.Objects;
import asunfdiez.com.equilibra.R;

/**
 * Clase que representa un consejo o sugerencia financiera para el usuario.
 * 
 * <p>Contiene un título y una descripción, y proporciona métodos estáticos de utilidad 
 * para generar consejos comunes basados en el contexto de la aplicación y recursos de cadena.</p>
 */
public class Advice {
    private final String title;
    private final String description;

    public Advice(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String title() { return title; }
    public String description() { return description; }

    public static Advice getRegisterExpenseToday(Context context) {
        return new Advice (context.getString(R.string.today_advice), context.getString(R.string.advice));
    }
    public static Advice getWelcome(Context context) {
        return new Advice (context.getString(R.string.tip_title_welcome), context.getString(R.string.tip_desc_welcome));
    }

    public static Advice getExpenseRegistered(Context context) {
        return new Advice (context.getString(R.string.tip_title_expense_registered), context.getString(R.string.tip_desc_expense_registered));
    }

    //--- para quick actions
    public static Advice getCompleteUserProfile (Context context) {
        return new Advice (context.getString(R.string.tip_title_complete_user_profile), context.getString(R.string.tip_desc_complete_user_profile));
    }

    public static Advice getCompleteBudget(Context context) {
        return new Advice (context.getString(R.string.setup_budget), context.getString(R.string.setup_budget_des));
    }

    public static Advice getQuickAction(Context context) {
        return new Advice (context.getString(R.string.quick_action_title), context.getString(R.string.quick_action_desc));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advice advice = (Advice) o;
        return Objects.equals(title, advice.title) &&
                Objects.equals(description, advice.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }
}
