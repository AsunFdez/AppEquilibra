package asunfdiez.com.equilibra.controllers;

import android.content.Context;

import asunfdiez.com.equilibra.repositories.CloudSyncRepository;

/**
 * Controlador para la lógica de sincronización en la nube.
 * 
 * <p>Actúa como intermediario entre el ViewModel y el Repositorio de Sincronización,
 * gestionando la autenticación en Firebase y las tareas de sincronización.</p>
 */
public class CloudSyncController {

    private final CloudSyncRepository syncRepository;

    public CloudSyncController(Context context) {
        this.syncRepository = new CloudSyncRepository(context);
    }

    /**
     * Inicia sesión con Google en Firebase.
     */
    public void login(String idToken, CloudSyncRepository.OnAuthCompleteListener listener) {
        syncRepository.loginWithGoogle(idToken, listener);
    }

    /**
     * Cierra la sesión de Firebase.
     */
    public void logout() {
        syncRepository.logout();
    }

    /**
     * Obtiene el email del usuario conectado.
     */
    public String getFirebaseUserEmail() {
        return syncRepository.getCurrentUserEmail();
    }

    /**
     * Indica si el usuario está autenticado en Firebase.
     */
    public boolean isFirebaseAuthenticated() {
        return syncRepository.isAuthenticated();
    }

    /**
     * Programa una sincronización inmediata.
     */
    public void syncNow() {
        syncNow(null);
    }

    /**
     * Programa una sincronización inmediata con un callback.
     * @param callback Callback que se ejecutará al finalizar la sincronización.
     */
    public void syncNow(CloudSyncRepository.OnSyncCompleteListener callback) {
        syncRepository.scheduleImmediateSync(callback);
    }
}
