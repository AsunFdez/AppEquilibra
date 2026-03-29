package asunfdiez.com.equilibra.activities;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import asunfdiez.com.equilibra.R;

public class MainActivity extends AppCompatActivity {


    private static final long FAB_ANIM_DURATION = 100;
    private static final long CARD_STAGGER = 100;

    // Views
    private TextView userNameText;
    private TextView dayCountText;
    private TextView progressText;
    private View progressBar;
    private TextView tipText;

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

    // Cards
    private CardView progressCard;
    private CardView tipCard;
    private CardView quickActionCard;
    private CardView nextGoalCard;
    private CardView viewAllExpensesCard;

    // Dummy Data
    private int weekProgress = 4;
    private int weekGoal = 5;
    private String userName = "Ana";
    private int dayCount = 1;
    private String currentView = "beginner"; // beginner, intermediate, advanced



    // Handler
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initViews();

        // Load user data
        loadUserData();

        // Setup listeners
        setupListeners();

        // Animate cards
        animateCardsEntrance();

        // Animate progress bar
        //animateProgressBar();
    }

    private void initViews() {
        // Header
        userNameText = findViewById(R.id.userNameText);
        dayCountText = findViewById(R.id.dayCountText);

        // Progress Card
        progressCard = findViewById(R.id.progressCard);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);

        // Other Cards
        tipCard = findViewById(R.id.tipCard);
        quickActionCard = findViewById(R.id.quickActionCard);
        nextGoalCard = findViewById(R.id.nextGoalCard);
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
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("equilibra_prefs", MODE_PRIVATE);
        userName = prefs.getString("user_name", "Ana");
        dayCount = prefs.getInt("day_count", 1);
        currentView = prefs.getString("current_view", "beginner");
        weekProgress = prefs.getInt("week_progress", 4);
        weekGoal = prefs.getInt("week_goal", 5);

        // Update UI
        userNameText.setText(getString(R.string.hello_user, userName));
        dayCountText.setText(getString(R.string.day_count, dayCount));
        progressText.setText(getString(R.string.week_progress, weekProgress, weekGoal));

        updateBottomNavigation("home");
    }

    private void setupListeners() {

        // Quick Action
        findViewById(R.id.quickActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(AddExpenseActivity.class);
            }
        });

        // View All Expenses
        viewAllExpensesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ExpenseListActivity.class);
            }
        });

        // Bottom Navigation
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on home, do nothing or scroll to top
                NestedScrollView scrollView = findViewById(R.id.contentScroll);
                scrollView.smoothScrollTo(0, 0);
            }
        });

        navAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(AnalysisActivity.class);
            }
        });

        fabAddExpense.setOnClickListener(v -> {
            animateFABPress(v);
            handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), FAB_ANIM_DURATION + 50);
        });


        navAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(AchievementsActivity.class);
            }
        });

        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ProfileActivity.class);
                //navigateTo(SettingsActivity.class);
            }
        });

        // Notification Button
        findViewById(R.id.notificationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Show notifications
            }
        });

        // Avatar Button
        findViewById(R.id.avatarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ProfileActivity.class);
            }
        });
    }


    private void changeView(String newView) {
        if (currentView.equals(newView)) return;

        currentView = newView;

        // Save preference
        SharedPreferences prefs = getSharedPreferences("equilibra_prefs", MODE_PRIVATE);
        prefs.edit().putString("current_view", currentView).apply();

        // TODO: Update content based on view level

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


    private void animateCardsEntrance() {
        View[] cards = {progressCard, tipCard, quickActionCard, nextGoalCard, viewAllExpensesCard};

        for (int i = 0; i < cards.length; i++) {
            final View card = cards[i];
            final int delay = i * 100;

            card.setAlpha(0f);
            card.setTranslationY(20f);

            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(delay)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    private void animateProgressBar() {
        float progressPercentage = ((float) weekProgress / weekGoal);

        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        final int targetWidth = (int) (progressBar.getParent() instanceof View
                ? ((View) progressBar.getParent()).getWidth() * progressPercentage
                : 0);

        progressBar.post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(0, targetWidth);
                animator.setDuration(1000);
                animator.setStartDelay(300);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
                        params.width = (int) animation.getAnimatedValue();
                        progressBar.setLayoutParams(params);
                    }
                });
                animator.start();
            }
        });
    }

    private void animateFABPress(View fab) {
        fab.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }
}
