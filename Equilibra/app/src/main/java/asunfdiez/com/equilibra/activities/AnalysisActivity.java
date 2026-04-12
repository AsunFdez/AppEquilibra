package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityAnalysisBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.AnalysisViewModel;

/**
 * AnalysisActivity - Pantalla de análisis financiero
 * Muestra gráficos y estadísticas de gastos e ingresos
 */
public class AnalysisActivity extends AppCompatActivity implements BottomNavigationHandler {

    private ActivityAnalysisBinding binding;
    private AnalysisViewModel viewModel;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);
        setupBottomNavigation();

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.togglePeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                viewModel.setPeriod(checkedId == R.id.btnWeekly);
            }
        });

        setupPieChart();
        setupBarChart();
    }

    private void observeViewModel() {
        viewModel.getIsWeekly().observe(this, isWeekly -> {
            updatePeriodLabels(isWeekly);
        });

        viewModel.getAnalysisState().observe(this, state -> {
            updateSummaryCards(state.totalExpenses, state.savings);
            updatePieChartData(state.pieEntries);
            updateBarChartData(state.barValues, viewModel.getIsWeekly().getValue());
        });
    }

    private void updatePeriodLabels(boolean isWeekly) {
        binding.tvTrendTitle.setText(isWeekly ? R.string.trend_weekly : R.string.trend_monthly);
    }

    private void updateSummaryCards(double expenses, double savings) {
        binding.cardExpenses.tvSummaryValue.setText(currencyFormat.format(expenses));
        binding.cardExpenses.tvSummaryLabel.setText(R.string.label_expenses);
        binding.cardExpenses.ivSummaryIcon.setImageResource(R.drawable.ic_ana_chart_column_decreasing);
        binding.cardExpenses.ivSummaryIcon.setColorFilter(ContextCompat.getColor(this, R.color.red));

        binding.cardSavings.tvSummaryValue.setText(currencyFormat.format(savings));
        binding.cardSavings.tvSummaryLabel.setText(R.string.label_saved);
        binding.cardSavings.ivSummaryIcon.setImageResource(R.drawable.ic_nav_trending_up);
        binding.cardSavings.ivSummaryIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
    }

    private void setupPieChart() {
        binding.pieChart.setHoleRadius(0f);
        binding.pieChart.setTransparentCircleRadius(0f);
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setEntryLabelColor(Color.WHITE);
        binding.pieChart.setEntryLabelTextSize(14f);
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void setupBarChart() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.getLegend().setEnabled(false);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        //hiding the x-axis line, default true if not set
        //xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);
        //xAxis.setAxisLineWidth(4f);
        //xAxis.setAxisLineColor(R.color.accent);

        YAxis yAxis = binding.barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        //yAxis.setAxisLineWidth(4f);
        //yAxis.setAxisLineColor(R.color.accent);
        //yAxis.setDrawAxisLine(false);
    }

    private void updatePieChartData(List<CategoryAnalysis> analysis) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategoryAnalysis item : analysis) {
            entries.add(new PieEntry((float) item.getAmount(), item.getCategory()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{R.color.accent, R.color.secondary, R.color.primary, R.color.purple, R.color.secondary_light, R.color.accent_dark}, this);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChart));
        // Personalizar los valores (Tamaño y Color)
        dataSet.setValueTextColor(Color.WHITE); // Color blanco
        dataSet.setValueTextSize(14f);         // Tamaño 14sp

        binding.pieChart.setData(data);
        binding.pieChart.invalidate();
    }

    private void updateBarChartData(double[] values, boolean isWeekly) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        String[] labels = isWeekly
                ? new String[]{getString(R.string.day_mon), getString(R.string.day_tue), getString(R.string.day_wed), getString(R.string.day_thu), getString(R.string.day_fri), getString(R.string.day_sat), getString(R.string.day_sun)}
                : new String[]{getString(R.string.month_jan), getString(R.string.month_feb), getString(R.string.month_mar), getString(R.string.month_apr), getString(R.string.month_may), getString(R.string.month_jun), getString(R.string.month_jul), getString(R.string.month_aug), getString(R.string.month_sep), getString(R.string.month_oct), getString(R.string.month_nov), getString(R.string.month_dec)};

        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, (float) values[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.chart_label_expenses));
        dataSet.setColor(ContextCompat.getColor(this, R.color.secondary_light));
        dataSet.setValueTextSize(14f);

        binding.barChart.setData(new BarData(dataSet));
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }

    private void setupBottomNavigation() {

        if (binding.bottomNav != null) {
            BottomNavigationBinding navBinding = DataBindingUtil.getBinding(binding.bottomNav.bottomNavigation);
            if (navBinding != null) {
                navBinding.setActiveTab("analysis");
                navBinding.setHandler(this);
            }
        }
    }
    @Override
    public void onHomeClick() {
        navigateTo(MainActivity.class);
    }

    @Override
    public void onAnalysisClick() {
        binding.contentScroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onAddExpenseClick(View v) {
        AnimationHelper.animateFABPress(v);
        handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), Constants.FAB_ANIM_DURATION + 50);
    }

    @Override
    public void onAchievementsClick() {
        navigateTo(AchievementsActivity.class);
    }

    @Override
    public void onSettingsClick() {
        navigateTo(SettingsActivity.class);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
