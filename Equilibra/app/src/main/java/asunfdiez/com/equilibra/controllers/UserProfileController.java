package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
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
    private final StreakController streakController;

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
        this.streakController = new StreakController(context);
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
            
            // Una vez guardado el perfil, obtenemos el usuario síncronamente
            // para asegurar que tenemos el ID (si era nuevo) e inicializamos rachas.
            executor.execute(() -> {
                UserProfile savedUser = userRepository.getUserProfileSync();
                if (savedUser != null) {
                    streakController.initializeDefaultStreaks(savedUser.getId());
                }
                
                new Handler(Looper.getMainLooper()).post(() -> {
                    setDummyData(user); //solo para probar funciones concretas
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
            });
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
     * Obtiene una lista de acciones recomendadas para el usuario.
     * Siempre devuelve exactamente dos elementos: el primero para quickAction
     * y el segundo para nextGoal.
     */
    public List<Advice> getQuickAction(UserProfile user) {
        List<Advice> advices = new ArrayList<>();

        if (user == null) {
            advices.add(Advice.getWelcome(context));
            advices.add(Advice.getCompleteUserProfile(context));
            return advices;
        }

        // Caso 1: Usuario sin perfil → Acción: Completar perfil, Meta: Configurar presupuesto
        if (user.getName() == null || user.getName().isEmpty()) {
            advices.add(Advice.getCompleteUserProfile(context));
            advices.add(Advice.getCompleteBudget(context));
        }
        // Caso 2: Sin ingresos → Acción: Configurar presupuesto, Meta: Registrar primer gasto
        else if (user.getMonthlyIncome() <= 0) {
            advices.add(Advice.getCompleteBudget(context));
            advices.add(Advice.getRegisterExpenseToday(context));
        }
        // Caso 3: Usuario activo → Acción genérica, Meta: Seguir registrando
        else {
            advices.add(Advice.getQuickAction(context));
            advices.add(Advice.getRegisterExpenseToday(context));
        }

        // Garantizar que siempre haya exactamente dos elementos
        while (advices.size() < 2) {
            advices.add(Advice.getQuickAction(context));
        }
        return advices.subList(0, 2);
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

            // 0. Asegurar que las rachas existan (comprobación inicial)
            streakController.ensureStreaksExist(profile.getId());

            // 1. Comprobar reinicio semanal
            if (DateUtils.isNewWeek(profile.getLastResetTimestamp())) {
                profile.setWeekProgress(0);
                profile.setLastResetTimestamp(now);
                profile.setWeekCount(profile.getWeekCount() + 1);
                
                // Resetear rachas semanales
                streakController.resetWeeklyStreaks(profile.getId());
                
                Log.d(TAG, "Mantenimiento: Progreso semanal y rachas reiniciadas");
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
     * Crea datos dummy para pruebas solo si el nombre del usuario es Dummy
     * @param user perfil de usuario
     */
    public void setDummyData(UserProfile user) {
        if (user != null && user.getName() != null && user.getName().equals("Dummy") ){
            Log.d("User Controller setDummyData", "Setting dummy data");
            executor.execute(AppDatabase::dummyData);
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

    public UserProfile getUserProfileSync() {
        return userRepository.getUserProfileSync();
    }

    public SavingGoal getActiveSavingGoalSync(long id) {
        return goalRepository.getActiveSavingGoalSync(id);
    }

    public LiveData<String> getUserName() {
        return userRepository.getUserName();
    }
}
