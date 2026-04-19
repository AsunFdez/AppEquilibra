package asunfdiez.com.equilibra.models;

import android.content.Context;

import asunfdiez.com.equilibra.R;

public record Advice(String title, String description) {
    public static Advice getRegisterExpenseToday(Context context) {
        return new Advice (context.getString(R.string.today_advice), context.getString(R.string.advice_1));
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



}
