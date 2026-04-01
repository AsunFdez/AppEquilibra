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

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityAdvancedUserBinding;
import asunfdiez.com.equilibra.databinding.ActivityIntermediateUserBinding;
import asunfdiez.com.equilibra.databinding.ActivityMainBinding;
import asunfdiez.com.equilibra.databinding.ProgressCardBinding;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

public class MainActivity extends AppCompatActivity {

    private SharedPrefsManager preferencesManager;
    UserProfile user;
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

    // Beginner Cards
    private ProgressCardBinding progressCard;

    // Common Cards
    private CardView viewAllExpensesCard;

    // UserProfile Data
    private int weekProgress = 0;
    private int weekGoal = 5;
    private String currentView = "beginner"; // beginner, intermediate, advanced

    // Handler
    private final Handler handler = new Handler();
    // dataBinding
    private ViewDataBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
        loadUserData();
        loadCurrentView();

    }

    private void loadCurrentView() {
        // Determinamos qué layout inflar
        int layoutId = switch (currentView) {
            case "intermediate" -> R.layout.activity_intermediate_user;
            case "advanced" -> R.layout.activity_advanced_user;
            default -> R.layout.activity_main;
        };

        // DataBindingUtil para inflar y establecer el contenido
        binding = DataBindingUtil.setContentView(this, layoutId);
        binding.setVariable(asunfdiez.com.equilibra.BR.user, user);
        binding.executePendingBindings();

        // Vincular vistas y listeners según el tipo de Binding
        if (binding instanceof ActivityMainBinding b) {
            initBeginnerViews(b);
            setBeginnerListeners(b);
            animateBeginnerCards(b);
        } else if (binding instanceof ActivityIntermediateUserBinding b) {
            initIntermediateViews(b);
            setIntermediateListeners(b);
        } else if (binding instanceof ActivityAdvancedUserBinding b) {
            initAdvancedViews(b);
            setAdvancedListeners(b);
        }

        // Vincular vistas y listeners comunes
        initCommonViews();
        setupCommonListeners();
    }

    private void initializeComponents() {
        preferencesManager =  SharedPrefsManager.getInstance(this);
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

    private void initBeginnerViews(ActivityMainBinding b) {
        // Progress Card
        progressCard = b.progressCard;
        progressBar = b.progressCard.weeklyProgressBar;
        b.progressCard.progressTitle.setText(getString(R.string.weekly_progress_title));
        b.progressCard.progressText.setText(getString(R.string.week_progress, user.getWeekProgress(), user.getWeekGoal()));
    }

    private void initIntermediateViews(ActivityIntermediateUserBinding b) {

    }

    private void initAdvancedViews(ActivityAdvancedUserBinding b) {

    }


    private void loadUserData() {
        user = preferencesManager.getUserProfile();
        weekProgress = user.getWeekProgress();
        weekGoal = user.getWeekGoal();
        currentView = user.getCurrentView();
        // calcular días transcurridos
        int days = (int) ((System.currentTimeMillis() - user.getCreatedAt()) / (1000 * 60 * 60 * 24)) + 1;
        user.setDayCount(days);
        preferencesManager.saveUserProfile(user);
    }

    private void setupCommonListeners() {

        // View All Expenses
        viewAllExpensesCard.setOnClickListener(v -> {
                navigateTo(ExpenseListActivity.class);
        });

        // Bottom Navigation
        navHome.setOnClickListener(v -> {
                // Already on home, do nothing or scroll to top
                NestedScrollView scrollView = findViewById(R.id.contentScroll);
                scrollView.smoothScrollTo(0, 0);
        });

        navAnalysis.setOnClickListener(v -> {
                navigateTo(AnalysisActivity.class);
        });

        fabAddExpense.setOnClickListener(v -> {
            AnimationHelper.animateFABPress(v);
            handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), Constants.FAB_ANIM_DURATION + 50);
        });


        navAchievements.setOnClickListener(v -> {
                navigateTo(AchievementsActivity.class);
        });

        navSettings.setOnClickListener ( v -> {
             navigateTo(SettingsActivity.class);
        });

        // Notification Button
        findViewById(R.id.notificationButton).setOnClickListener(v -> {
                // TODO: Show notifications
        });

        // Avatar Button
        findViewById(R.id.avatarButton).setOnClickListener(v -> {
                navigateTo(ProfileActivity.class);
        });
    }

    private void setBeginnerListeners(ActivityMainBinding b) {
        // Quick Action
        b.quickActionCard.quickActionButton.setOnClickListener(v -> {
            navigateTo(AddExpenseActivity.class);
        });

    }
    private void setIntermediateListeners(ActivityIntermediateUserBinding b) {
        // TODO
    }
    private void setAdvancedListeners(ActivityAdvancedUserBinding b) {
        // TODO
    }

    private void animateBeginnerCards(ActivityMainBinding b) {
        // Animate cards
        View[] cards = {b.progressCard.getRoot(), b.tipCard, b.quickActionCard.getRoot(), b.nextGoalCard, b.viewAllExpensesCard};
        AnimationHelper.animateCardsEntrance(cards);

        // Animate progress bar
        if (progressBar != null) {
            AnimationHelper.animateProgressBar(weekProgress, weekGoal, progressBar);
        }
    }

    private void resetNavItem(ImageView icon, TextView text) {
        int gray = getResources().getColor(R.color.gray_400);
        icon.setColorFilter(gray);
        text.setTextColor(gray);
        text.setTypeface(null, Typeface.NORMAL);
    }

    private void activateNavItem(ImageView icon, TextView text) {
        int primary = getResources().getColor(R.color.equilibra_primary);
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
        super.onResume();// Guardamos la vista antigua antes de cargar los nuevos datos
        String previousView = currentView;

        loadUserData();

        // Solo re-inflar si cambió su tipo de vista (beginner/int/adv)
        if (!previousView.equals(currentView)) {
            loadCurrentView();
        } else {
            // Si la vista es la misma, solo actualizamos los datos de los textos
            // sin destruir los botones y listeners
            updateUI();
        }

        if (progressBar != null) {
            handler.postDelayed(() -> {
                AnimationHelper.animateProgressBar(weekProgress, weekGoal, progressBar);
            }, 200);
        }
    }

    // Crea este método para actualizar textos sin resetear el layout
    private void updateUI() {
        binding.setVariable(asunfdiez.com.equilibra.BR.user, user);
        binding.executePendingBindings();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }
}
