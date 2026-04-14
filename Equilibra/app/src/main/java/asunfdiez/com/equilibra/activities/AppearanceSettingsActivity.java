package asunfdiez.com.equilibra.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityAppearanceSettingsBinding;
import asunfdiez.com.equilibra.databinding.ItemSelectorPopupBinding;
import asunfdiez.com.equilibra.viewmodels.AppearanceSettingsViewModel;

/**
 * Activity para configurar apariencia de la app
 * Usa SharedPreferences para guardar preferencias
 *
 */
public class AppearanceSettingsActivity extends AppCompatActivity {


    // Data Binding
    private ActivityAppearanceSettingsBinding binding;

    // ViewModel
    private AppearanceSettingsViewModel viewModel;

    private PopupWindow viewSelectorPopup;

    private PopupWindow themeSelectorPopup;
    private PopupWindow languageSelectorPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appearance_settings);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AppearanceSettingsViewModel.class);
        binding.setViewModel(viewModel);

        // Configurar UI
        setupToolbar();
        setupListeners();
        setupObservers();

        // Animación de entrada
        binding.getRoot().startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    /**
     * Configura el Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Configura los listeners
     */
    private void setupListeners() {

        binding.selectorView.setOnClickListener(v -> showViewSelector() );
        // Selector de tema
        binding.selectorTheme.setOnClickListener(v -> showThemeSelector());

        // Selector de idioma
        binding.selectorLanguage.setOnClickListener(v -> showLanguageSelector());
    }

    private void showViewSelector() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        String[] viewModes = {"beginner", "intermediate", "advanced"};
        String[] viewModeNames = {getString(R.string.view_selector_text_beginner), getString(R.string.view_selector_text_intermediate), getString(R.string.view_selector_text_advanced)};

        for (int i = 0; i < viewModes.length; i++) {
            final String viewModeValue = viewModes[i];
            ItemSelectorPopupBinding itemBinding = ItemSelectorPopupBinding.inflate(getLayoutInflater(), container, false);
            itemBinding.setText(viewModeNames[i]);
            itemBinding.setSelected(viewModeValue.equals(viewModel.getViewMode().getValue()));
            if (i == viewModes.length - 1) itemBinding.divider.setVisibility(View.GONE);
            itemBinding.executePendingBindings();
            itemBinding.getRoot().setOnClickListener(v -> {
                viewModel.setViewMode(viewModeValue);
                if (viewSelectorPopup != null) viewSelectorPopup.dismiss();
            });
            container.addView(itemBinding.getRoot());
        }

        viewSelectorPopup = createPopup(container, binding.selectorView);
        binding.selectorView.rotateArrow(180f);
        viewSelectorPopup.setOnDismissListener(() -> binding.selectorView.rotateArrow(0f));

    }

    private void showThemeSelector() {
        // Reutilizamos la lógica de creación de opciones dinámicamente o inflamos un layout específico
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        String[] themes = {"light", "dark", "auto"};
        String[] themeNames = {getString(R.string.theme_light), getString(R.string.theme_dark), getString(R.string.theme_auto)};

        for (int i = 0; i < themes.length; i++) {
            final String themeValue = themes[i];
            ItemSelectorPopupBinding itemBinding = ItemSelectorPopupBinding.inflate(getLayoutInflater(), container, false);
            itemBinding.setText(themeNames[i]);
            itemBinding.setSelected(themeValue.equals(viewModel.getTheme().getValue()));
            if (i == themes.length - 1) itemBinding.divider.setVisibility(View.GONE);
            itemBinding.executePendingBindings();

            itemBinding.getRoot().setOnClickListener(v -> {
                viewModel.setTheme(themeValue);
                if (themeSelectorPopup != null) themeSelectorPopup.dismiss();
            });
            container.addView(itemBinding.getRoot());
        }

        themeSelectorPopup = createPopup(container, binding.selectorTheme);
        binding.selectorTheme.rotateArrow(180f);
        themeSelectorPopup.setOnDismissListener(() -> binding.selectorTheme.rotateArrow(0f));
    }

    private void showLanguageSelector() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        String[] languages = getResources().getStringArray(R.array.languages);

        for (int i = 0; i < languages.length; i++) {
            final String langName = languages[i];
            final String langCode = getLanguageCode(langName);
            ItemSelectorPopupBinding itemBinding = ItemSelectorPopupBinding.inflate(getLayoutInflater(), container, false);
            itemBinding.setText(langName);
            itemBinding.setSelected(langCode.equals(viewModel.getLanguage().getValue()));
            if (i == languages.length - 1) itemBinding.divider.setVisibility(View.GONE);
            itemBinding.executePendingBindings();

            itemBinding.getRoot().setOnClickListener(v -> {
                viewModel.setLanguage(langCode);
                if (languageSelectorPopup != null) languageSelectorPopup.dismiss();
            });
            container.addView(itemBinding.getRoot());
        }

        languageSelectorPopup = createPopup(container, binding.selectorLanguage);
        binding.selectorLanguage.rotateArrow(180f);
        languageSelectorPopup.setOnDismissListener(() -> binding.selectorLanguage.rotateArrow(0f));
    }

    private PopupWindow createPopup(View content, View anchor) {
        androidx.cardview.widget.CardView cardView = new androidx.cardview.widget.CardView(this);
        cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
        cardView.setCardElevation(12f);
        cardView.addView(content);

        PopupWindow popup = new PopupWindow(this);
        popup.setContentView(cardView);
        popup.setWidth(anchor.getWidth());
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        popup.showAsDropDown(anchor, 0, 10);
        return popup;
    }

    /**
     * Configura los observadores
     */
    private void setupObservers() {

        // Observar mensajes de estado
        viewModel.getStatusMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Obtiene el código de idioma desde el nombre
     */
    private String getLanguageCode(String languageName) {
        if (languageName.contains("Español")) {
            return "es";
        } else if (languageName.contains("English")) {
            return "en";
        } else if (languageName.contains("Français")) {
            return "fr";
        } else if (languageName.contains("Deutsch")) {
            return "de";
        }
        return "es";
    }

    /**
     * Obtiene la posición del spinner para un idioma
     */
    private int getLanguagePosition(String langCode) {
        switch (langCode) {
            case "es":
                return 0;
            case "en":
                return 1;
            case "fr":
                return 2;
            case "de":
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
