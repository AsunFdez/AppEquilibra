package asunfdiez.com.equilibra.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ViewSelectorBinding;

/**
 * Componente personalizado que simula un selector/spinner con la estética de Equilibra.
 */
public class SelectorView extends LinearLayout {

    private ViewSelectorBinding binding;

    public SelectorView(Context context) {
        super(context);
        init(context, null);
    }

    public SelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        binding = ViewSelectorBinding.inflate(LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectorView);
            String title = a.getString(R.styleable.SelectorView_cardTitle);
            int iconRes = a.getResourceId(R.styleable.SelectorView_cardIcon, -1);
            int iconTint = a.getColor(R.styleable.SelectorView_cardIconTint, -1);
            String label = a.getString(R.styleable.SelectorView_selectorLabel);
            String value = a.getString(R.styleable.SelectorView_selectorValue);

            if (title != null) binding.cardTitle.setText(title);
            else binding.titleContainer.setVisibility(GONE);

            if (iconRes != -1) {
                binding.cardIcon.setImageResource(iconRes);
                if (iconTint != -1) {
                    binding.cardIcon.setColorFilter(iconTint);
                }
            } else {
                binding.cardIcon.setVisibility(GONE);
            }

            if (label != null) binding.selectorLabel.setText(label);
            if (value != null) binding.selectorValue.setText(value);

            a.recycle();
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        binding.selectorContainer.setOnClickListener(l);
    }

    public void setCardTitle(String title) {
        binding.cardTitle.setText(title);
        binding.titleContainer.setVisibility(title != null ? VISIBLE : GONE);
    }

    public void setCardIcon(int resId) {
        binding.cardIcon.setImageResource(resId);
        binding.cardIcon.setVisibility(resId != 0 ? VISIBLE : GONE);
    }

    public void setLabel(String label) {
        binding.selectorLabel.setText(label);
    }

    public void setValue(String value) {
        binding.selectorValue.setText(value);
    }

    public void rotateArrow(float degrees) {
        binding.selectorArrow.animate().rotation(degrees).setDuration(200).start();
    }

    @BindingAdapter("selectorLabel")
    public static void setSelectorLabel(SelectorView view, String label) {
        view.setLabel(label);
    }

    @BindingAdapter("selectorValue")
    public static void setSelectorValue(SelectorView view, String value) {
        view.setValue(value);
    }
}
