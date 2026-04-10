package asunfdiez.com.equilibra.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    @BindingAdapter("formattedAmount")
    public static void setFormattedAmount(TextView textView, double amount) {
        textView.setText(String.format(java.util.Locale.getDefault(), Constants.CURRENCY_FORMAT, amount));
    }

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

    @BindingAdapter("app:tint")
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
}
