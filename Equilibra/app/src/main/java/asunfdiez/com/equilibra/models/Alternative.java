package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que representa una alternativa de ahorro sugerida.
 * 
 * <p>Contiene sugerencias basadas en categorías de gastos y palabras clave encontradas 
 * en las descripciones de los gastos del usuario. Proporciona una estimación de ahorro 
 * potencial mediante un porcentaje.</p>
 */
@Entity(tableName = Constants.TABLE_ALTERNATIVES,
        indices = {@androidx.room.Index(value = {"suggestion"}, unique = true)})
public class Alternative {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    
    // Categoría de gasto a la que se aplica la alternativa
    private String category;
    
    // Palabra clave utilizada para identificar el gasto relacionado
    private String descriptionKeyword; 
    
    // La sugerencia de ahorro o comportamiento alternativo
    private String suggestion;
    
    // Porcentaje estimado que el usuario podría ahorrar al seguir esta sugerencia
    private double savingsPercentage;

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
