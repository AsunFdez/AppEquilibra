package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.repositories.SavingGoalRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;
import asunfdiez.com.equilibra.utils.AdviceManager;
import asunfdiez.com.equilibra.utils.DateUtils;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * Controlador encargado de la lógica de negocio del perfil de usuario.
 * 
 * <p>Gestiona el estado global del usuario, incluyendo el cálculo de su nivel 
 * de bienestar financiero, el seguimiento de sus metas de ahorro y la actualización 
 * de sus indicadores de progreso. Sirve como punto central para realizar cambios 
 * en el perfil y coordinar la persistencia de los mismos.</p>
 */
public class UserProfileController {

    private String TAG = "UserProfileController";
    
    // Repositorios para el acceso a datos relacionados con el usuario
    private final UserProfileRepository userRepository;
    private final SavingGoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;
    
    // Gestores de estado y lógica adicional
    private final SharedPrefsManager prefsManager;
    private UserProfile user;
    private final WellbeingController wellbeingController;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    Context context;


    /**
     * Constructor del controller
     * @param context Contexto de la aplicación
     */
    public UserProfileController(Context context) {
        this.userRepository = new UserProfileRepository(context);
        this.goalRepository = new SavingGoalRepository(context);
        this.expenseRepository = new ExpenseRepository(context);
        this.prefsManager = SharedPrefsManager.getInstance(context);
        this.wellbeingController = new WellbeingController(context);
        this.context = context;
    }

    /**
     * Obtiene el usuario actual
     *@return Usuario actual o null si no existe
     */
    public LiveData<UserProfile> getCurrentUser() {
        return userRepository.getUserProfile();
    }

    /**
     * Actualiza el usuario
     * @param user usuario con datos a actualizar
     * @param onComplete función que se ejecuta tras actualizar el usuario
     */
    public void updateUser(UserProfile user, @Nullable Runnable onComplete) {
        if (user == null) return;
        userRepository.updateProfile(user, () -> {
            Log.d(TAG, "Perfil guardado: " + user.toString());
            setDummyData(user); //solo para probar funciones concretas
            if (onComplete != null) {
                onComplete.run();
            }
        });

    }

    /**
     * Borra el usuario actual
     * @param onComplete función que se ejecuta tras borrar el usuario
     */
    public void deleteCurrentUser(Runnable onComplete) {
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getInstance(context);
            database.clearAllTables();
            prefsManager.resetAll();
            // Recrear datos estáticos (categorías, logros, alternativas) de forma síncrona en este hilo de fondo
            AppDatabase.insertInitialCategories();
            AppDatabase.insertInitialAchievements();
            AppDatabase.insertInitialAlternatives();
            Log.d(TAG, "Usuario borrado");
            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }

    /**
     * Comprueba el bienestar financiero del usuario
     * @return true si necesita una intervención, false en caso contrario
     */

    public boolean checkWellbeing() {
        AtomicBoolean shouldShow = new AtomicBoolean(false);
        executor.execute(() -> {
            try {
                shouldShow.set(wellbeingController.shouldShowIntervention());
            } catch (Exception e) {
                Log.e(TAG, "Error al comprobar bienestar", e);
            }
        });
        return shouldShow.get();
    }

    /**
     * Obtiene el consejo apropiado según el estado del usuario
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

    /**
     * Obtiene una acción rápida para el usuario
     * @param user Perfil del usuario
     * @return Advice con la acción
     */
    public Advice getQuickAction(UserProfile user) {
        // Caso 1: Usuario sin perfil → Completar perfil
        if (user.getName() == null || user.getName().isEmpty()) {
            return Advice.getCompleteUserProfile(context);
        }
        // Caso 2: Usuario sin ingreso mensual → Completar ingreso mensual
        else if (user.getMonthlyIncome() == 0) {
            return Advice.getCompleteBudget(context);
        }
        else {
            return Advice.getQuickAction(context);
        }
    }

    /**
     * Realiza todas las tareas de mantenimiento de una sola vez para minimizar refrescos de UI
     * 1. Comprobar reinicio semanal
     * 2. Sincronizar progreso si no ha habido reinicio
     * 3. Comprobar contador de días
     * 4. Actualizar última conexión de todos modos para que isNewDay funcione mañana
     * 5. Actualizar perfil en base de datos
     *
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
            if (DateUtils.isNewWeek(profile.getLastResetTimestamp())) {
                profile.setWeekProgress(0);
                profile.setLastResetTimestamp(now);
                profile.setWeekCount(profile.getWeekCount() + 1);
                Log.d(TAG, "Mantenimiento: Progreso semanal reiniciado");
                changed = true;
                weekReset = true;
            }
            // 2. Sincronizar progreso si no ha habido reinicio
            else {
                int actualCount = expenseRepository.getExpensesThisWeekCount();
                if (profile.getWeekProgress() != actualCount) {
                    profile.setWeekProgress(actualCount);
                    Log.d(TAG, "Mantenimiento: Progreso sincronizado a " + actualCount);
                    changed = true;
                }
            }
            // 3. Comprobar contador de días
            if (DateUtils.isNewDay(profile.getLastConnectionTimestamp())) {
                profile.setDayCount(profile.getDayCount() + 1);
                profile.setLastConnectionTimestamp(now);
                Log.d(TAG, "Mantenimiento: Día incrementado");
                changed = true;
                dayIncremented = true;
            }
            // Actualizar última conexión de todos modos para que isNewDay funcione mañana
            else {
                profile.setLastConnectionTimestamp(now);
            }
            // 4. Actualizar perfil en base de datos
            if (changed) {
                profile.setUpdatedAt(now);
                userRepository.updateProfile(profile, null);
            } else {
                Log.d(TAG, "Mantenimiento: Sin cambios necesarios");
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
                Log.d("UserProfileController", "Sincronizado progreso semanal: " + actualCount);
            }
        });
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
     * Crea datos dummy para pruebas solo si el nombre del usuario es Dummy
     * @param user perfil de usuario
     */
    public void setDummyData(UserProfile user) {
        if (user != null && user.getName() != null && user.getName().equals("Dummy") ){
            Log.d("User Controller setDummyData", "Setting dummy data");
            AppDatabase.dummyData();
        }
    }

    /**
     * Guarda el objetivo de ahorro
     * @param currentGoal Objetivo de ahorro
     * @param onComplete función a ejecutar tras guardar el objetivo
     */
    public void saveSavingGoal(SavingGoal currentGoal, @Nullable Runnable onComplete) {
        goalRepository.saveSavingGoal(currentGoal, onComplete);
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
