package asunfdiez.com.equilibra.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ItemFeatureCardBinding;

public class FeatureCard extends CardView {

    private ItemFeatureCardBinding binding;
    public FeatureCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        binding = ItemFeatureCardBinding.inflate(LayoutInflater.from(context), this, true);

        TextView titleView = binding.cardTitle;
        TextView descView = binding.cardDescription;
        ImageView iconView = binding.cardIcon;
        View iconCircleView = binding.cardIconCircle;
        TextView arrowView = binding.cardArrow;
        ImageView rightIconView = binding.cardRightIcon;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FeatureCard);

            // Color de fondo
            int defaultWhite = androidx.core.content.ContextCompat.getColor(context, R.color.white);
            int bgColor = a.getColor(R.styleable.FeatureCard_cardBackgroundColor, defaultWhite);
            setCardBackgroundColor(bgColor);

            // Textos
            String title = a.getString(R.styleable.FeatureCard_cardTitle);
            String desc = a.getString(R.styleable.FeatureCard_cardDescription);
            if (titleView != null && title != null) titleView.setText(title);
            if (descView != null && desc != null) descView.setText(desc);

            // Icono Principal y su TINTE (Color)
            int iconRes = a.getResourceId(R.styleable.FeatureCard_cardIcon, R.drawable.ic_nav_home);
            int tintColor = a.getColor(R.styleable.FeatureCard_cardIconTint,
                    androidx.core.content.ContextCompat.getColor(context, R.color.white));

            if (iconView != null) {
                iconView.setImageResource(iconRes);
                ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(tintColor));
            }

            // Círculo de fondo (Drawable)
            int iconCircleRes = a.getResourceId(R.styleable.FeatureCard_cardIconCircle, R.drawable.circle_accent);
            if (iconCircleView != null) {
                iconCircleView.setBackgroundResource(iconCircleRes);
            }

            // Elementos opcionales
            boolean showArrow = a.getBoolean(R.styleable.FeatureCard_showArrow, false);
            if (arrowView != null) {
                arrowView.setVisibility(showArrow ? View.VISIBLE : View.GONE);
            }

            boolean showRightIcon = a.getBoolean(R.styleable.FeatureCard_showRightIcon, false);
            int iconRightRes = a.getResourceId(R.styleable.FeatureCard_cardRightIcon, R.drawable.ic_nav_plus);
            if (rightIconView != null) {
                if (showRightIcon) {
                    rightIconView.setVisibility(View.VISIBLE);
                    rightIconView.setImageResource(iconRightRes);
                } else {
                    rightIconView.setVisibility(View.GONE);
                }
            }

            // Estética
            setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            setCardElevation(4);

            a.recycle();
        }
    }


}
