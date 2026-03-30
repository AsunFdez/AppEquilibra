package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * Controller para gestión de usuarios
 * Maneja la lógica de negocio relacionada con usuarios
 *
 */
public class UserController {

    private final SharedPrefsManager preferencesManager;
    private UserProfile user;

    /**
     * Constructor del controller
     *
     * @param context Contexto de la aplicación
     */
    public UserController(Context context) {
        this.preferencesManager = SharedPrefsManager.getInstance(context);
    }

    /**
     * Obtiene el usuario actual
     *
     * @return Usuario actual o null si no existe
     */
    public UserProfile getCurrentUser() {
        user = preferencesManager.getUserProfile();

        if (user.getId() == -1) {
            return createDefaultUser();
        }
        return user;

    }

    /**
     * Obtiene un usuario por su ID
     *
     * @param userId ID del usuario
     * @return Usuario encontrado o null
     */
    public UserProfile getUserById(long userId) {
        // TODO: Implementar obtención real desde base de datos
        // Por ahora retornamos un usuario de ejemplo
        user = new UserProfile();
        user.setId(userId);
        user.setName(preferencesManager.getUserName());
        user.setEmail(preferencesManager.getUserEmail());
        user.setMonthlyIncome(preferencesManager.getMonthlyIncome());
        return user;
    }

    /**
     * Crea un usuario por defecto
     *
     * @return Usuario por defecto
     */
    private UserProfile createDefaultUser() {
        user = new UserProfile();
        user.setId(1);
        user.setName("Usuario");
        user.setEmail("usuario@equilibra.com");
        return user;
    }

    /**
     * Actualiza los datos del usuario
     *
     * @param user Usuario con datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean updateUser(UserProfile user) {
        if (user == null) {
            return false;
        }

        // Actualizar en PreferencesManager
        preferencesManager.setIdNameEmailIncomeUserData(user.getId(), user.getName(), user.getEmail(), user.getMonthlyIncome());

        // TODO: Actualizar en base de datos SQLite

        return true;
    }

    /**
     * Crea un nuevo usuario
     *
     * @param user Usuario a crear
     * @return ID del usuario creado o -1 si falla
     */
    public long createUser(UserProfile user) {
        if (user == null) {
            return -1;
        }

        // TODO: Guardar en base de datos SQLite
        long userId = 1; // ID temporal

        // Guardar en preferencias
        preferencesManager.setIdNameEmailIncomeUserData(userId, user.getName(), user.getEmail(), user.getMonthlyIncome());

        return userId;
    }

    /**
     * Elimina el usuario actual
     *
     * @return true si se eliminó correctamente
     */
    public boolean deleteCurrentUser() {
        long userId = preferencesManager.getCurrentUserId();

        if (userId == -1) {
            return false;
        }

        // TODO: Eliminar de base de datos SQLite

        // Limpiar preferencias
        preferencesManager.clearAll();

        return true;
    }

    /**
     * Elimina todos los datos del usuario
     *
     * @return true si se eliminó correctamente
     */
    public boolean deleteAllUserData() {
        // TODO: Eliminar todos los datos de la base de datos SQLite
        // (gastos, metas, logros, etc.)

        return deleteCurrentUser();
    }

    /**
     * Verifica si existe un usuario activo
     *
     * @return true si existe un usuario
     */
    public boolean hasActiveUser() {
        return preferencesManager.getCurrentUserId() != -1;
    }

    /**
     * Cambia la contraseña del usuario
     *
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió correctamente
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        // TODO: Implementar cambio de contraseña con validación
        // Verificar contraseña actual
        // Hashear nueva contraseña
        // Actualizar en base de datos
        return false;
    }

    /**
     * Valida las credenciales del usuario
     *
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    public UserProfile validateCredentials(String email, String password) {
        // TODO: Implementar validación real con base de datos
        return null;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() {
        preferencesManager.setCurrentUserId(-1);
    }
}
