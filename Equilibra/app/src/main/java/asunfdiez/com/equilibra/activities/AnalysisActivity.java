package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.text.NumberFormat;
import java.util.Locale;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityAnalysisBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.AnalysisViewModel;

/**
 * Actividad que proporciona análisis visuales y estadísticos de los gastos.
 * 
 * <p>Esta clase utiliza la librería MPAndroidChart para mostrar gráficos de barras y de pastel
 * que representan la distribución de los gastos por categoría y su evolución temporal. 
 * Permite al usuario visualizar su comportamiento financiero de forma intuitiva.</p>
 */
public class AnalysisActivity extends AppCompatActivity implements BottomNavigationHandler {

    // Vinculación de Datos (Data Binding)
    private ActivityAnalysisBinding binding;
    
    // Modelo de Vista (ViewModel)
    private AnalysisViewModel viewModel;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        setupBottomNavigation();

        setupUI();
        setObservers();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.togglePeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                viewModel.setPeriod(checkedId == R.id.btnWeekly);
            }
        });
    }

    private void setObservers() {
        viewModel.getAnalysisState().observe(this, state -> {
            if (state != null) {
                updateSummaryCards(state.totalExpenses, state.savings);
            }
        });
    }

    private void updateSummaryCards(double expenses, double savings) {
        binding.cardExpenses.tvSummaryValue.setText(currencyFormat.format(expenses));
        binding.cardExpenses.tvSummaryLabel.setText(R.string.label_expenses);
        binding.cardExpenses.ivSummaryIcon.setImageResource(R.drawable.ic_ana_chart_column_decreasing);
        binding.cardExpenses.ivSummaryIcon.setColorFilter(ContextCompat.getColor(this, R.color.red));

        binding.cardSavings.tvSummaryValue.setText(currencyFormat.format(savings));
        binding.cardSavings.tvSummaryLabel.setText(R.string.label_saved);
        binding.cardSavings.ivSummaryIcon.setImageResource(R.drawable.ic_nav_trending_up);
        binding.cardSavings.ivSummaryIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
    }

    private void setupBottomNavigation() {
        if (binding.bottomNav != null) {
            BottomNavigationBinding navBinding = DataBindingUtil.getBinding(binding.bottomNav.bottomNavigation);
            if (navBinding != null) {
                navBinding.setActiveTab("analysis");
                navBinding.setHandler(this);
            }
        }
    }

    @Override
    public void onHomeClick() {
        navigateTo(MainActivity.class);
    }

    @Override
    public void onAnalysisClick() {
        binding.contentScroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onAddExpenseClick(View v) {
        AnimationHelper.animateFABPress(v);
        handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), Constants.FAB_ANIM_DURATION + 50);
    }

    @Override
    public void onAchievementsClick() {
        navigateTo(AchievementsActivity.class);
    }

    @Override
    public void onSettingsClick() {
        navigateTo(SettingsActivity.class);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
