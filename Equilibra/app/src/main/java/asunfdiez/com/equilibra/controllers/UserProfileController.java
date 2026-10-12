package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.CustomCredential;
import androidx.lifecycle.LiveData;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.CloudSyncRepository;
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
    private final CloudSyncController syncController;
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
        this.syncController = new CloudSyncController(context);
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
            // Recrear datos estáticos (categorías, logros, alternativas, rachas) de forma síncrona en este hilo de fondo
            AppDatabase.insertInitialCategories();
            AppDatabase.insertInitialAchievements();
            AppDatabase.insertInitialAlternatives();
            AppDatabase.insertInitialStreaks();
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
        else if (user.getMonthlyIncome() == null || user.getMonthlyIncome() <= 0) {
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

    public LiveData<Boolean> hasProfile() {
        return userRepository.hasProfile();
    }

    // --- Métodos de Autenticación y Sincronización en la Nube ---


    /**
     * Indica si el usuario está autenticado en Firebase.
     */
    public boolean isFirebaseAuthenticated() {
        return syncController.isFirebaseAuthenticated();
    }

    /**
     * Obtiene el email del usuario conectado en Firebase.
     */
    public String getFirebaseUserEmail() {
        return syncController.getFirebaseUserEmail();
    }

    /**
     * Inicia el proceso de login en Firebase con un token de Google.
     */
    public void loginToFirebase(String idToken, CloudSyncRepository.OnAuthCompleteListener listener) {
        syncController.login(idToken, new CloudSyncRepository.OnAuthCompleteListener() {
            @Override
            public void onSuccess(String uid) {
                syncController.syncNow();
                if (listener != null) listener.onSuccess(uid);
            }

            @Override
            public void onFailure(Exception e) {
                if (listener != null) listener.onFailure(e);
            }
        });
    }

    /**
     * Cierra la sesión de Firebase.
     */
    public void logoutFromFirebase() {
        syncController.logout();
    }

    /**
     * Sincroniza los datos actuales con la nube.
     */
    public void syncNow() {
        syncNow(null);
    }

    /**
     * Sincroniza los datos actuales con la nube con un callback.
     * @param callback Callback que se ejecutará al finalizar la sincronización.
     */
    public void syncNow(CloudSyncRepository.OnSyncCompleteListener callback) {
        if (syncController.isFirebaseAuthenticated()) {
            syncController.syncNow(callback);
        } else {
            Log.d(TAG, "No se puede sincronizar sin estar autenticado en Firebase");
            if (callback != null) callback.onComplete(false);
        }
    }

    // --- Métodos de Google Sign-In Unificados ---

    /**
     * Crea la petición para obtener el token de identidad de Google.
     * @return Petición configurada para Credential Manager
     */
    public GetCredentialRequest getGoogleIdTokenRequest() {
        GetGoogleIdOption googleIdTokenOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(asunfdiez.com.equilibra.R.string.default_web_client_id))
                .build();

        return new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdTokenOption)
                .build();
    }

    /**
     * Procesa la respuesta de Credential Manager y realiza el login en Firebase.
     * @param result Resultado obtenido de Credential Manager
     * @param listener Callback para notificar el resultado del login
     */
    public void handleGoogleIdTokenResponse(GetCredentialResponse result, CloudSyncRepository.OnAuthCompleteListener listener) {
        if (result.getCredential() instanceof CustomCredential &&
                result.getCredential().getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            try {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                String idToken = googleIdTokenCredential.getIdToken();
                loginToFirebase(idToken, listener);
            } catch (Exception e) {
                Log.e(TAG, "Error al extraer token de Google", e);
                if (listener != null) listener.onFailure(e);
            }
        } else {
            Log.e(TAG, "Tipo de credencial no esperado");
            if (listener != null) listener.onFailure(new Exception("Invalid credential type"));
        }
    }

    /**
     * Obtiene los datos de perfil del usuario de Google conectado.
     * @return Un objeto UserProfile con los datos de Google o null si no está conectado.
     */
    public UserProfile getGoogleAccountData() {
        com.google.firebase.auth.FirebaseUser firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            UserProfile googleProfile = new UserProfile();
            googleProfile.setName(firebaseUser.getDisplayName());
            googleProfile.setEmail(firebaseUser.getEmail());
            if (firebaseUser.getPhotoUrl() != null) {
                googleProfile.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
            }
            return googleProfile;
        }
        return null;
    }
}
