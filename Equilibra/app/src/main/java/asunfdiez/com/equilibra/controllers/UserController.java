package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * Controller para gestión de usuarios
 * Maneja la lógica de negocio relacionada con usuarios
 *
 */
public class UserController {

    private final UserProfileRepository userRepository;
    private final SharedPrefsManager prefsManager;
    private UserProfile user;
    private final WellbeingController wellbeingController;


    /**
     * Constructor del controller
     *
     * @param context Contexto de la aplicación
     */
    public UserController(Context context) {
        this.userRepository = new UserProfileRepository(context);
        this.prefsManager = SharedPrefsManager.getInstance(context);
        this.wellbeingController = new WellbeingController(context);
    }

    /**
     * Obtiene el usuario actual
     *
     * @return Usuario actual o null si no existe
     */
    public LiveData<UserProfile> getCurrentUser() {
        return userRepository.getUserProfile();
    }

    public void updateUser(UserProfile user) {
        if (user == null) return;
        // Las operaciones de escritura en Room deben ser asíncronas
        Executors.newSingleThreadExecutor().execute(() -> {
            userRepository.updateProfile(user);
            android.util.Log.d("User Controller updateUser", user.toString());
        });
    }

    public void deleteCurrentUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Limpiar Room
            userRepository.deleteAll();
            //  Limpiar SharedPreferences para que SplashActivity detecte que no hay onboarding
            prefsManager.resetOnboarding();
            prefsManager.clearAll();
        });
    }
    /*
    * Actualiza la vista actual del usuario
    */
    public void setCurrentView(String view) {
        if (user == null) {
            return;
        }
        user.setCurrentView(view);
        userRepository.updateProfile(user);
    }

    public boolean checkWellbeing() {
        AtomicBoolean shouldShow = new AtomicBoolean(false);
        // Usamos un Executor para sacar la consulta a la base de datos del hilo UI
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Esta llamada se ejecuta en segundo plano
                shouldShow.set(wellbeingController.shouldShowIntervention());
            } catch (Exception e) {
                android.util.Log.e("ViewModel", "Error al comprobar bienestar", e);
            }
        });
        return shouldShow.get();
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


}
