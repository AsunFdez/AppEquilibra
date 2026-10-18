package asunfdiez.com.equilibra.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;
import androidx.databinding.BindingAdapter;

import asunfdiez.com.equilibra.R;
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
     * Calcula y muestra un importe prorrateado (ej. gasto por día o semana).
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
            try {
                int resolvedColor = androidx.core.content.ContextCompat.getColor(view.getContext(), color);
                view.setBackgroundTintList(ColorStateList.valueOf(resolvedColor));
            } catch (Exception e) {
                view.setBackgroundTintList(ColorStateList.valueOf(color));
            }
        }
    }

    @BindingAdapter("tint")
    public static void setImageViewTint(ImageView imageView, int color) {
        if (color != 0) {
            try {
                int resolvedColor = androidx.core.content.ContextCompat.getColor(imageView.getContext(), color);
                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(resolvedColor));
            } catch (Exception e) {
                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color));
            }
        }
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

