package asunfdiez.com.equilibra.models;

/**
 * ExpenseCategory - Modelo de datos para categorías de gastos
 * Representa una categoría predefinida para clasificar transacciones
 */
public class ExpenseCategory {

    private String name;
    private int iconResId;
    private String iconName; // Nombre del drawable
    private String color;    // Color en formato hexadecimal #RRGGBB
    private boolean isSelected;

    public ExpenseCategory(String name, int iconResId, String iconName) {
        this.name = name;
        this.iconResId = iconResId;
        this.iconName = iconName;
        this.isSelected = false;
    }

    public ExpenseCategory(String name, int iconResId, String iconName, String color) {
        this.name = name;
        this.iconResId = iconResId;
        this.iconName = iconName;
        this.color = color;
        this.isSelected = false;
    }


    // Constructor vacío
    public ExpenseCategory() {
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
