package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.UserProfile;

/**
 * ViewModel responsable de la lógica de generación y provisión de consejos financieros.
 * 
 * <p>Centraliza la lógica para seleccionar qué consejo diario o acción rápida debe 
 * mostrarse al usuario en función de su perfil, estado financiero y comportamiento. 
 * Utiliza transformaciones reactivas para actualizar los consejos automáticamente 
 * cuando cambian los datos del usuario.</p>
 */
public class AdviceViewModel extends AndroidViewModel {

    // Controlador para gestionar el estado del perfil de usuario
    private final UserProfileController userProfileController;
    
    // Perfil de usuario observado
    private final LiveData<UserProfile> userProfile;

    /**
     * LiveData reactivo que proporciona el consejo financiero del día.
     * Se actualiza automáticamente si ocurren cambios relevantes en el perfil.
     */
    private final LiveData<Advice> dailyAdvice;

    /**
     * LiveData reactivo: Acción rápida recomendada
     * Emite una acción según el estado del perfil, presupuesto...
     */
    private final LiveData<Advice> quickAction;

    /**
     * LiveData reactivo: Título y descripción para la siguiente acción
     * Emitter una acción según se haya completado pasos
     */
    private final LiveData<Advice> nextGoal;

    /**
     * Constructor
     * @param application Contexto de la aplicación
     */
    public AdviceViewModel(@NonNull Application application) {
        super(application);

        this.userProfileController = new UserProfileController(application);
        this.userProfile = userProfileController.getCurrentUser();

        // Transformar el perfil de usuario → Consejo del día
        // Se ejecuta automáticamente cada vez que userProfile cambia
        this.dailyAdvice = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            return userProfileController.getAdvice(user);
        });

        this.quickAction = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            List<Advice> actions = userProfileController.getQuickAction(user);
            return actions.get(0);
        });

        this.nextGoal = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            List<Advice> actions = userProfileController.getQuickAction(user);
            return actions.get(actions.size() - 1); // Siempre el último elemento (el segundo)
        });
    }

    /**
     * Getter reactivo: Consejo del día
     * @return LiveData que emite Advice cuando cambia el usuario
     */
    public LiveData<Advice> getDailyAdvice() {
        return dailyAdvice;
    }

    /**
     * Getter reactivo: Acción rápida
     * @return LiveData que emite Advice cuando cambia el usuario
     */
    public LiveData<Advice> getQuickAction() {
        return quickAction;
    }

    public LiveData<Advice> getNextGoal() {
        return nextGoal;
    }

    /**
     * Getter reactivo: Perfil del usuario actual
     * @return LiveData del perfil de usuario
     */
    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    /**
     * Refresh manual de consejos (si es necesario)
     * Nota: Normalmente, se actualiza automáticamente via Transformations
     */
    public void refreshAdvice() {
        // Forzar actualización del perfil
        UserProfile current = userProfile.getValue();
        if (current != null) {
            // Los Transformations se actualizarán automáticamente
            userProfileController.updateUser(current, null);
        }
    }
}
