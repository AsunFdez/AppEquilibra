package asunfdiez.com.equilibra.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import asunfdiez.com.equilibra.R;

public class FeatureCard extends CardView {

    public FeatureCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.item_feature_card, this, true);

        TextView titleView = findViewById(R.id.cardTitle);
        TextView descView = findViewById(R.id.cardDescription);
        ImageView iconView = findViewById(R.id.cardIcon);

        // 3. Configurar atributos solo si las vistas existen
        if (attrs != null && titleView != null && descView != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FeatureCard);

            String title = a.getString(R.styleable.FeatureCard_cardTitle);
            String desc = a.getString(R.styleable.FeatureCard_cardDescription);
            int iconRes = a.getResourceId(R.styleable.FeatureCard_cardIcon, R.drawable.ic_nav_home);
            int tint = a.getColor(R.styleable.FeatureCard_cardIconTint, getResources().getColor(R.color.equilibra_primary));

            if (title != null) titleView.setText(title);
            if (desc != null) descView.setText(desc);
            if (iconView != null) {
                iconView.setImageResource(iconRes);
                iconView.setColorFilter(tint);
            }

            // Estética por defecto
            setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            setCardElevation(4);
            a.recycle();
        }
    }



}