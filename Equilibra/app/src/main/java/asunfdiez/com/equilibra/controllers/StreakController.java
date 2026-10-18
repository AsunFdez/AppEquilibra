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
                        streak.incrementCounter();
                        streakRepository.updateStreak(streak);
                        userProfileRepository.addXp(10);
                        Log.d("StreakController", "Recording streak incremented: " + streak.getConsecutiveDays());
                    }
                }
            } catch (Exception e) {
                Log.e("StreakController", "Error recording activity streak", e);
            }
        }).start();
    }

    /**
     * Decrementa el contador de la racha de registro (al borrar un gasto)
     */
    public void decrementActivity() {
        new Thread(() -> {
            try {
                UserProfile user = userProfileRepository.getUserProfileSync();
                if (user != null) {
                    Streak streak = streakRepository.getStreakByTypeSync(user.getId(), Streak.TYPE_RECORDING);
                    if (streak != null) {
                        streak.decrementCounter();
                        streakRepository.updateStreak(streak);
                        Log.d("StreakController", "Recording streak decremented: " + streak.getConsecutiveDays());
                    }
                }
            } catch (Exception e) {
                Log.e("StreakController", "Error decrementing activity streak", e);
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
                        userProfileRepository.addXp(100);
                        Log.d("StreakController", "Budget streak updated: " + streak.getConsecutiveDays() + " days");
                    }
                }
            } catch (Exception e) {
                Log.e("StreakController", "Error recording budget day streak", e);
            }
        }).start();
    }

    /**
     * Resetea las rachas semanales
     */
    public void resetWeeklyStreaks(long userId) {
        streakRepository.resetStreak(userId, Streak.TYPE_RECORDING);
        streakRepository.resetStreak(userId, Streak.TYPE_BUDGET);
        Log.d("StreakController", "Weekly streaks reset for user: " + userId);
    }

    /**
     * Asegura que el usuario tenga las rachas básicas inicializadas.
     * Si no existen, las crea.
     */
    public void ensureStreaksExist(long userId) {
        new Thread(() -> {
            try {
                checkAndCreateStreak(userId, Streak.TYPE_RECORDING);
                checkAndCreateStreak(userId, Streak.TYPE_BUDGET);
            } catch (Exception e) {
                Log.e("StreakController", "Error ensuring streaks exist", e);
            }
        }).start();
    }

    private void checkAndCreateStreak(long userId, String type) {
        Streak existing = streakRepository.getStreakByTypeSync(userId, type);
        if (existing == null) {
            Streak newStreak = new Streak();
            newStreak.setUserId(userId);
            newStreak.setType(type);
            newStreak.setConsecutiveDays(0);
            newStreak.setActive(true);
            newStreak.setStartDate(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            streakRepository.insertStreak(newStreak);
            Log.d("StreakController", "Created missing streak: " + type + " for user: " + userId);
        }
    }

    /**
     * Inicializa las rachas por defecto para un nuevo usuario
     */
    public void initializeDefaultStreaks(long userId) {
        new Thread(() -> {
            try {
                // Racha de registro
                Streak recording = new Streak();
                recording.setUserId(userId);
                recording.setType(Streak.TYPE_RECORDING);
                recording.setConsecutiveDays(0);
                recording.setActive(true);
                recording.setStartDate(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                streakRepository.insertStreak(recording);

                // Racha de presupuesto
                Streak budget = new Streak();
                budget.setUserId(userId);
                budget.setType(Streak.TYPE_BUDGET);
                budget.setConsecutiveDays(0);
                budget.setActive(true);
                budget.setStartDate(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                streakRepository.insertStreak(budget);

                Log.d("StreakController", "Default streaks initialized for user: " + userId);
            } catch (Exception e) {
                Log.e("StreakController", "Error initializing default streaks", e);
            }
        }).start();
    }
}
