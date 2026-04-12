// Nuevo archivo: models/AnalysisState.java
package asunfdiez.com.equilibra.models;

import java.util.List;

public class AnalysisState {
    public final double totalExpenses;
    public final double totalIncome;
    public final double savings;
    public final List<CategoryAnalysis> pieEntries;
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