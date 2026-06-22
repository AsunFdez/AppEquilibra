package asunfdiez.com.equilibra.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import asunfdiez.com.equilibra.R;

/**
 * Conjunto de adaptadores de vinculación (Binding Adapters) para la pantalla de perfil.
 * 
 * <p>Proporciona lógica personalizada para el Data Binding en elementos de la UI 
 * del perfil, como la generación automática de iniciales para el avatar o la 
 * gestión del estilo visual de los campos según su estado.</p>
 */
public class ProfileBindingAdapter {

    /**
     * Establece la inicial del nombre del usuario en el componente visual del avatar.
     */
    @BindingAdapter("avatarInitial")
    public static void setAvatarInitial(TextView textView, String name) {
        if (name == null || name.isEmpty()) {
            textView.setText("?"); // Valor por defecto
            return;
        }

        // Obtener la primera letra y convertir a Mayúscula
        String initial = name.substring(0, 1).toUpperCase();
        textView.setText(initial);
    }

    /**
     * Carga la imagen de perfil desde una URL.
     */
    @BindingAdapter("profileImage")
    public static void setProfileImage(ImageView imageView, String url) {
        if (url == null || url.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_person_circle);
            return;
        }

        Glide.with(imageView.getContext())
                .load(url)
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_person_circle)
                .error(R.drawable.ic_person_circle)
                .into(imageView);
    }
    @BindingAdapter("viewModeText")
    public static void setViewModeText(TextView view, String mode) {
        if (mode == null) return;

        int resId;
        switch (mode.toLowerCase()) {
            case "intermediate":
                resId = R.string.view_selector_text_intermediate;
                break;
            case "advanced":
                resId = R.string.view_selector_text_advanced;
                break;
            default:
                resId = R.string.view_selector_text_beginner;
                break;
        }
        view.setText(resId);
    }

    @BindingAdapter("isSelectedView")
    public static void setSelectedViewStyle(TextView view, boolean isSelected) {
        Context context = view.getContext();
        if (isSelected) {
            view.setTextColor(ContextCompat.getColor(context, R.color.primary));
            view.setTypeface(null, Typeface.BOLD);
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_lighter));
        } else {
            view.setTextColor(ContextCompat.getColor(context, R.color.gray_700));
            view.setTypeface(null, Typeface.NORMAL);
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }

    @BindingAdapter("etMonthlyIncome")
    public static void setMonthlyIncome(TextView view, double income) {
        // Evitar bucles infinitos y saltos del cursor comprobando si el valor ha cambiado realmente
        String currentText = view.getText().toString();
        try {
            double currentVal = Double.parseDouble(currentText);
            if (Math.abs(currentVal - income) < 0.001) return;
        } catch (NumberFormatException ignored) {}
        
        view.setText(String.valueOf(income));
    }

    @InverseBindingAdapter(attribute = "etMonthlyIncome")
    public static double getMonthlyIncome(TextView view) {
        try {
            return Double.parseDouble(view.getText().toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @BindingAdapter("etMonthlyIncomeAttrChanged")
    public static void setMonthlyIncomeListener(TextView view, final InverseBindingListener listener) {
        if (listener != null) {
            view.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {
                    listener.onChange();
                }
            });
        }
    }

    @BindingAdapter("android:textStyle")
    public static void setTextStyle(TextView view, String style) {
        if (style == null) return;
        switch (style) {
            case "bold":
                view.setTypeface(null, Typeface.BOLD);
                break;
            case "italic":
                view.setTypeface(null, Typeface.ITALIC);
                break;
            default:
                view.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }



}
