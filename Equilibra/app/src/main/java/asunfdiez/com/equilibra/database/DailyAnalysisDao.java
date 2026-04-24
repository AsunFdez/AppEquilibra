package asunfdiez.com.equilibra.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import asunfdiez.com.equilibra.models.DailyAnalysis;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * DAO para gestionar la persistencia de los análisis financieros diarios.
 * 
 * <p>Almacena los resúmenes consolidados de cada día, facilitando la recuperación 
 * histórica de balances, desglose de categorías y detección de anomalías 
 * sin necesidad de recalcular toda la información de transacciones individuales.</p>
 */
@Dao
public interface DailyAnalysisDao {

    /**
     * Guarda un registro de análisis diario. Si ya existe uno para la misma fecha (ID), lo sobrescribe.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDailyAnalysis(DailyAnalysis dailyAnalysis);

    @Update
    void updateDailyAnalysis(DailyAnalysis dailyAnalysis);

    @Delete
    void deleteDailyAnalysis(DailyAnalysis dailyAnalysis);

    @Query("DELETE FROM " + Constants.TABLE_DAILY_ANALYSIS)
    void deleteAll();

    @Query("SELECT * FROM "+ Constants.TABLE_DAILY_ANALYSIS + " WHERE "+Constants.COLUMN_DAILY_ANALYSIS_USER_ID +" = :userId")
    List<DailyAnalysis> getAllDailyAnalysisByUserId(long userId);

    /**
     * Obtiene el análisis diario de una fecha específica
     */
    @Query("SELECT * FROM "+ Constants.TABLE_DAILY_ANALYSIS + " WHERE "+Constants.COLUMN_DAILY_ANALYSIS_USER_ID +" = :userId AND "+ Constants.COLUMN_DAILY_ANALYSIS_DATE +"= :date")
    public DailyAnalysis getDailyAnalysis(long userId, String date);


    /**
     * Obtiene los análisis de un rango de fechas
     */
    @Query("SELECT * FROM "+ Constants.TABLE_DAILY_ANALYSIS + " WHERE "+Constants.COLUMN_DAILY_ANALYSIS_USER_ID +" = :userId AND "+ Constants.COLUMN_DAILY_ANALYSIS_DATE +" BETWEEN :startDate AND :endDate")
    public List<DailyAnalysis> getDailyAnalysisByRange(long userId, String startDate, String endDate);


    }
