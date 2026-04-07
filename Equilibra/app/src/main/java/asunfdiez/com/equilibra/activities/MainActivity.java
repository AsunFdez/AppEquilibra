package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.WellbeingController;
import asunfdiez.com.equilibra.databinding.ActivityAdvancedUserBinding;
import asunfdiez.com.equilibra.databinding.ActivityIntermediateUserBinding;
import asunfdiez.com.equilibra.databinding.ActivityMainBinding;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;
    private WellbeingController wellbeingController;
    private ProgressBar progressBar;

    // Bottom Navigation
    private LinearLayout navHome;
    private LinearLayout navAnalysis;
    private LinearLayout navAchievements;
    private LinearLayout navSettings;
    private CardView fabAddExpense;

    private ImageView navHomeIcon;
    private ImageView navAnalysisIcon;
    private ImageView navAchievementsIcon;
    private ImageView navSettingsIcon;

    private TextView navHomeText;
    private TextView navAnalysisText;
    private TextView navAchievementsText;
    private TextView navSettingsText;

    // Common Cards
    private CardView viewAllExpensesCard;

    // View State
    private String currentViewType;

    // Handler
    private final Handler handler = new Handler();
    // dataBinding
    private ViewDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        
        initializeComponents();
        setupObservers();
    }

    private void initializeComponents() {
        wellbeingController = new WellbeingController(this);
    }

    private void setupObservers() {
        // Observar el perfil de usuario para manejar cambios de datos y de vista
        viewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                handleUserProfileUpdate(user);
            }
        });

        // Observar si se debe mostrar una intervención (Bienestar financiero)
        viewModel.getShouldShowIntervention().observe(this, shouldShow -> {
            if (shouldShow != null && shouldShow) {
                viewModel.resetInterventionTrigger();
                Intent intent = new Intent(this, SoftInterventionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void handleUserProfileUpdate(UserProfile user) {
        String viewType = user.getCurrentView();
        
        // Si el tipo de vista ha cambiado o es la primera vez, cargamos el layout
        if (binding == null || !viewType.equals(currentViewType)) {
            currentViewType = viewType;
            loadLayout(viewType, user);
        } else {
            // Si es la misma vista, solo actualizamos los datos vinculados
            binding.setVariable(asunfdiez.com.equilibra.BR.user, user);
            binding.executePendingBindings();
            
            // Actualizar elementos manuales o animaciones que dependen del progreso
            if (binding instanceof ActivityMainBinding) {
                updateBeginnerAnimations(user);
            }
        }
    }

    private void loadLayout(String viewType, UserProfile user) {
        int layoutId = switch (viewType) {
            case Constants.USER_LEVEL_INTERMEDIATE -> R.layout.activity_intermediate_user;
            case Constants.USER_LEVEL_ADVANCED -> R.layout.activity_advanced_user;
            default -> R.layout.activity_main;
        };

        // DataBindingUtil para inflar y establecer el contenido
        binding = DataBindingUtil.setContentView(this, layoutId);
        binding.setLifecycleOwner(this);
        binding.setVariable(asunfdiez.com.equilibra.BR.user, user);
        binding.executePendingBindings();

        // Vincular vistas y listeners comunes
        initCommonViews();
        setupCommonListeners();

        // Vincular vistas y listeners según el tipo de Binding
        if (binding instanceof ActivityMainBinding b) {
            initBeginnerViews(b, user);
            setBeginnerListeners(b);
            animateBeginnerCards(b, user);
        } else if (binding instanceof ActivityIntermediateUserBinding b) {
            initIntermediateViews(b);
            setIntermediateListeners(b);
        } else if (binding instanceof ActivityAdvancedUserBinding b) {
            initAdvancedViews(b);
            setAdvancedListeners(b);
        }
    }

    private void initCommonViews() {
        View root = binding.getRoot();

        // Content Cards
        viewAllExpensesCard = root.findViewById(R.id.viewAllExpensesCard);

        // Bottom Navigation
        navHome = root.findViewById(R.id.navHome);
        navAnalysis = root.findViewById(R.id.navAnalysis);
        navAchievements = root.findViewById(R.id.navAchievements);
        navSettings = root.findViewById(R.id.navSettings);
        fabAddExpense = root.findViewById(R.id.fabAddExpense);

        navHomeIcon = root.findViewById(R.id.navHomeIcon);
        navAnalysisIcon = root.findViewById(R.id.navAnalysisIcon);
        navAchievementsIcon = root.findViewById(R.id.navAchievementsIcon);
        navSettingsIcon = root.findViewById(R.id.navSettingsIcon);

        navHomeText = root.findViewById(R.id.navHomeText);
        navAnalysisText = root.findViewById(R.id.navAnalysisText);
        navAchievementsText = root.findViewById(R.id.navAchievementsText);
        navSettingsText = root.findViewById(R.id.navSettingsText);
        
        updateBottomNavigation("home");
    }

    private void initBeginnerViews(ActivityMainBinding b, UserProfile user) {
        progressBar = b.progressCard.weeklyProgressBar;
        b.progressCard.progressTitle.setText(getString(R.string.weekly_progress_title));
        b.progressCard.progressText.setText(getString(R.string.week_progress, user.getWeekProgress(), user.getWeekGoal()));
    }

    private void updateBeginnerAnimations(UserProfile user) {
        if (progressBar != null) {
            AnimationHelper.animateProgressBar(user.getWeekProgress(), user.getWeekGoal(), progressBar);
        }
    }

    private void initIntermediateViews(ActivityIntermediateUserBinding b) {
        // Implementación futura
    }

    private void initAdvancedViews(ActivityAdvancedUserBinding b) {
        // Implementación futura
    }

    private void setupCommonListeners() {
        // View All Expenses
        viewAllExpensesCard.setOnClickListener(v -> navigateTo(ExpenseListActivity.class));

        // Bottom Navigation
        navHome.setOnClickListener(v -> {
            NestedScrollView scrollView = findViewById(R.id.contentScroll);
            if (scrollView != null) {
                scrollView.smoothScrollTo(0, 0);
            }
        });

        navAnalysis.setOnClickListener(v -> navigateTo(AnalysisActivity.class));

        fabAddExpense.setOnClickListener(v -> {
            AnimationHelper.animateFABPress(v);
            handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), Constants.FAB_ANIM_DURATION + 50);
        });

        navAchievements.setOnClickListener(v -> navigateTo(AchievementsActivity.class));
        navSettings.setOnClickListener(v -> navigateTo(SettingsActivity.class));

        // Notification Button
        findViewById(R.id.notificationButton).setOnClickListener(v -> {
            // TODO: Show notifications
        });

        // Avatar Button
        findViewById(R.id.avatarButton).setOnClickListener(v -> navigateTo(ProfileActivity.class));
    }

    private void setBeginnerListeners(ActivityMainBinding b) {
        b.quickActionCard.quickActionButton.setOnClickListener(v -> navigateTo(AddExpenseActivity.class));
    }

    private void setIntermediateListeners(ActivityIntermediateUserBinding b) { }
    private void setAdvancedListeners(ActivityAdvancedUserBinding b) { }

    private void animateBeginnerCards(ActivityMainBinding b, UserProfile user) {
        View[] cards = {b.progressCard.getRoot(), b.tipCard, b.quickActionCard.getRoot(), b.nextGoalCard, b.viewAllExpensesCard};
        AnimationHelper.animateCardsEntrance(cards);

        if (progressBar != null) {
            AnimationHelper.animateProgressBar(user.getWeekProgress(), user.getWeekGoal(), progressBar);
        }
    }

    private void resetNavItem(ImageView icon, TextView text) {
        int gray = getResources().getColor(R.color.gray_400);
        icon.setColorFilter(gray);
        text.setTextColor(gray);
        text.setTypeface(null, Typeface.NORMAL);
    }

    private void activateNavItem(ImageView icon, TextView text) {
        int primary = getResources().getColor(R.color.primary);
        icon.setColorFilter(primary);
        text.setTextColor(primary);
        text.setTypeface(null, Typeface.BOLD);
    }

    private void updateBottomNavigation(String activeTab) {
        resetNavItem(navHomeIcon, navHomeText);
        resetNavItem(navAnalysisIcon, navAnalysisText);
        resetNavItem(navAchievementsIcon, navAchievementsText);
        resetNavItem(navSettingsIcon, navSettingsText);

        switch (activeTab) {
            case "home":        activateNavItem(navHomeIcon, navHomeText); break;
            case "analysis":    activateNavItem(navAnalysisIcon, navAnalysisText); break;
            case "achievements":activateNavItem(navAchievementsIcon, navAchievementsText); break;
            case "settings":    activateNavItem(navSettingsIcon, navSettingsText); break;
        }
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar datos a través del ViewModel (dispara checkWellbeing en 2º plano)
        viewModel.refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
