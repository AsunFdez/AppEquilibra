package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.controllers.WellbeingController;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.FinancialData;
import asunfdiez.com.equilibra.models.InterventionMessage;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;

/**
 * ViewModel para análisis de intervención suave en hábitos de gasto.
 *
 * <p>Proporciona análisis detallado del comportamiento financiero del usuario,
 * patrones de gasto, recomendaciones personalizadas y puntos de intervención.</p>
 */
public class SoftInterventionViewModel extends AndroidViewModel {

    private final ExpenseController expenseController;
    private final WellbeingController wellbeingController;

    // Datos financieros principales
    private final MutableLiveData<FinancialData> financialData = new MutableLiveData<>();
    private final MutableLiveData<InterventionMessage> interventionMessage = new MutableLiveData<>();

    // Análisis avanzado
    private final MutableLiveData<List<CategoryAnalysis>> categoryAnalysis = new MutableLiveData<>();
    private final MutableLiveData<String> spendingTrend = new MutableLiveData<>();
    private final MutableLiveData<List<String>> recommendations = new MutableLiveData<>();

    // Estado de análisis
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Métricas de comportamiento
    private final MutableLiveData<Double> savingsRate = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> riskLevel = new MutableLiveData<>(0); // 1-5 (1=bajo, 5=crítico)
    private final MutableLiveData<String> riskDescription = new MutableLiveData<>();

    public SoftInterventionViewModel(@NonNull Application application) {
        super(application);
        expenseController = new ExpenseController(application);
        wellbeingController = new WellbeingController(application);
        loadData();
    }

    /**
     * Carga y analiza todos los datos financieros y de comportamiento
     */
    public void loadData() {
        isLoading.setValue(true);

        new Thread(() -> {
            try {
                // 1. Cargar datos financieros básicos
                loadBasicFinancialData();

                // 2. Analizar categorías
                analyzeCategories();

                // 3. Detectar tendencias
                detectSpendingTrends();

                // 4. Calcular tasa de ahorro
                calculateSavingsRate();

                // 5. Evaluar nivel de riesgo
                evaluateRiskLevel();

                // 6. Generar recomendaciones
                generateRecommendations();

                // 7. Obtener mensaje de intervención
                InterventionMessage message = wellbeingController.getInterventionMessage();
                interventionMessage.postValue(message);

            } catch (Exception e) {
                Log.e("SoftInterventionViewModel", "Error cargando datos: " + e.getMessage());
                errorMessage.postValue("Error al cargar datos de análisis");
            } finally {
                isLoading.postValue(false);
            }
        }).start();
    }

    /**
     * Carga datos financieros básicos del mes
     */
    private void loadBasicFinancialData() {
        double income = expenseController.getTotalIncomeThisMonth();
        double expenses = expenseController.getTotalExpensesThisMonth();
        double percentage = (income > 0) ? (expenses / income) * 100 : 0;
        long topCategoryId = expenseController.getTopExpenseCategoryThisMonth();
        double topCategoryAmount = 0;

        if (topCategoryId != 0) {
            topCategoryAmount = expenseController.getCategoryExpensesThisMonth(topCategoryId);
        }

        FinancialData data = new FinancialData(
                income,
                expenses,
                percentage,
                String.valueOf(topCategoryId),
                topCategoryAmount
        );
        financialData.postValue(data);
    }

    /**
     * Analiza el gasto por categorías y detecta patrones
     */
    private void analyzeCategories() {
        try {
            List<CategoryAnalysis> analyses = new ArrayList<>();
            List<ExpenseWithCategory> expenses = expenseController.getExpensesThisMonth();

            if (expenses == null || expenses.isEmpty()) {
                categoryAnalysis.postValue(analyses);
                return;
            }

            // Agrupar gastos por categoría
            java.util.Map<Long, List<ExpenseWithCategory>> groupedByCategory = new java.util.HashMap<>();
            for (ExpenseWithCategory expense : expenses) {
                groupedByCategory
                        .computeIfAbsent(expense.getExpense().getCategory(), k -> new ArrayList<>())
                        .add(expense);
            }

            // Analizar cada categoría
            for (Long categoryId : groupedByCategory.keySet()) {
                List<ExpenseWithCategory> categoryExpenses = groupedByCategory.get(categoryId);

                double total = 0;
                int count = 0;
                double highest = 0;
                double lowest = Double.MAX_VALUE;

                for (ExpenseWithCategory exp : categoryExpenses) {
                    double amount = exp.getExpense().getAmount();
                    total += amount;
                    count++;
                    if (amount > highest) highest = amount;
                    if (amount < lowest) lowest = amount;
                }

                CategoryAnalysis analysis = new CategoryAnalysis();
                analysis.setCategoryId(categoryId);
                if (!categoryExpenses.isEmpty()) {
                    analysis.setCategoryName(categoryExpenses.get(0).getCategoryName());
                }
                analysis.setTotalAmount(total);
                analysis.setTransactionCount(count);
                analysis.setAverageAmount(total / count);
                analysis.setHighestAmount(highest);
                analysis.setLowestAmount(lowest);

                analyses.add(analysis);
            }

            // Ordenar por importe total descendente
            analyses.sort((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()));

            categoryAnalysis.postValue(analyses);
        } catch (Exception e) {
            Log.e("SoftInterventionViewModel", "Error analizando categorías: " + e.getMessage());
        }
    }

    /**
     * Detecta tendencias en el gasto (aumentando/disminuyendo/estable)
     */
    private void detectSpendingTrends() {
        double currentMonthExpenses = expenseController.getTotalExpensesThisMonth();
        double lastMonthExpenses = expenseController.getTotalExpensesLastMonth();

        String trend;
        if (lastMonthExpenses == 0) {
            trend = "Primer mes de seguimiento";
        } else {
            double percentageChange = ((currentMonthExpenses - lastMonthExpenses) / lastMonthExpenses) * 100;

            if (percentageChange > 10) {
                trend = String.format("⬆️ Gasto en aumento (%.1f%%)", percentageChange);
            } else if (percentageChange < -10) {
                trend = String.format("⬇️ Gasto en disminución (%.1f%%)", Math.abs(percentageChange));
            } else {
                trend = "→ Gasto estable (variación < 10%)";
            }
        }

        spendingTrend.postValue(trend);
    }

    /**
     * Calcula la tasa de ahorro (% de ingresos que no se gastan)
     */
    private void calculateSavingsRate() {
        double income = expenseController.getTotalIncomeThisMonth();
        double expenses = expenseController.getTotalExpensesThisMonth();

        double rate = (income > 0) ? ((income - expenses) / income) * 100 : 0;
        rate = Math.max(0, rate); // No puede ser negativo

        savingsRate.postValue(rate);
    }

    /**
     * Evalúa el nivel de riesgo financiero (1-5)
     * 1 = Muy bajo (ahorra >30%)
     * 2 = Bajo (ahorra 20-30%)
     * 3 = Medio (ahorra 10-20%)
     * 4 = Alto (ahorra 0-10%)
     * 5 = Crítico (gasta más de lo que ingresa)
     */
    private void evaluateRiskLevel() {
        double income = expenseController.getTotalIncomeThisMonth();
        double expenses = expenseController.getTotalExpensesThisMonth();

        int level;
        String description;

        if (income <= 0) {
            level = 5;
            description = "Sin ingresos registrados";
        } else {
            double ratio = (expenses / income) * 100;

            if (ratio <= 70) {
                level = 1;
                description = "Excelente gestión: ahorro >30%";
            } else if (ratio <= 80) {
                level = 2;
                description = "Buena gestión: ahorro 20-30%";
            } else if (ratio <= 90) {
                level = 3;
                description = "Gestión adecuada: ahorro 10-20%";
            } else if (ratio <= 100) {
                level = 4;
                description = "Gestión ajustada: ahorro <10%";
            } else {
                level = 5;
                description = "⚠️ Crítico: gastos > ingresos";
            }
        }

        riskLevel.postValue(level);
        riskDescription.postValue(description);
    }

    /**
     * Genera recomendaciones personalizadas basadas en el análisis
     */
    private void generateRecommendations() {
        List<String> recs = new ArrayList<>();

        try {
            double income = expenseController.getTotalIncomeThisMonth();
            double expenses = expenseController.getTotalExpensesThisMonth();
            int riskLvl = riskLevel.getValue() != null ? riskLevel.getValue() : 3;

            // Recomendación 1: Basada en tasa de ahorro
            Double savRate = savingsRate.getValue();
            if (savRate != null) {
                if (savRate < 10) {
                    recs.add("💡 Aumenta tu ahorro: Con un 10% menos de gasto podrías ahorrar " +
                            String.format("%.2f€", expenses * 0.1) + " mensualmente");
                } else if (savRate > 30) {
                    recs.add("🎉 Excelente ahorro: Mantén este nivel de disciplina financiera");
                }
            }

            // Recomendación 2: Categoría con gasto más alto
            List<CategoryAnalysis> analyses = categoryAnalysis.getValue();
            if (analyses != null && !analyses.isEmpty()) {
                CategoryAnalysis topCategory = analyses.get(0);
                double topPercentage = (income > 0) ? (topCategory.getTotalAmount() / income) * 100 : 0;

                if (topPercentage > 30) {
                    recs.add(String.format("🔍 %s consume el %.1f%% de ingresos. Revisa si es optimizable.",
                            topCategory.getCategoryName(), topPercentage));
                }
            }

            // Recomendación 3: Basada en nivel de riesgo
            if (riskLvl >= 4) {
                recs.add("⚠️ Situación crítica: Gasta más de lo que ingresa. Necesitas ajustar tu presupuesto urgentemente.");
            } else if (riskLvl == 3) {
                recs.add("📊 Márgenes ajustados: Deja espacio para imprevistos.");
            }

            // Recomendación 4: Patrón de gastos frecuentes
            if (analyses != null && analyses.size() > 1) {
                CategoryAnalysis secondCategory = analyses.get(1);
                if (secondCategory.getTransactionCount() > 10 && secondCategory.getAverageAmount() < 20) {
                    recs.add(String.format("📱 Muchas transacciones pequeñas en %s. Agrúpalas puede reducir gastos.",
                            secondCategory.getCategoryName()));
                }
            }

            // Si no hay recomendaciones, agregar una genérica
            if (recs.isEmpty()) {
                recs.add("Mantén monitoreando tus gastos regularmente para optimizar tu situación financiera.");
            }

            recommendations.postValue(recs);
        } catch (Exception e) {
            Log.e("SoftInterventionViewModel", "Error generando recomendaciones: " + e.getMessage());
        }
    }

    // ========== GETTERS ==========

    public LiveData<FinancialData> getFinancialData() {
        return financialData;
    }

    public LiveData<InterventionMessage> getInterventionMessage() {
        return interventionMessage;
    }

    public LiveData<List<CategoryAnalysis>> getCategoryAnalysis() {
        return categoryAnalysis;
    }

    public LiveData<String> getSpendingTrend() {
        return spendingTrend;
    }

    public LiveData<List<String>> getRecommendations() {
        return recommendations;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Double> getSavingsRate() {
        return savingsRate;
    }

    public LiveData<Integer> getRiskLevel() {
        return riskLevel;
    }

    public LiveData<String> getRiskDescription() {
        return riskDescription;
    }

    /**
     * Marca el mensaje de intervención como visto
     */
    public void markAsSeen() {
        wellbeingController.markInterventionAsSeen();
    }

    /**
     * Recarga todos los datos
     */
    public void refresh() {
        loadData();
    }
}