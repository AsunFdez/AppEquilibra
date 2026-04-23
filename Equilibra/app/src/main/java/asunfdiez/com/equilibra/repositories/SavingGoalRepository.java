package asunfdiez.com.equilibra.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.SavingGoalDao;
import asunfdiez.com.equilibra.models.SavingGoal;

/**
 * Repository para gestionar el objetivo de ahorro
 * Intermedia entre ViewModel y Room DAOs
 */
public class SavingGoalRepository {
    private final SavingGoalDao savingGoalDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    /**
     * Constructor del repositorio
     * @param context Contexto de la aplicación
     */
    public SavingGoalRepository(Context context) {
        this.savingGoalDao = AppDatabase.getInstance(context).savingGoalDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Obtiene el primer objetivo de ahorro activo para un usuario
     * @param userId Id del usuario
     * @return Objetivo de ahorro activo
     */
    public SavingGoal getActiveSavingGoalSync(long userId) {
        return savingGoalDao.getFirstSavingGoalActiveByUserId(userId);
    }


    /**
     * Guarda o actualiza un objetivo de ahorro en la base de datos
     * @param savingGoal Objetivo de ahorro a guardar
     * @param onComplete Callback a ejecutar después de guardar
     */
    public void saveSavingGoal(SavingGoal savingGoal, @Nullable Runnable onComplete) {
        executorService.execute(() -> {
            savingGoalDao.insertOrUpdateSavingGoal(savingGoal);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    /**
     * Cierra el executor service
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}
