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
import asunfdiez.com.equilibra.databinding.ItemFeatureCardBinding;

/*
* Componente reutilizable en los layout con atributos definidos en attrs.xml
* */
public class FeatureCard extends CardView {

    private ItemFeatureCardBinding binding;
    public FeatureCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        binding = ItemFeatureCardBinding.inflate(LayoutInflater.from(context), this, true);

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
            if (desc != null) binding.cardDescription.setText(desc);

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
            int iconRightRes = a.getResourceId(R.styleable.FeatureCard_cardRightIcon, R.drawable.ic_nav_chevron_right);
            if (showRightIcon) {
                binding.cardRightIcon.setVisibility(View.VISIBLE);
                binding.cardRightIcon.setImageResource(iconRightRes);
            } else {
                binding.cardRightIcon.setVisibility(View.GONE);
            }

            // Estética
            setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            setCardElevation(4);

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
            view.binding.cardDescription.setText(description);
        }
    }
}
