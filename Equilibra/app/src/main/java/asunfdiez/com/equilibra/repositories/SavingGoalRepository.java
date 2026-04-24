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
 * Repositorio encargado de gestionar las metas de ahorro del usuario.
 * 
 * <p>Facilita el acceso y la persistencia de los objetivos financieros, 
 * permitiendo consultas síncronas y asíncronas. Coordina con el DAO 
 * correspondiente para realizar operaciones de actualización de progreso 
 * y gestión del estado de cumplimiento de las metas.</p>
 */
public class SavingGoalRepository {
    // DAO para la ejecución de consultas sobre objetivos de ahorro
    private final SavingGoalDao savingGoalDao;
    
    // Executor para realizar operaciones en segundo plano
    private final ExecutorService executorService;
    
    // Handler para reportar resultados al hilo principal de la UI
    private final Handler mainHandler;

    /**
     * Constructor del repositorio que inicializa el acceso a datos.
     * @param context Contexto de la aplicación.
     */
    public SavingGoalRepository(Context context) {
        this.savingGoalDao = AppDatabase.getInstance(context).savingGoalDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Recupera de forma síncrona el primer objetivo de ahorro activo de un usuario.
     * @param userId Identificador único del usuario.
     * @return El objetivo de ahorro encontrado o null si no existe ninguno activo.
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
