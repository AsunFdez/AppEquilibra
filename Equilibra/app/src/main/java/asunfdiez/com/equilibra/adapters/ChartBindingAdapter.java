package asunfdiez.com.equilibra.adapters;

import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.CategoryAnalysis;

/**
 * Binding adapters para configurar gráficos de MPAndroidChart de forma reactiva en el XML.
 */
public class ChartBindingAdapter {

    @BindingAdapter({"barData", "barLabels"})
    public static void setBarChartData(BarChart chart, Double[] values, String[] labels) {
        if (chart == null || values == null || labels == null) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            float val = values[i] != null ? values[i].floatValue() : 0f;
            entries.add(new BarEntry(i, val));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(chart.getContext(), R.color.secondary_light));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        BarData data = new BarData(dataSet);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        
        chart.animateY(1000);
        chart.invalidate();
    }

    @BindingAdapter("pieAnalysisData")
    public static void setPieAnalysisData(PieChart pieChart, List<CategoryAnalysis> analysis) {
        if (analysis == null || analysis.isEmpty()) return;

        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategoryAnalysis item : analysis) {
            entries.add(new PieEntry((float) item.getAmount(), item.getCategory()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        int[] chartColors = new int[]{
                pieChart.getContext().getColor(R.color.accent),
                pieChart.getContext().getColor(R.color.secondary),
                pieChart.getContext().getColor(R.color.primary),
                pieChart.getContext().getColor(R.color.purple),
                pieChart.getContext().getColor(R.color.teal),
                pieChart.getContext().getColor(R.color.gray_400)
        };
        dataSet.setColors(chartColors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setDrawValues(false);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    @BindingAdapter("pieLegendData")
    public static void setPieLegendData(androidx.recyclerview.widget.RecyclerView recyclerView, List<CategoryAnalysis> analysis) {
        if (analysis == null || analysis.isEmpty()) return;

        asunfdiez.com.equilibra.adapters.CategoryLegendAdapter adapter = (asunfdiez.com.equilibra.adapters.CategoryLegendAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new asunfdiez.com.equilibra.adapters.CategoryLegendAdapter();
            recyclerView.setAdapter(adapter);
        }

        int[] chartColors = new int[]{
                recyclerView.getContext().getColor(R.color.accent),
                recyclerView.getContext().getColor(R.color.secondary),
                recyclerView.getContext().getColor(R.color.primary),
                recyclerView.getContext().getColor(R.color.purple),
                recyclerView.getContext().getColor(R.color.teal),
                recyclerView.getContext().getColor(R.color.gray_400)
        };

        adapter.setItems(analysis, chartColors);
    }

    @BindingAdapter({"barData", "barLabels", "barShowValues"})
    public static void setBarChartData(BarChart barChart, Double[] dataValues, String[] labels, boolean showValues) {
        if (dataValues == null || labels == null) return;

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.getAxisLeft().setDrawGridLines(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dataValues.length; i++) {
            entries.add(new BarEntry(i, dataValues[i].floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(barChart.getContext(), R.color.secondary_light));
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(showValues);

        barChart.setData(new BarData(dataSet));
        barChart.invalidate();
    }

}
