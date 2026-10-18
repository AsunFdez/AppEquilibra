package asunfdiez.com.equilibra.utils;

/**
 * Clase de utilidad para la validación de entradas de usuario en formularios.
 * 
 * <p>Proporciona métodos estáticos para verificar la integridad y corrección 
 * de datos como nombres de metas, importes numéricos y otros campos de texto
 * antes de ser procesados o guardados en la base de datos.</p>
 */
public final class InputValidator {
    // Importe mínimo aceptable para transacciones o metas
    private static final double MIN_AMOUNT = 0.01;

    private InputValidator() {
        throw new AssertionError("No se pueden instanciar validadores");
    }

    /**
     * Verifica si el nombre de una meta es válido (no nulo ni vacío).
     */
    public static boolean isValidGoal(String goal) {
        return goal != null && !goal.trim().isEmpty();
    }

    /**
     * Verifica si una cadena de texto representa un importe numérico válido y mayor al mínimo.
     */
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
