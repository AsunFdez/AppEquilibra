package asunfdiez.com.equilibra.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
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
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.DateUtils;

/**
 * Adaptadores de vinculación globales para el sistema de Data Binding de Android.
 * 
 * <p>Contiene lógica reutilizable en los archivos de diseño XML para tareas como 
 * el formateo de moneda, la visibilidad condicional de vistas, el tintado dinámico 
 * de iconos y la manipulación de cadenas para visualización.</p>
 */
public class BindingAdapters {

    /**
     * Formatea un valor numérico como moneda y lo establece en un TextView.
     */
    @BindingAdapter("formattedAmount")
    public static void setFormattedAmount(TextView textView, double amount) {
        textView.setText(String.format(java.util.Locale.getDefault(), Constants.CURRENCY_FORMAT, amount));
    }

    /**
     * Calcula y muestra un monto prorrateado (ej. gasto por día o semana).
     */
    @BindingAdapter({"amount", "divisor"})
    public static void setDividedAmount(TextView textView, double amount, int divisor) {
        if (divisor != 0) {
            double val = amount / divisor;
            textView.setText(String.format(java.util.Locale.getDefault(), Constants.CURRENCY_FORMAT, val));
        }
    }
    
    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView imageView, int resource) {
        if (resource != 0) {
            imageView.setImageResource(resource);
        }
    }

    @BindingAdapter("imageTint")
    public static void setImageTint(ImageView imageView, String colorStr) {
        if (colorStr != null && !colorStr.isEmpty()) {
            try {
                int color = Color.parseColor(colorStr);
                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color));
            } catch (Exception e) {
                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(Color.GRAY));
            }
        } else {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(Color.GRAY));
        }
    }

    @BindingAdapter("customBackgroundTint")
    public static void setCustomBackgroundTint(View view, int color) {
        if (color != 0) {
            view.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    @BindingAdapter("tint")
    public static void setImageViewTint(ImageView imageView, int color) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color));
    }

    @BindingAdapter("categories")
    public static void setCategories(androidx.recyclerview.widget.RecyclerView recyclerView, java.util.List<asunfdiez.com.equilibra.models.ExpenseCategory> categories) {
        asunfdiez.com.equilibra.adapters.CategoryAdapter adapter = (asunfdiez.com.equilibra.adapters.CategoryAdapter) recyclerView.getAdapter();
        if (adapter != null && categories != null) {
            adapter.updateCategories(categories);
        }
    }

    @BindingAdapter("progressValue")
    public static void setProgressValue(android.widget.ProgressBar progressBar, int progress) {
        progressBar.setProgress(progress);
    }

    @BindingAdapter("android:text")
    public static void setDoubleToText(com.google.android.material.textfield.TextInputEditText view, double value) {
        if (view.getText() != null && !view.getText().toString().isEmpty()) {
            try {
                double current = Double.parseDouble(view.getText().toString());
                if (current == value) return;
            } catch (NumberFormatException ignored) {}
        }
        view.setText(String.format(java.util.Locale.US, "%.2f", value));
    }

    @androidx.databinding.InverseBindingAdapter(attribute = "android:text")
    public static double getTextToDouble(com.google.android.material.textfield.TextInputEditText view) {
        try {
            return Double.parseDouble(view.getText().toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
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


    /**
     * Muestra la última fecha y hora de sincronización formateada de manera amigable.
     */
    @BindingAdapter("formattedSyncTime")
    public static void setFormattedSyncTime(TextView textView, long timestamp) {
        String baseText = textView.getContext().getString(R.string.profile_last_sync);
        if (timestamp <= 0) {
            String never = textView.getContext().getString(R.string.profile_sync_never);
            textView.setText(String.format(baseText, never));
        } else {
            String formatted = DateUtils.formatDateTime(timestamp);
            textView.setText(String.format(baseText, formatted));
        }
    }
}

