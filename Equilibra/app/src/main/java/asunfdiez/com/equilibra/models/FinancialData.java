package asunfdiez.com.equilibra.models;

/**
 * Clase que agrupa los indicadores clave del desempeño financiero del usuario.
 * 
 * <p>Contiene métricas como ingresos totales, gastos acumulados, el porcentaje 
 * de uso del presupuesto y detalles sobre la categoría con mayor gasto. Se utiliza 
 * para alimentar los paneles informativos y las intervenciones.</p>
 */
public class FinancialData {
    // Ingresos o presupuesto total disponible
    public final double income;
    
    // Gastos totales registrados
    public final double expenses;
    
    // Porcentaje del presupuesto consumido
    public final double percentage;
    
    // Monto gastado en la categoría principal
    public final double topCategoryAmount;
    
    // Nombre de la categoría donde más se ha gastado
    public final String topCategory;

    public FinancialData(double income, double expenses, double percentage, String topCategory, double topCategoryAmount) {
        this.income = income;
        this.expenses = expenses;
        this.percentage = percentage;
        this.topCategory = topCategory;
        this.topCategoryAmount = topCategoryAmount;
    }

}
