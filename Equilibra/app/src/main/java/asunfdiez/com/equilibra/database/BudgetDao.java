package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import asunfdiez.com.equilibra.models.Budget;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para gestionar la persistencia de los presupuestos configurados.
 * 
 * <p>Proporciona acceso a las operaciones básicas de creación y actualización 
 * de presupuestos, así como consultas para recuperar el presupuesto activo 
 * que rige los límites de gasto actuales.</p>
 */
@Dao
public interface BudgetDao {

    /**
     * Inserta un nuevo registro de presupuesto. Si ya existe uno con el mismo ID, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Budget budget);

    /**
     * Actualiza un presupuesto existente
     *
     * @return
     */
    @Update
    int update(Budget budget);

    /**
     * Obtiene el presupuesto activo si existe (solo debe haber uno) o el último editado si no hay activo.
     */
    @Query("SELECT * FROM " + Constants.TABLE_BUDGETS +
            " ORDER BY enabled DESC, updatedAt DESC LIMIT 1")
    LiveData<Budget> getActiveOrLastBudget();

    /**
     * Obtiene el presupuesto activo de forma síncrona
     */
    @Query("SELECT * FROM " + Constants.TABLE_BUDGETS +
            " WHERE enabled = 1 LIMIT 1")
    Budget getActiveBudgetSync();

    @Query("SELECT * FROM " + Constants.TABLE_BUDGETS + " WHERE id = :id")
    Budget getBudgetById(long id);

    /**
     * Obtiene el último presupuesto editado (sea activo o no)
     */
    @Query("SELECT * FROM " + Constants.TABLE_BUDGETS + " ORDER BY updatedAt DESC LIMIT 1")
    Budget getAnyBudgetSync();

    /**
     * Obtiene todos los presupuestos
     */
    @Query("SELECT * FROM " + Constants.TABLE_BUDGETS +
            " ORDER BY updatedAt DESC")
    LiveData<java.util.List<Budget>> getAllBudgets();


    /**
     * Desactiva todos los presupuestos
     */
    @Query("UPDATE " + Constants.TABLE_BUDGETS + " SET enabled = 0")
    void deactivateAll();

    /**
     * Elimina todos los presupuestos
     */
    @Query("DELETE FROM " + Constants.TABLE_BUDGETS)
    void deleteAll();

    /**
     * Cuenta los presupuestos activos
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_BUDGETS +
            " WHERE enabled = 1")
    int countActive();

    /**
     * Cuenta los presupuestos
     */
    @Query("SELECT COUNT(*) FROM " + Constants.TABLE_BUDGETS )
    int countBudgets();

}
