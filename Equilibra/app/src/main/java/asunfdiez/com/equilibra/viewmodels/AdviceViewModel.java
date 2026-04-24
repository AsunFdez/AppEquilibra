package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

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
    
    // Repositorio para el acceso a datos del usuario
    private final UserProfileRepository userRepository;
    
    // Perfil de usuario observado
    private final LiveData<UserProfile> userProfile;

    /**
     * LiveData reactivo que proporciona el consejo financiero del día.
     * Se actualiza automáticamente si ocurren cambios relevantes en el perfil.
     */
    private final LiveData<Advice> dailyAdvice;

    /**
     * LiveData reactivo: Acción rápida recomendada
     * Emite una acción según el estado de completitud del perfil
     */
    private final LiveData<Advice> quickAction;

    /**
     * Constructor
     * @param application Contexto de la aplicación
     */
    public AdviceViewModel(@NonNull Application application) {
        super(application);

        this.userProfileController = new UserProfileController(application);
        this.userRepository = new UserProfileRepository(application);
        this.userProfile = userProfileController.getCurrentUser();

        // Transformar el perfil de usuario → Consejo del día
        // Se ejecuta automáticamente cada vez que userProfile cambia
        this.dailyAdvice = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            return userProfileController.getAdvice(user);
        });

        // Transformar el perfil de usuario → Acción rápida
        // Se ejecuta automáticamente cada vez que userProfile cambia
        this.quickAction = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            return userProfileController.getQuickAction(user);
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

    /**
     * Getter reactivo: Perfil del usuario actual
     * @return LiveData del perfil de usuario
     */
    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    /**
     * Refresh manual de consejos (si es necesario)
     * Nota: Normalmente se actualiza automáticamente via Transformations
     */
    public void refreshAdvice() {
        // Forzar actualización del perfil
        UserProfile current = userProfile.getValue();
        if (current != null) {
            // Los Transformations se actualizarán automáticamente
            userRepository.updateProfile(current, null);
        }
    }
}
