package asunfdiez.com.equilibra.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.BindingAdapter;
import asunfdiez.com.equilibra.utils.Constants;

public class BindingAdapters {

    @BindingAdapter("formattedAmount")
    public static void setFormattedAmount(TextView textView, String amount) {
        if (amount != null && !amount.isEmpty()) {
            try {
                double val = Double.parseDouble(amount);
                textView.setText(String.format(java.util.Locale.getDefault(), Constants.CURRENCY_FORMAT, val));
            } catch (NumberFormatException e) {
                textView.setText(amount);
            }
        }
    }

    @BindingAdapter({"amount", "divisor"})
    public static void setDividedAmount(TextView textView, String amount, int divisor) {
        if (amount != null && !amount.isEmpty() && divisor != 0) {
            try {
                double val = Double.parseDouble(amount) / divisor;
                textView.setText(String.format(java.util.Locale.getDefault(), Constants.CURRENCY_FORMAT, val));
            } catch (NumberFormatException e) {
                textView.setText("");
            }
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

    @BindingAdapter("categories")
    public static void setCategories(androidx.recyclerview.widget.RecyclerView recyclerView, java.util.List<asunfdiez.com.equilibra.models.ExpenseCategory> categories) {
        asunfdiez.com.equilibra.adapters.CategoryAdapter adapter = (asunfdiez.com.equilibra.adapters.CategoryAdapter) recyclerView.getAdapter();
        if (adapter != null && categories != null) {
            adapter.updateCategories(categories);
        }
    }
}
