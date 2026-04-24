// Nuevo archivo: models/AnalysisState.java
package asunfdiez.com.equilibra.models;

import java.util.List;

/**
 * Clase que encapsula el estado consolidado de los datos de análisis financiero.
 * 
 * <p>Esta clase se utiliza para agrupar los resultados de los cálculos estadísticos, 
 * incluyendo totales de gastos, ingresos, ahorros y los datos estructurados necesarios 
 * para la representación en gráficos (pie y bar).</p>
 */
public class AnalysisState {
    // Total acumulado de gastos en el periodo analizado
    public final double totalExpenses;
    
    // Total acumulado de ingresos (presupuesto)
    public final double totalIncome;
    
    // Diferencia entre ingresos y gastos
    public final double savings;
    
    // Datos para el gráfico circular por categoría
    public final List<CategoryAnalysis> pieEntries;
    
    // Valores numéricos para el gráfico de barras comparativo
    public final double[] barValues;

    public AnalysisState(double totalExpenses, double totalIncome, double savings,
                         List<CategoryAnalysis> pieEntries, double[] barValues) {
        this.totalExpenses = totalExpenses;
        this.totalIncome = totalIncome;
        this.savings = savings;
        this.pieEntries = pieEntries;
        this.barValues = barValues;
    }
}