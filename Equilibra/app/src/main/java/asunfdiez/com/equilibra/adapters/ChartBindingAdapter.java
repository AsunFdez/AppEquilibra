package asunfdiez.com.equilibra.adapters;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import asunfdiez.com.equilibra.R;

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
}
