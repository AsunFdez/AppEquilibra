package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import asunfdiez.com.equilibra.controllers.UserController;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

/**
 * AdviceViewModel - Gestor de consejos diarios
 *
 * Responsabilidad: Manejo centralizado de consejos financieros
 * 
 * - Obtiene el consejo del día actual
 * - Obtiene la acción rápida recomendada
 * - Valida el estado del usuario antes de mostrar consejos
 *
 * Patrones:
 * - Observables reactivos con LiveData
 * - Transformations.map() para derivar datos
 * - Sin lógica de UI (Activities limpias)
 * - Mantiene aislada la lógica de consejos
 */
public class AdviceViewModel extends AndroidViewModel {

    private final UserController userController;
    private final UserProfileRepository userRepository;
    private final LiveData<UserProfile> userProfile;

    /**
     * LiveData reactivo: Consejos del día
     * Emite un nuevo consejo cada vez que el perfil cambia
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

        this.userController = new UserController(application);
        this.userRepository = new UserProfileRepository(application);
        this.userProfile = userController.getCurrentUser();

        // Transformar el perfil de usuario → Consejo del día
        // Se ejecuta automáticamente cada vez que userProfile cambia
        this.dailyAdvice = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            return userController.getAdvice(user);
        });

        // Transformar el perfil de usuario → Acción rápida
        // Se ejecuta automáticamente cada vez que userProfile cambia
        this.quickAction = Transformations.map(userProfile, user -> {
            if (user == null) return null;
            return userController.getQuickAction(user);
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
