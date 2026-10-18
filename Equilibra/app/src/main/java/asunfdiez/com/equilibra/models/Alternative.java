package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
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
        foreignKeys = @ForeignKey(
                entity = ExpenseCategory.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"categoryId"}),
                @Index(value = {"suggestion"}, unique = true)
        })
public class Alternative {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    
    // ID de la categoría a la que se aplica la alternativa
    private long categoryId;
    
    // Palabra clave utilizada para identificar el gasto relacionado
    private String descriptionKeyword; 
    
    // La sugerencia de ahorro o comportamiento alternativo
    private String suggestion;
    
    // Porcentaje estimado que el usuario podría ahorrar al seguir esta sugerencia
    private double savingsPercentage;

    public Alternative(long categoryId, String descriptionKeyword, String suggestion, double savingsPercentage) {
        this.categoryId = categoryId;
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

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
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
