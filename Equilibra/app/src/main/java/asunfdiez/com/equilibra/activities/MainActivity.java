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

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

public class MainActivity extends AppCompatActivity {

    private SharedPrefsManager preferencesManager;
    UserProfile user;

    // Header
    private TextView userNameText;
    private TextView dayCountText;
    private TextView progressText;
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
    private CardView progressCard;
    private CardView tipCard;
    private CardView quickActionCard;
    private CardView nextGoalCard;

    // Common Cards
    private CardView viewAllExpensesCard;

    // UserProfile Data
    private int weekProgress = 4;
    private int weekGoal = 5;
    private String currentView = "beginner"; // beginner, intermediate, advanced

    // Handler
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
        loadUserData();
        loadCurrentView();

    }

    private void loadCurrentView() {
        switch (currentView) {
            case "intermediate":
                setContentView(R.layout.activity_intermediate_user);
                initIntermediateViews();
                setIntermediateListeners();
                break;
            case "advanced":
                setContentView(R.layout.activity_advanced_user);
                initAdvancedViews();
                setAdvancedListeners();
                break;
            case "beginner":
            default:
                setContentView(R.layout.activity_main);
                initBeginnerViews();
                setBeginnerListeners();
                animateBeginnerCards();
                break;
        }
        initCommonViews();
        setupCommonListeners();
    }

    private void initializeComponents() {
        preferencesManager =  SharedPrefsManager.getInstance(this);
    }

    private void initCommonViews() {
        // Header
        userNameText = findViewById(R.id.userNameText);
        dayCountText = findViewById(R.id.dayCountText);


        // Content Cards
        viewAllExpensesCard = findViewById(R.id.viewAllExpensesCard);

        // Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navAnalysis = findViewById(R.id.navAnalysis);
        navAchievements = findViewById(R.id.navAchievements);
        navSettings = findViewById(R.id.navSettings);
        fabAddExpense = findViewById(R.id.fabAddExpense);

        navHomeIcon = findViewById(R.id.navHomeIcon);
        navAnalysisIcon = findViewById(R.id.navAnalysisIcon);
        navAchievementsIcon = findViewById(R.id.navAchievementsIcon);
        navSettingsIcon = findViewById(R.id.navSettingsIcon);

        navHomeText = findViewById(R.id.navHomeText);
        navAnalysisText = findViewById(R.id.navAnalysisText);
        navAchievementsText = findViewById(R.id.navAchievementsText);
        navSettingsText = findViewById(R.id.navSettingsText);
        updateBottomNavigation("home");

        // Set user data
        userNameText.setText(getString(R.string.hello_user, user.getName()));
        dayCountText.setText(getString(R.string.day_count, user.getDayCount()));


    }

    private void initBeginnerViews() {
        // Progress Card
        progressCard = findViewById(R.id.progressCard);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.weeklyProgressBar);

        progressText.setText(getString(R.string.week_progress, user.getWeekProgress(), user.getWeekGoal()));


        // Content Cards
        tipCard = findViewById(R.id.tipCard);
        quickActionCard = findViewById(R.id.quickActionCard);
        nextGoalCard = findViewById(R.id.nextGoalCard);
        viewAllExpensesCard = findViewById(R.id.viewAllExpensesCard);
    }

    private void initIntermediateViews() {

    }

    private void initAdvancedViews() {

    }


    private void loadUserData() {
        user = preferencesManager.getUserProfile();
        weekProgress = user.getWeekProgress();
        weekGoal = user.getWeekGoal();
        currentView = user.getCurrentView();
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

    private void setBeginnerListeners() {
        // Quick Action
        findViewById(R.id.quickActionButton).setOnClickListener(v -> {
            navigateTo(AddExpenseActivity.class);
        });

    }
    private void setIntermediateListeners() {

    }
    private void setAdvancedListeners() {

    }

    private void animateBeginnerCards() {
        // Animate cards
        View[] cards = {progressCard, tipCard, quickActionCard, nextGoalCard, viewAllExpensesCard};
        AnimationHelper.animateCardsEntrance(cards);

        // Animate progress bar
        AnimationHelper.animateProgressBar(weekProgress, weekGoal, progressBar);
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
        if (userNameText != null) userNameText.setText(getString(R.string.hello_user, user.getName()));
        if (dayCountText != null) dayCountText.setText(getString(R.string.day_count, user.getDayCount()));
        // Actualiza aquí cualquier otro texto que pueda haber cambiado en ProfileActivity
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }
}
