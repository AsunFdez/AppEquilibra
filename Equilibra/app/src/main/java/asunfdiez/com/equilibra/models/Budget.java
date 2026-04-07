package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Budget - Modelo de datos para presupuesto
 * Representa el límite de gasto por cada categoría en un periodo determinado (mensual)
 */
@Entity (tableName = Constants.TABLE_BUDGETS)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private String category;
    private double amount;
    private String period;
    private int month;
    private int year;

    public Budget() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
