package asunfdiez.com.equilibra.repositories;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.Streak;

/**
 * StreakRepository - Repositorio para gestión de rachas
 *
 * Responsabilidades:
 * - Acceso a datos de rachas desde persistencia
 * - Crear/actualizar/eliminar rachas
 * - Queries por tipo de racha
 *
 * Patrón:
 * - Repository pattern
 * - Abstrae la fuente de datos (Room/SQLite)
 * - Operaciones en background threads
 */
public class StreakRepository {

    private final Context context;
    private final asunfdiez.com.equilibra.database.StreakDao streakDAO;

    /**
     * Constructor
     */
    public StreakRepository(@NonNull Application application) {
        this.context = application.getApplicationContext();
        this.streakDAO =  AppDatabase.getInstance(context).streakDao();
    }

    /**
     * Obtener racha por tipo (LiveData reactivo)
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
     * Actualizar racha existente
     * @param streak Racha a actualizar
     */
    public void updateStreak(@NonNull Streak streak) {
        new Thread(() -> streakDAO.updateStreak(streak)).start();
    }

    /**
     * Eliminar racha
     * @param streak Racha a eliminar
     */
    public void deleteStreak(@NonNull Streak streak) {
        new Thread(() -> streakDAO.deleteStreak(streak)).start();
    }

    /**
     * Resetear racha del usuario
     * @param userId ID del usuario
     * @param type Tipo de racha a resetear
     */
    public void resetStreak(long userId, String type) {
        new Thread(() -> {
            Streak streak = streakDAO.getStreakByTypeSync(userId, type);
            if (streak != null) {
                streak.reset();
                streakDAO.updateStreak(streak);
            }
        }).start();
    }

    /**
     * Obtener todas las rachas del usuario
     * @param userId ID del usuario
     * @return LiveData con lista de rachas
     */
    public LiveData<java.util.List<Streak>> getUserStreaks(long userId) {
        return streakDAO.getUserStreaks(userId);
    }
}
