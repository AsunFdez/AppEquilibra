package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Alternative - Modelo de datos para alternativas de ahorro
 * Representa una sugerencia de ahorro basada en categoría y descripción
 */
@Entity(tableName = Constants.TABLE_ALTERNATIVES)
public class Alternative {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    private String category;
    private String descriptionKeyword; // Palabra clave en la descripción del gasto
    private String suggestion;
    private double savingsPercentage; // Porcentaje estimado de ahorro

    public Alternative(String category, String descriptionKeyword, String suggestion, double savingsPercentage) {
        this.category = category;
        this.descriptionKeyword = descriptionKeyword;
        this.suggestion = suggestion;
        this.savingsPercentage = savingsPercentage;
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

    public String getDescriptionKeyword() {
        return descriptionKeyword;
    }

    public void setDescriptionKeyword(String descriptionKeyword) {
        this.descriptionKeyword = descriptionKeyword;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public double getSavingsPercentage() {
        return savingsPercentage;
    }

    public void setSavingsPercentage(double savingsPercentage) {
        this.savingsPercentage = savingsPercentage;
    }
}
