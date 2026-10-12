package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;
import asunfdiez.com.equilibra.workers.CloudSyncWorker;

/**
 * Repositorio para la sincronización de datos con Firebase Cloud Firestore.
 * 
 * <p>Maneja la subida de los datos locales de SQLite a la nube como una copia de seguridad 
 * sincronizada. Utiliza Firestore para almacenar las entidades bajo el UID del usuario 
 * autenticado.</p>
 */
public class CloudSyncRepository {
    private static final String TAG = "CloudSyncRepository";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context context;

    public CloudSyncRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Programa una tarea de sincronización inmediata.
     */
    public void scheduleImmediateSync() {
        scheduleImmediateSync(null);
    }

    /**
     * Programa una tarea de sincronización inmediata con un callback.
     * @param callback Callback que se ejecutará al finalizar la tarea.
     */
    public void scheduleImmediateSync(OnSyncCompleteListener callback) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(CloudSyncWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build();

        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueue(syncRequest);

        if (callback != null) {
            workManager.getWorkInfoByIdLiveData(syncRequest.getId())
                    .observeForever(new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                callback.onComplete(workInfo.getState() == WorkInfo.State.SUCCEEDED);
                                workManager.getWorkInfoByIdLiveData(syncRequest.getId()).removeObserver(this);
                            }
                        }
                    });
        }
    }

    /**
     * Asegura que el usuario esté autenticado antes de realizar la sincronización.
     */
    public void ensureAuthenticated(OnAuthCompleteListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            listener.onSuccess(currentUser.getUid());
        } else {
            Log.w(TAG, "ensureAuthenticated: No hay usuario autenticado.");
            listener.onFailure(new Exception("Usuario no autenticado"));
        }
    }

    /**
     * Sincroniza todos los datos locales con la nube.
     */
    public void syncAll(String uid) {
        // Sincronizar Gastos
        syncExpensesSync(uid);
        
        // Sincronizar Perfil
        UserProfile profile = AppDatabase.getInstance(context).userProfileDao().getUserProfileSync();
        if (profile != null) {
            syncUserProfileSync(uid, profile);
        }
        
        // Sincronizar Presupuestos
        syncBudgetsSync(uid);
        
        // Sincronizar Rachas
        syncStreaksSync(uid);
    }

    /**
     * Sincroniza todos los gastos locales con la nube.
     */
    public void syncExpenses(String uid) {
        AppDatabase.getInstance(context).expenseDao().getAllExpensesAsync().observeForever(expenses -> {
            if (expenses != null) {
                Executors.newSingleThreadExecutor().execute(() -> uploadExpenses(uid, expenses));
            }
        });
    }

    // Versión síncrona para ser llamada desde hilos secundarios o Workers
    public void syncExpensesSync(String uid) {
        List<Expense> expenses = AppDatabase.getInstance(context).expenseDao().getAllExpenses();
        if (expenses != null && !expenses.isEmpty()) {
            uploadExpenses(uid, expenses);
        }
    }

    private void uploadExpenses(String uid, List<Expense> expenses) {
        WriteBatch batch = db.batch();
        for (Expense expense : expenses) {
            batch.set(db.collection("users").document(uid)
                    .collection("expenses").document(String.valueOf(expense.getId())), expense);
        }
        batch.commit().addOnSuccessListener(aVoid -> Log.d(TAG, "Gastos sincronizados correctamente"))
                .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar gastos", e));
    }

    /**
     * Sincroniza los presupuestos locales con la nube.
     */
    public void syncBudgetsSync(String uid) {
        List<Budget> budgets = AppDatabase.getInstance(context).budgetDao().getAllBudgetsSync();
        if (budgets != null && !budgets.isEmpty()) {
            WriteBatch batch = db.batch();
            for (Budget budget : budgets) {
                batch.set(db.collection("users").document(uid)
                        .collection("budgets").document(String.valueOf(budget.getId())), budget);
            }
            batch.commit().addOnSuccessListener(aVoid -> Log.d(TAG, "Presupuestos sincronizados correctamente"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar presupuestos", e));
        }
    }

    /**
     * Sincroniza las rachas locales con la nube.
     */
    public void syncStreaksSync(String uid) {
        List<Streak> streaks = AppDatabase.getInstance(context).streakDao().getAllStreaks();
        if (streaks != null && !streaks.isEmpty()) {
            WriteBatch batch = db.batch();
            for (Streak streak : streaks) {
                batch.set(db.collection("users").document(uid)
                        .collection("streaks").document(String.valueOf(streak.getId())), streak);
            }
            batch.commit().addOnSuccessListener(aVoid -> Log.d(TAG, "Rachas sincronizadas correctamente"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar rachas", e));
        }
    }

    /**
     * Sincroniza el perfil de usuario.
     */
    public void syncUserProfile(String uid, UserProfile profile) {
        if (profile == null) return;
        db.collection("users").document(uid).set(profile)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Perfil sincronizado");
                    SharedPrefsManager.getInstance(context).setLastSyncTime(System.currentTimeMillis());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar perfil", e));
    }

    /**
     * Versión síncrona de sincronización de perfil (sin LiveData/Async manual en la llamada).
     */
    public void syncUserProfileSync(String uid, UserProfile profile) {
        if (profile == null) return;
        db.collection("users").document(uid).set(profile)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Perfil sincronizado correctamente"))
                .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar perfil", e));
    }

    /**
     * Inicia sesión en Firebase usando un token de ID de Google.
     */
    public void loginWithGoogle(String idToken, OnAuthCompleteListener listener) {
        Log.d(TAG, "loginWithGoogle: Iniciando con token");
        com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null);
        
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful() && auth.getCurrentUser() != null) {
                Log.d(TAG, "loginWithGoogle: Éxito en Firebase. UID: " + auth.getCurrentUser().getUid());
                listener.onSuccess(auth.getCurrentUser().getUid());
            } else {
                Log.e(TAG, "loginWithGoogle: Error en Firebase", task.getException());
                listener.onFailure(task.getException());
            }
        });
    }

    /**
     * Cierra la sesión actual.
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * Obtiene el email del usuario autenticado actual.
     */
    public String getCurrentUserEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }


    /**
     * Verifica si el usuario está autenticado.
     */
    public boolean isAuthenticated() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null;
    }

    public interface OnAuthCompleteListener {
        void onSuccess(String uid);
        void onFailure(Exception e);
    }

    public interface OnSyncCompleteListener {
        void onComplete(boolean success);
    }
}
