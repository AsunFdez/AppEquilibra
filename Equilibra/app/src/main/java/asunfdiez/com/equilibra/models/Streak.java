package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Streak - Modelo de datos para racha de uso consecutivo
 * Contabiliza los días consecutivos de interacción con la app
 */
@Entity(tableName = Constants.TABLE_STREAK)
public class Streak {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    private long id;
    private long userId;
    private int consecutiveDays;
    private String startDate; // Formato: yyyy-MM-dd
    private String lastActivityDate; // Formato: yyyy-MM-dd
    private int maxStreakRecord;
    private boolean isActive;

    // Formateador de fechas
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor vacío
    public Streak() {
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getConsecutiveDays() {
        return consecutiveDays;
    }

    public void setConsecutiveDays(int consecutiveDays) {
        this.consecutiveDays = consecutiveDays;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public int getMaxStreakRecord() {
        return maxStreakRecord;
    }

    public void setMaxStreakRecord(int maxStreakRecord) {
        this.maxStreakRecord = maxStreakRecord;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Registra una actividad y actualiza la racha
     */
    public void recordActivity() {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String yesterday = LocalDate.now().minusDays(1).format(DATE_FORMATTER);

        if (!isActive) {
            // Iniciar nueva racha
            startDate = today;
            consecutiveDays = 1;
            isActive = true;
        } else if (lastActivityDate.equals(yesterday)) {
            // Continuar racha
            consecutiveDays++;
        } else if (!lastActivityDate.equals(today)) {
            // Día saltado, reiniciar racha
            startDate = today;
            consecutiveDays = 1;
        }

        lastActivityDate = today;

        // Actualizar récord
        if (consecutiveDays > maxStreakRecord) {
            maxStreakRecord = consecutiveDays;
        }
    }

    /**
     * Verifica si la racha se ha roto
     */
    public void checkStreakBreak() {
        String yesterday = LocalDate.now().minusDays(1).format(DATE_FORMATTER);

        if (isActive && !lastActivityDate.equals(today()) && !lastActivityDate.equals(yesterday)) {
            isActive = false;
            consecutiveDays = 0;
        }
    }

    /**
     * Obtiene la fecha de hoy en formato yyyy-MM-dd
     */
    private static String today() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    @Override
    public String toString() {
        return "Streak{" +
                "id=" + id +
                ", userId=" + userId +
                ", consecutiveDays=" + consecutiveDays +
                ", maxRecord=" + maxStreakRecord +
                ", isActive=" + isActive +
                '}';
    }
}
