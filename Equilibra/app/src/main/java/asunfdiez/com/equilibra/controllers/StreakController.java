package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.StreakRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

/**
 * Controlador de racha de usuarios.
 * 
 * <p>Gestiona la lógica de negocio para las rachas de registro de gastos 
 * y presupuesto. Coordina la actualización de rachas cuando el usuario 
 * realiza actividades importantes.</p>
 */
public class StreakController {

    private final StreakRepository streakRepository;
    private final UserProfileRepository userProfileRepository;

    public StreakController(Context context) {
        this.streakRepository = new StreakRepository(context);
        this.userProfileRepository = new UserProfileRepository(context);
    }

    /**
     * Obtiene una racha por tipo como LiveData reactivo
     */
    public LiveData<Streak> getStreakByType(long userId, String type) {
        return streakRepository.getStreakByType(userId, type);
    }

    /**
     * Obtiene una racha por tipo de forma síncrona
     */
    public Streak getStreakByTypeSync(long userId, String type) {
        return streakRepository.getStreakByTypeSync(userId, type);
    }

    /**
     * Actualiza una racha existente
     */
    public void updateStreak(Streak streak) {
        streakRepository.updateStreak(streak);
    }

    /**
     * Registra una actividad (gasto) y actualiza la racha de registro
     * Se ejecuta en un hilo de fondo
     */
    public void recordActivity() {
        new Thread(() -> {
            try {
                UserProfile user = userProfileRepository.getUserProfileSync();
                if (user != null) {
                    Streak streak = streakRepository.getStreakByTypeSync(user.getId(), Streak.TYPE_RECORDING);
                    if (streak != null) {
                        streak.recordActivity();
                        streakRepository.updateStreak(streak);
                        Log.d("StreakController", "Recording streak updated: " + streak.getConsecutiveDays() + " days");
                    }
                }
            } catch (Exception e) {
                Log.e("StreakController", "Error recording activity streak", e);
            }
        }).start();
    }

    /**
     * Registra un día dentro del presupuesto y actualiza la racha de presupuesto
     * Se ejecuta en un hilo de fondo
     */
    public void recordBudgetDay() {
        new Thread(() -> {
            try {
                UserProfile user = userProfileRepository.getUserProfileSync();
                if (user != null) {
                    Streak streak = streakRepository.getStreakByTypeSync(user.getId(), Streak.TYPE_BUDGET);
                    if (streak != null) {
                        streak.recordActivity();
                        streakRepository.updateStreak(streak);
                        Log.d("StreakController", "Budget streak updated: " + streak.getConsecutiveDays() + " days");
                    }
                }
            } catch (Exception e) {
                Log.e("StreakController", "Error recording budget day streak", e);
            }
        }).start();
    }
}
