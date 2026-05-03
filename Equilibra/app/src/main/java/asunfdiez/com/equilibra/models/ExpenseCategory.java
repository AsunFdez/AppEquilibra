package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;

/**
 * Modelo de datos que representa una categoría de clasificación para los gastos.
 * 
 * <p>Define la identidad visual y nominal de una categoría, incluyendo su nombre, 
 * el icono asociado y un color distintivo. Se utiliza para agrupar transacciones 
 * y facilitar el análisis de patrones de consumo.</p>
 */
@Entity(tableName = Constants.TABLE_CATEGORIES,
        indices = {@androidx.room.Index(value = {"name"}, unique = true)})
public class ExpenseCategory {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    private long id;

    // Nombre de la categoría (ej. "Comida", "Transporte")
    private String name;
    
    // ID del recurso de icono (obsoleto si se usa iconName)
    private int iconResId;
    
    // Nombre del recurso drawable para el icono
    private String iconName; 
    
    // Código de color hexadecimal para la UI
    private String color;    
    
    // Estado de selección en la interfaz de usuario
    private boolean isSelected;

    public ExpenseCategory(String name, int iconResId, String iconName, String color) {
        this.name = name;
        this.iconResId = iconResId;
        this.iconName = iconName;
        this.color = color;
        this.isSelected = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpenseCategory category = (ExpenseCategory) o;

        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
