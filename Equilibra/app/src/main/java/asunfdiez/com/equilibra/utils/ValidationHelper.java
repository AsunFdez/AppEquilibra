package asunfdiez.com.equilibra.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Helper para validación de datos
 * Contiene métodos de validación reutilizables
 *
 */
public final class ValidationHelper {

    private ValidationHelper() {
        // Constructor privado para prevenir instanciación
        throw new UnsupportedOperationException("Cannot instantiate ValidationHelper");
    }

    /**
     * Valida si un email es válido
     *
     * @param email Email a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Valida si un teléfono es válido
     *
     * @param phone Teléfono a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return Patterns.PHONE.matcher(phone).matches() && phone.length() >= 10;
    }

    /**
     * Valida si una contraseña es segura
     * Mínimo 8 caracteres, debe contener mayúscula, minúscula y número
     *
     * @param password Contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     * Valida si un nombre es válido
     *
     * @param name Nombre a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 2;
    }

    /**
     * Valida si un número es positivo
     *
     * @param value Valor a validar
     * @return true si es positivo, false en caso contrario
     */
    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    /**
     * Valida si un número es no negativo
     *
     * @param value Valor a validar
     * @return true si es no negativo, false en caso contrario
     */
    public static boolean isNonNegativeNumber(double value) {
        return value >= 0;
    }

    /**
     * Valida si un texto tiene una longitud mínima
     *
     * @param text Texto a validar
     * @param minLength Longitud mínima
     * @return true si cumple, false en caso contrario
     */
    public static boolean hasMinLength(String text, int minLength) {
        return !TextUtils.isEmpty(text) && text.trim().length() >= minLength;
    }

    /**
     * Valida si un texto tiene una longitud máxima
     *
     * @param text Texto a validar
     * @param maxLength Longitud máxima
     * @return true si cumple, false en caso contrario
     */
    public static boolean hasMaxLength(String text, int maxLength) {
        if (TextUtils.isEmpty(text)) {
            return true;
        }
        return text.length() <= maxLength;
    }

    /**
     * Valida si un string es numérico
     *
     * @param str String a validar
     * @return true si es numérico, false en caso contrario
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida si un valor está en un rango
     *
     * @param value Valor a validar
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return true si está en el rango, false en caso contrario
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Valida si un string contiene solo letras
     *
     * @param text Texto a validar
     * @return true si solo contiene letras, false en caso contrario
     */
    public static boolean isAlphabetic(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");
    }

    /**
     * Valida si un string contiene solo letras y números
     *
     * @param text Texto a validar
     * @return true si solo contiene letras y números, false en caso contrario
     */
    public static boolean isAlphanumeric(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.matches("[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+");
    }
}

