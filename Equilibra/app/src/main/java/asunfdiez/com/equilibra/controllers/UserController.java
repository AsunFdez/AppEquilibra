package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.AdviceManager;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * Controller para gestión de usuarios
 * Maneja la lógica de negocio relacionada con usuarios
 *
 */
public class UserController {

    private final UserProfileRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final SharedPrefsManager prefsManager;
    private UserProfile user;
    private final WellbeingController wellbeingController;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    Context context;


    /**
     * Constructor del controller
     *
     * @param context Contexto de la aplicación
     */
    public UserController(Context context) {
        this.userRepository = new UserProfileRepository(context);
        this.expenseRepository = new ExpenseRepository(context);
        this.prefsManager = SharedPrefsManager.getInstance(context);
        this.wellbeingController = new WellbeingController(context);
        this.context = context;
    }

    /**
     * Obtiene el usuario actual
     *
     * @return Usuario actual o null si no existe
     */
    public LiveData<UserProfile> getCurrentUser() {
        return userRepository.getUserProfile();
    }

    public void updateUser(UserProfile user, @Nullable Runnable onComplete) {
        if (user == null) return;
        userRepository.updateProfile(user, () -> {
            android.util.Log.d("UserController", "Perfil guardado: " + user.toString());
            setDummyData(user); //solo para probar funciones concretas

            if (onComplete != null) {
                onComplete.run();
            }
        });

    }

    public void deleteCurrentUser(Runnable onComplete) {
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getInstance(context);
            database.clearAllTables();
            prefsManager.resetAll();
            // Recrear datos estáticos (categorías, logros, alternativas) de forma síncrona en este hilo de fondo
            AppDatabase.insertInitialCategories();
            AppDatabase.insertInitialAchievements();
            AppDatabase.insertInitialAlternatives();

            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }

    public boolean checkWellbeing() {
        AtomicBoolean shouldShow = new AtomicBoolean(false);
        // Usamos un Executor para sacar la consulta a la base de datos del hilo UI
        executor.execute(() -> {
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
     * Obtiene el consejo apropiado según el estado del usuario
     *
     * Lógica:
     * 1. Día 1 y sin gastos registrados → Mensaje de bienvenida
     * 2. Sin gastos registrados en la semana → Mensaje para registrar hoy
     * 3. Con progreso semanal → Consejo del día rotatorio (1 diferente cada día)
     *
     * @param user Perfil del usuario
     * @return Advice adaptado al estado del usuario
     */
    public Advice getAdvice(UserProfile user) {
        // Caso 1: Primer día sin gastos → Bienvenida especial
        if (user.getDayCount() == 1 && user.getWeekProgress() == 0) {
            return Advice.getWelcome(context);
        }
        // Caso 2: Sin gastos registrados esta semana → Animar a registrar
        else if (user.getWeekProgress() == 0) {
            return Advice.getRegisterExpenseToday(context);
        }
        // Caso 3: Usuario con actividad → Consejo del día rotatorio
        else {
            return AdviceManager.getDailyAdvice(context);
        }
    }

    public Advice getQuickAction(UserProfile user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            return Advice.getCompleteUserProfile(context);
        } else if (user.getMonthlyIncome() == 0) {
            return Advice.getCompleteBudget(context);
        } else {
            return Advice.getQuickAction(context);
        }
    }

    /**
     * Realiza todas las tareas de mantenimiento de una sola vez para minimizar refrescos de UI
     */
    public void performMaintenance() {
        executor.execute(() -> {
            UserProfile profile = userRepository.getUserProfileSync();
            if (profile == null) return;

            long now = System.currentTimeMillis();
            boolean changed = false;
            boolean weekReset = false;
            boolean dayIncremented = false;

            // 1. Comprobar reinicio semanal
            if (isNewWeek(profile.getLastResetTimestamp())) {
                profile.setWeekProgress(0);
                profile.setLastResetTimestamp(now);
                profile.setWeekCount(profile.getWeekCount() + 1);
                Log.d("UserController", "Mantenimiento: Progreso semanal reiniciado");
                changed = true;
                weekReset = true;
            } else {
                // 2. Sincronizar progreso si no ha habido reinicio
                int actualCount = expenseRepository.getExpensesThisWeekCount();
                if (profile.getWeekProgress() != actualCount) {
                    profile.setWeekProgress(actualCount);
                    Log.d("UserController", "Mantenimiento: Progreso sincronizado a " + actualCount);
                    changed = true;
                }
            }

            // 3. Comprobar contador de días
            if (isNewDay(profile.getLastConnectionTimestamp())) {
                profile.setDayCount(profile.getDayCount() + 1);
                profile.setLastConnectionTimestamp(now);
                Log.d("UserController", "Mantenimiento: Día incrementado");
                changed = true;
                dayIncremented = true;
            } else {
                // Actualizar última conexión de todos modos para que isNewDay funcione mañana
                profile.setLastConnectionTimestamp(now);
            }

            if (changed) {
                profile.setUpdatedAt(now);
                userRepository.updateProfile(profile, null);
            } else {
                Log.d("UserController", "Mantenimiento: Sin cambios necesarios");
            }
        });
    }

    /**
     * Sincroniza el contador weekProgress con los gastos reales
     * Solo se usa cuando sabemos que algo ha cambiado (ej. después de borrar un gasto)
     */
    public void syncWeekProgress() {
        executor.execute(() -> {
            int actualCount = expenseRepository.getExpensesThisWeekCount();
            UserProfile profile = userRepository.getUserProfileSync();
            if (profile != null && profile.getWeekProgress() != actualCount) {
                userRepository.updateWeekProgress(actualCount, null);
                Log.d("UserController", "Sincronizado progreso semanal: " + actualCount);
            }
        });
    }

    private boolean isNewWeek(long lastTimestamp) {
        Calendar lastSave = Calendar.getInstance();
        lastSave.setTimeInMillis(lastTimestamp);

        Calendar now = Calendar.getInstance();
        return now.get(Calendar.WEEK_OF_YEAR) != lastSave.get(Calendar.WEEK_OF_YEAR)
                || now.get(Calendar.YEAR) != lastSave.get(Calendar.YEAR);
    }

    private boolean isNewDay(long lastTimestamp) {
        Calendar lastSave = Calendar.getInstance();
        lastSave.setTimeInMillis(lastTimestamp);

        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_YEAR) != lastSave.get(Calendar.DAY_OF_YEAR)
                || now.get(Calendar.YEAR) != lastSave.get(Calendar.YEAR);
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


    public void setDummyData(UserProfile user) {
        if (user != null && user.getName() != null && user.getName().equals("Dummy") ){
            Log.d("User Controller setDummyData", "Setting dummy data");
            AppDatabase.dummyData();
        }
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }


}
