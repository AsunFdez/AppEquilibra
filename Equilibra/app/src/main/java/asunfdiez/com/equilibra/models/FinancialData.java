package asunfdiez.com.equilibra.models;

// Clase para agrupar datos financieros
public class FinancialData {
    public final double income, expenses, percentage, topCategoryAmount;
    public final String topCategory;

    public FinancialData(double income, double expenses, double percentage, String topCategory, double topCategoryAmount) {
        this.income = income;
        this.expenses = expenses;
        this.percentage = percentage;
        this.topCategory = topCategory;
        this.topCategoryAmount = topCategoryAmount;
    }

}
