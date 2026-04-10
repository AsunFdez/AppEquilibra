package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

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
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity implements BottomNavigationHandler {

    private MainActivityViewModel viewModel;
    private WellbeingController wellbeingController;
    private ProgressBar progressBar;

    // Content Cards
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

        viewModel.getAdvice().observe(this, advice -> {
            if (advice != null) {
                binding.setVariable(asunfdiez.com.equilibra.BR.advice, advice);
                // Esto forzará a la tipCard a actualizarse usando los BindingAdapters
                binding.executePendingBindings();
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
            binding.setVariable(asunfdiez.com.equilibra.BR.advice, viewModel.getAdvice().getValue());
            updateBottomNavigationBinding("home");
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
        binding.setVariable(asunfdiez.com.equilibra.BR.advice, viewModel.getAdvice().getValue());
        updateBottomNavigationBinding("home");
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

    private void updateBottomNavigationBinding(String activeTab) {
        try {
            // Buscamos el binding de bottomNav de forma dinámica si es posible, 
            // o simplemente accedemos si sabemos el ID en los layouts.
            // En nuestros layouts el include tiene el id bottomNav
            View bottomNavRoot = findViewById(R.id.bottomNav);
            if (bottomNavRoot != null) {
                BottomNavigationBinding navBinding = DataBindingUtil.getBinding(bottomNavRoot);
                if (navBinding != null) {
                    navBinding.setActiveTab(activeTab);
                    navBinding.setHandler(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCommonViews() {
        View root = binding.getRoot();

        // Content Cards
        viewAllExpensesCard = root.findViewById(R.id.viewAllExpensesCard);
    }

    private void initBeginnerViews(ActivityMainBinding b, UserProfile user) {
        progressBar = b.progressCard.weeklyProgressBar;
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
        if (viewAllExpensesCard != null) {
            viewAllExpensesCard.setOnClickListener(v -> navigateTo(ExpenseListActivity.class));
        }

        // Notification Button
        View notifBtn = findViewById(R.id.notificationButton);
        if (notifBtn != null) {
            notifBtn.setOnClickListener(v -> {
                // TODO: Show notifications
            });
        }

        // Avatar Button
        View avatarBtn = findViewById(R.id.avatarButton);
        if (avatarBtn != null) {
            avatarBtn.setOnClickListener(v -> navigateTo(ProfileActivity.class));
        }
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

    @Override
    public void onHomeClick() {
        NestedScrollView scrollView = findViewById(R.id.contentScroll);
        if (scrollView != null) {
            scrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    public void onAnalysisClick() {
        navigateTo(AnalysisActivity.class);
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
