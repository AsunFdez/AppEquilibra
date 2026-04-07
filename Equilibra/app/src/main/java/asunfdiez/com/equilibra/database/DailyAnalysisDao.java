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

@Dao
public interface DailyAnalysisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDailyAnalysis(DailyAnalysis dailyAnalysis);

    @Update
    void updateDailyAnalysis(DailyAnalysis dailyAnalysis);

    @Delete
    void deleteDailyAnalysis(DailyAnalysis dailyAnalysis);


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
