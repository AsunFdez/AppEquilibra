package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.UserProfileDao;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.workers.AutoIncomeWorker;

/**
 * Repositorio central para la gestión del perfil de usuario único.
 * 
 * <p>Proporciona acceso tanto síncrono como reactivo (LiveData) a la información 
 * del perfil del usuario. Maneja la persistencia de cambios en los datos personales 
 * y preferencias de configuración mediante hilos secundarios para no bloquear la UI.</p>
 */
public class UserProfileRepository {
    // DAO para la gestión de la entidad de perfil de usuario
    private final UserProfileDao userProfileDao;
    
    private Context context;
    
    // Executor para realizar actualizaciones en segundo plano
    private final ExecutorService executorService;
    
    // Handler para comunicación con el hilo principal
    private final Handler mainHandler;

    private final CloudSyncRepository syncRepository;
    
    public UserProfileRepository(Context context) {
        this.context = context;
        this.userProfileDao = AppDatabase.getInstance(context).userProfileDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.syncRepository = new CloudSyncRepository(context);
    }

    /**
     * Recupera el perfil del usuario como un objeto observable LiveData.
     */
    public LiveData<UserProfile> getUserProfile() {
        return userProfileDao.getUserProfile();
    }

    /**
     * Recupera el perfil del usuario de forma síncrona (bloqueante).
     */
    public UserProfile getUserProfileSync() {
        return userProfileDao.getUserProfileSync();
    }

    public LiveData<String> getUserName() {
        return userProfileDao.getUserName();
    }


    public void updateProfile(UserProfile profile, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.insertOrUpdate(profile);
            syncRepository.scheduleImmediateSync();
            
            // Programar el ingreso automático si está configurado
            if (profile.getMonthlyIncome() != null && profile.getIncomeDay() != null) {
                scheduleAutoIncomeWorker();
            } else {
                cancelAutoIncomeWorker();
            }
            
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    /**
     * Programa la tarea periódica para el ingreso automático.
     */
    private void scheduleAutoIncomeWorker() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                AutoIncomeWorker.class, 1, TimeUnit.DAYS)
                .addTag("auto_income_work")
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "auto_income_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    /**
     * Cancela la tarea periódica del ingreso automático.
     */
    private void cancelAutoIncomeWorker() {
        WorkManager.getInstance(context).cancelUniqueWork("auto_income_work");
    }



    public void deleteAll(@Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.deleteAll();
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void incrementDayCount (@Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.incrementDayCount();
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void updateLastConnection(long timestamp, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.updateLastConnection(timestamp);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void resetWeeklyProgress(long timestamp, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.resetWeeklyProgress(timestamp);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void updateWeekProgress(int progress, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            userProfileDao.updateWeekProgress(progress);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }


    public int getWeeksUnderBudget() {
        return userProfileDao.getWeeksUnderBudget();
    }


    public void decrementWeekProgress() {
        executorService.execute(userProfileDao::decrementWeekProgress);
    }

    public void incrementWeekProgress() {
        executorService.execute(userProfileDao::incrementWeekProgress);
    }


    public double getUserMonthlyIncome() {
        return userProfileDao.getUserMonthlyIncome();
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public LiveData<Boolean> hasProfile() {
        return userProfileDao.hasProfile();
    }
}

