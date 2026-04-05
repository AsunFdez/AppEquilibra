package asunfdiez.com.equilibra.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.ImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {
    
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
}
