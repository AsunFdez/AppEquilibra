package asunfdiez.com.equilibra.utils;

public final class InputValidator {
    private static final double MIN_AMOUNT = 0.01;

    private InputValidator() {
        throw new AssertionError("No se pueden instanciar validadores");
    }

    public static boolean isValidGoal(String goal) {
        return goal != null && !goal.trim().isEmpty();
    }

    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        try {
            double value = Double.parseDouble(amount.trim());
            return value >= MIN_AMOUNT;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidSavingGoalInputs(String goal, String amount) {
        return isValidGoal(goal) && isValidAmount(amount);
    }
}
