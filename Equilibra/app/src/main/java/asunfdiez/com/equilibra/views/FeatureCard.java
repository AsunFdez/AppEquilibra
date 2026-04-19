package asunfdiez.com.equilibra.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.BindingAdapter;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.FeatureCardBinding;

/*
* Componente reutilizable en los layout con atributos definidos en attrs.xml
* */
public class FeatureCard extends CardView {

    private FeatureCardBinding binding;
    public FeatureCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        binding = FeatureCardBinding.inflate(LayoutInflater.from(context), this, true);

        if (attrs != null) {

            TypedArray a;
            a = context.obtainStyledAttributes(attrs, R.styleable.FeatureCard);

            // Color de fondo
            int defaultWhite = androidx.core.content.ContextCompat.getColor(context, R.color.white);
            int bgColor = a.getColor(R.styleable.FeatureCard_cardBackgroundColor, defaultWhite);
            setCardBackgroundColor(bgColor);

            // Textos
            String title = a.getString(R.styleable.FeatureCard_cardTitle);
            String desc = a.getString(R.styleable.FeatureCard_cardDescription);
            if (title != null) binding.cardTitle.setText(title);
            if (desc != null && !desc.isEmpty()) {
                binding.cardDescription.setText(desc);
                binding.cardDescription.setVisibility(View.VISIBLE);
            } else {
                binding.cardDescription.setVisibility(View.GONE);
            }

            // Icono Principal y su TINTE (Color)
            int iconRes = a.getResourceId(R.styleable.FeatureCard_cardIcon, R.drawable.ic_nav_home);
            int tintColor = a.getColor(R.styleable.FeatureCard_cardIconTint, defaultWhite);
            binding.cardIcon.setImageResource(iconRes);
            ImageViewCompat.setImageTintList(binding.cardIcon, ColorStateList.valueOf(tintColor));

            // Círculo de fondo del icono (Drawable)
            int iconCircleRes = a.getResourceId(R.styleable.FeatureCard_cardIconCircle, R.drawable.circle_accent);
            binding.cardIconCircle.setBackgroundResource(iconCircleRes);

            // Elementos opcionales
            boolean showRightIcon = a.getBoolean(R.styleable.FeatureCard_showRightIcon, false);
            if (showRightIcon) {
                // Icono Principal y su TINTE (Color)
                int iconRightRes = a.getResourceId(R.styleable.FeatureCard_cardRightIcon, R.drawable.ic_nav_chevron_right);
                int tintIconRightColor = a.getColor(R.styleable.FeatureCard_cardRightIconTint, defaultWhite);
                binding.cardRightIcon.setVisibility(View.VISIBLE);
                binding.cardRightIcon.setImageResource(iconRightRes);
                ImageViewCompat.setImageTintList(binding.cardRightIcon, ColorStateList.valueOf(tintIconRightColor));
            } else {
                binding.cardRightIcon.setVisibility(View.GONE);
            }

            // Switch opcional
            boolean showSwitch = a.getBoolean(R.styleable.FeatureCard_showSwitch, false);
            if (showSwitch) {
                binding.cardSwitch.setVisibility(View.VISIBLE);
                boolean switchChecked = a.getBoolean(R.styleable.FeatureCard_switchChecked, false);
                binding.cardSwitch.setChecked(switchChecked);
            } else {
                binding.cardSwitch.setVisibility(View.GONE);
            }

            // Estética
            setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            setCardElevation(10);

            a.recycle();
        }
    }

    @BindingAdapter("cardTitle")
    public static void setCardTitle(FeatureCard view, String title) {
        if (view.binding != null && view.binding.cardTitle != null) {
            view.binding.cardTitle.setText(title);
        }
    }

    @BindingAdapter("cardDescription")
    public static void setCardDescription(FeatureCard view, String description) {
        if (view.binding != null && view.binding.cardDescription != null) {
            if (description != null && !description.isEmpty()) {
                view.binding.cardDescription.setText(description);
                view.binding.cardDescription.setVisibility(View.VISIBLE);
            } else {
                view.binding.cardDescription.setVisibility(View.GONE);
            }
        }
    }

    @BindingAdapter("switchChecked")
    public static void setSwitchChecked(FeatureCard view, boolean isChecked) {
        if (view.binding != null && view.binding.cardSwitch != null) {
            if (view.binding.cardSwitch.isChecked() != isChecked) {
                view.binding.cardSwitch.setChecked(isChecked);
            }
        }
    }

    public void setOnCheckedChangeListener(android.widget.CompoundButton.OnCheckedChangeListener listener) {
        if (binding != null && binding.cardSwitch != null) {
            binding.cardSwitch.setOnCheckedChangeListener(listener);
        }
    }

    public boolean isChecked() {
        if (binding != null && binding.cardSwitch != null) {
            return binding.cardSwitch.isChecked();
        }
        return false;
    }
}
