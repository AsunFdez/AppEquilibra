package asunfdiez.com.equilibra.models;

import java.util.List;

/**
 * Clase que encapsula los datos de un periodo concreto para el análisis comparativo.
 */
public class ComparativeAnalysisState {
    public final String periodLabel;
    public final double totalExpenses;
    public final double totalIncome;
    public final double balance;
    public final List<CategoryAnalysis> pieEntries;
    public final boolean hasData;

    public ComparativeAnalysisState(String periodLabel, double totalExpenses, double totalIncome, 
                                    double balance, List<CategoryAnalysis> pieEntries, boolean hasData) {
        this.periodLabel = periodLabel;
        this.totalExpenses = totalExpenses;
        this.totalIncome = totalIncome;
        this.balance = balance;
        this.pieEntries = pieEntries;
        this.hasData = hasData;
    }
}