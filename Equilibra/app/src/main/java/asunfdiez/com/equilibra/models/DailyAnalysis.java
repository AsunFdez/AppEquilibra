package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * DailyAnalysis - Modelo de datos para análisis diario de gastos
 * Contiene resumen diario de transacciones y análisis de patrones
 */

@Entity(tableName = Constants.TABLE_DAILY_ANALYSIS)
public class DailyAnalysis {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private long userId;
    private String analysisDate; // Formato: yyyy-MM-dd
    private double totalSpent;
    private double totalIncome;
    private double netBalance;
    private String categoryBreakdown; // JSON con distribución por categorías
    private boolean anomalyDetected;

    // Constructor vacío
    public DailyAnalysis() {
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

    public String getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(String analysisDate) {
        this.analysisDate = analysisDate;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }

    public String getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(String categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public boolean isAnomalyDetected() {
        return anomalyDetected;
    }

    public void setAnomalyDetected(boolean anomalyDetected) {
        this.anomalyDetected = anomalyDetected;
    }

    /**
     * Recalcula el balance neto
     */
    public void recalculateBalance() {
        this.netBalance = totalIncome - totalSpent;
    }

    /**
     * Obtiene el JSON de distribución por categorías
     */
    public JSONObject getCategoryBreakdownAsJSON() {
        try {
            return new JSONObject(categoryBreakdown);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * Establece la distribución por categorías desde JSON
     */
    public void setCategoryBreakdownFromJSON(JSONObject json) {
        this.categoryBreakdown = json.toString();
    }

    /**
     * Calcula el porcentaje de gasto respecto al ingreso
     */
    public double getSpendingPercentage() {
        if (totalIncome <= 0) return 0;
        return (totalSpent / totalIncome) * 100;
    }

    @Override
    public String toString() {
        return "DailyAnalysis{" +
                "id=" + id +
                ", userId=" + userId +
                ", analysisDate='" + analysisDate + '\'' +
                ", totalSpent=" + totalSpent +
                ", totalIncome=" + totalIncome +
                ", netBalance=" + netBalance +
                ", anomalyDetected=" + anomalyDetected +
                '}';
    }
}
