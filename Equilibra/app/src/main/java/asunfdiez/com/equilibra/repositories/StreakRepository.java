package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.StreakDao;
import asunfdiez.com.equilibra.models.Streak;

/**
 * Repositorio dedicado a la gestión y persistencia de las rachas de uso del usuario.
 *
 * <p>Se encarga de abstraer las operaciones de acceso a datos para las rachas,
 * permitiendo su consulta reactiva por tipo y asegurando que los contadores
 * de días consecutivos se almacenen correctamente en la base de datos local.</p>
 */
public class StreakRepository {

    private final Context context;

    // DAO para la gestión de las rachas en la base de datos Room
    private final StreakDao streakDAO;

    // Executor para realizar operaciones de actualización en segundo plano
    private final ExecutorService executorService;

    /**
     * Constructor que inicializa el repositorio de rachas.
     */
    public StreakRepository(@NonNull Context context) {
        this.context = context;
        this.streakDAO =  AppDatabase.getInstance(context).streakDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Recupera una racha específica filtrada por su tipo como un observable LiveData.
     * @param userId ID del usuario
     * @param type Tipo de racha (TYPE_RECORDING, TYPE_BUDGET)
     * @return LiveData con la racha
     */
    public LiveData<Streak> getStreakByType(long userId, String type) {
        return streakDAO.getStreakByType(userId, type);
    }

    /**
     * Obtener racha por tipo (sincrónico)
     * Usar en background threads
     * @param userId ID del usuario
     * @param type Tipo de racha
     * @return Streak o null
     */
    public Streak getStreakByTypeSync(long userId, String type) {
        return streakDAO.getStreakByTypeSync(userId, type);
    }

    /**
     * Insertar nueva racha
     * @param streak Racha a insertar
     * @return ID de la racha
     */
    public long insertStreak(@NonNull Streak streak) {
        return streakDAO.insertStreak(streak);
    }

    /**
     * Actualizar racha existente en background usando ExecutorService.
     * @param streak Racha a actualizar
     */
    public void updateStreak(@NonNull Streak streak) {
        executorService.execute(() -> streakDAO.updateStreak(streak));
    }

    /**
     * Eliminar racha en background usando ExecutorService.
     * @param streak Racha a eliminar
     */
    public void deleteStreak(@NonNull Streak streak) {
        executorService.execute(() -> streakDAO.deleteStreak(streak));
    }

    /**
     * Resetear racha del usuario en background.
     * @param userId ID del usuario
     * @param type Tipo de racha a resetear
     */
    public void resetStreak(long userId, String type) {
        executorService.execute(() -> {
            Streak streak = streakDAO.getStreakByTypeSync(userId, type);
            if (streak != null) {
                streak.reset();
                streakDAO.updateStreak(streak);
            }
        });
    }

    /**
     * Obtener todas las rachas del usuario
     * @param userId ID del usuario
     * @return LiveData con lista de rachas
     */
    public LiveData<java.util.List<Streak>> getUserStreaks(long userId) {
        return streakDAO.getUserStreaks(userId);
    }

    /**
     * Libera recursos del ExecutorService cuando ya no se necesite.
     * Se recomienda llamar desde onDestroy() de la Activity que use este repositorio.
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
