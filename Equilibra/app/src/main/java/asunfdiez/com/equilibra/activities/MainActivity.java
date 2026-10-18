package asunfdiez.com.equilibra.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import asunfdiez.com.equilibra.BR;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.BudgetController;
import asunfdiez.com.equilibra.controllers.WellbeingController;
import asunfdiez.com.equilibra.databinding.ActivityMainBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.models.Advice;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AchievementUnlockHelper;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.AdviceViewModel;
import asunfdiez.com.equilibra.viewmodels.GamificationViewModel;
import asunfdiez.com.equilibra.viewmodels.InterventionHistoryViewModel;
import asunfdiez.com.equilibra.viewmodels.MainViewModel;

/**
 * Actividad principal de la aplicación que sirve como panel de control (Dashboard).
 * 
 * <p>Esta clase coordina la visualización de los consejos diarios, el estado de bienestar financiero,
 * el progreso de gamificación y los accesos rápidos a las funciones principales. Utiliza múltiples
 * ViewModels para gestionar los diferentes componentes de la pantalla inicial.</p>
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationHandler {

    // Modelos de Vista (ViewModels)
    private MainViewModel viewModel;
    private AdviceViewModel adviceViewModel;
    private GamificationViewModel gamificationViewModel;

    private InterventionHistoryViewModel interventionHistoryViewModel;
    
    // Controlador de presupuesto para chequeos diarios
    private BudgetController budgetController;
    
    // Controlador de lógica de bienestar
    private WellbeingController wellbeingController;

    // Tarjeta para ver todos los gastos
    private CardView viewAllExpensesCard;

    // Estado de la vista actual
    private String currentViewType;

    // Manejador para tareas diferidas
    private final Handler handler = new Handler();
    
    // Vinculación de Datos (Data Binding)
    private ActivityMainBinding binding;

    // Datos del perfil de usuario
    private UserProfile lastUserProfile;
    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModels
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        adviceViewModel = new ViewModelProvider(this).get(AdviceViewModel.class);
        gamificationViewModel = new ViewModelProvider(this).get(GamificationViewModel.class);
        interventionHistoryViewModel = new ViewModelProvider(this).get(InterventionHistoryViewModel.class);

        // Pasar ViewModels al Data Binding
        binding.setViewModel(viewModel);
        binding.setAdviceViewModel(adviceViewModel);
        binding.setGamificationViewModel(gamificationViewModel);

        // Inicializar controller de presupuesto
        budgetController = new BudgetController(this);

        // Establecer user inicial para evitar parpadeo
        UserProfile initialUser = viewModel.getUserProfile().getValue();
        if (initialUser != null) {
            binding.setVariable(BR.user, initialUser);
            lastUserProfile = initialUser;
        }

        wellbeingController = new WellbeingController(this);

        setupObservers();
        updateBottomNavigationBinding("home");
    }

    private void setupObservers() {
        // Observar el perfil de usuario para manejar cambios de datos
        // Usar Transformations.distinctUntilChanged para evitar múltiples emisiones del mismo valor
        Transformations.distinctUntilChanged(viewModel.getUserProfile())
                .observe(this, user -> {
            if (user != null) {
                Log.d("MainActivity - setupObservers", "Perfil de usuario actualizado: " + user.toString());
                handleUserProfileUpdate(user);
            }
        });

        // Observar si hay registros de intervenciónes para mostrar notificación
        interventionHistoryViewModel.getHasLogs().observe(this, hasLogs ->
                binding.headerSection.notificationButton.setVisibility(
                        Boolean.TRUE.equals(hasLogs) ? View.VISIBLE : View.GONE));

        // Observar el tipo de vista desde SharedPreferences (vía ViewModel)
        viewModel.getCurrentView().observe(this, viewType -> {
            if (viewType != null) {
                if (binding == null || !viewType.equals(currentViewType)) {
                    currentViewType = viewType;
                    loadLayout(viewType, viewModel.getUserProfile().getValue());
                }
            }
        });

        // Observar acciones rápidas para configurar listeners condicionales
        adviceViewModel.getQuickAction().observe(this, quickAction -> {
            if (quickAction != null) {
                Context context = getApplicationContext();
                if (quickAction.equals(Advice.getCompleteUserProfile(context))) {
                    binding.beginnerUser.quickActionCard.setOnClickListener(v -> navigateTo(ProfileActivity.class));
                } else if (quickAction.equals(Advice.getCompleteBudget(context))) {
                    binding.beginnerUser.quickActionCard.setOnClickListener(v -> navigateTo(BudgetSettingsActivity.class));
                } else if (quickAction.equals(Advice.getQuickAction(context))) {
                    binding.beginnerUser.quickActionCard.setOnClickListener(v-> navigateTo(AnalysisActivity.class));
                }
            }
        });

        adviceViewModel.getNextGoal().observe(this, nextGoal -> {
            if (nextGoal != null) {
                Context context = getApplicationContext();
                if (nextGoal.equals(Advice.getCompleteBudget(context))) {
                    binding.beginnerUser.nextGoalCard.setOnClickListener(v -> navigateTo(BudgetSettingsActivity.class));
                } else if (nextGoal.equals(Advice.getRegisterExpenseToday(context))) {
                    binding.beginnerUser.nextGoalCard.setOnClickListener(v -> navigateTo(AddExpenseActivity.class));
                }
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

        // Observar desbloqueo de logros para mostrar celebración
        gamificationViewModel.getAchievementUnlocked().observe(this, achievement -> {
            if (achievement != null) {
                AchievementUnlockHelper.showUnlockDialog(MainActivity.this, achievement, null);
                gamificationViewModel.consumeAchievementUnlockedEvent();
            }
        });

        // Observar hitos de racha (weekly goals)
        gamificationViewModel.getStreakMilestoneReached().observe(this, milestone -> {
            if (milestone != null) {
                String title = "";
                String desc = "";
                int icon = R.drawable.ic_achievement_week;

                if (milestone.type == GamificationViewModel.StreakMilestone.Type.RECORDING) {
                    title = getString(R.string.milestone_recording_title);
                    desc = getString(R.string.milestone_recording_desc);
                    icon = R.drawable.ic_achievement_explorer;
                } else if (milestone.type == GamificationViewModel.StreakMilestone.Type.BUDGET) {
                    title = getString(R.string.milestone_budget_title);
                    desc = getString(R.string.milestone_budget_desc);
                    icon = R.drawable.ic_achievement_budget_week;
                }

                AchievementUnlockHelper.showMilestoneDialog(this, title, desc, icon, () -> {
                    gamificationViewModel.consumeStreakMilestoneEvent();
                });
            }
        });

    }

    private void handleUserProfileUpdate(UserProfile user) {
        // Comprobar si los datos son realmente diferentes antes de hacer nada
        if (lastUserProfile != null && lastUserProfile.equals(user)) {
            return; // Detener la ejecución si el objeto es el mismo
        }

        // Guardar el nuevo estado
        lastUserProfile = user;

        // Solo actualizamos los datos vinculados si la vista ya está cargada
        if (binding != null) {
            // Actualizar binding solo si hay cambios significativos en la UI
            binding.setVariable(asunfdiez.com.equilibra.BR.user, user);

            // Usar postDelayed para evitar múltiples executePendingBindings seguidos
            handler.removeCallbacksAndMessages(null); // Cancelar cualquier actualización pendiente
            handler.postDelayed(() -> {
                if (binding != null) {
                    binding.executePendingBindings();
                }
            }, 50); // Pequeño delay para agrupar múltiples actualizaciones
        }
    }


private void loadLayout(String viewType, UserProfile user) {
    switch (viewType) {
        case Constants.USER_LEVEL_INTERMEDIATE:
            setIntermediateLayout();
            break;
        case Constants.USER_LEVEL_ADVANCED:
            setAdvancedLayout();
            break;
        case Constants.USER_LEVEL_BEGINNER:
        default:
            setBeginnerLayout(user);
            break;
    }
    ;
    Log.d("MainActivity - Load layout", "Vista actualizada: " + viewType);
    binding.executePendingBindings();
    setupCommonListeners();

}

private void setBeginnerLayout(UserProfile user) {
    binding.beginnerUser.getRoot().setVisibility(View.VISIBLE);
    binding.advancedUser.getRoot().setVisibility(View.GONE);
    binding.intermediateUser.getRoot().setVisibility(View.GONE);
    // Usamos post para asegurar que la transición de visibilidad se procese
    // antes de iniciar las animaciones de los elementos internos.
    binding.beginnerUser.getRoot().post(this::animateBeginnerCards);
}

private void setAdvancedLayout() {
    binding.advancedUser.getRoot().setVisibility(View.VISIBLE);
    binding.beginnerUser.getRoot().setVisibility(View.GONE);
    binding.intermediateUser.getRoot().setVisibility(View.GONE);
    animateAdvancedCards();
}

private void setIntermediateLayout() {
    binding.intermediateUser.getRoot().setVisibility(View.VISIBLE);
    binding.beginnerUser.getRoot().setVisibility(View.GONE);
    binding.advancedUser.getRoot().setVisibility(View.GONE);
    animateIntermediateCards();
}

private void updateBottomNavigationBinding(String activeTab) {
    try {
        View bottomNavRoot = binding.bottomNav.getRoot();
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

private void setupCommonListeners() {
    // View All Expenses
    binding.viewAllExpensesCard.setOnClickListener(v -> navigateTo(ExpenseListActivity.class));

    // Notification Button
    binding.headerSection.notificationButton.setOnClickListener(
            v -> navigateTo(InterventionHistoryActivity.class)
    );

    // Avatar Button
    binding.headerSection.avatarButton.setOnClickListener(v -> navigateTo(ProfileActivity.class));

}


private void animateBeginnerCards() {
    View[] cards = {
            binding.beginnerUser.progressCard.getRoot(),
            binding.beginnerUser.tipCard,
            binding.beginnerUser.quickActionCard,
            binding.beginnerUser.nextGoalCard,
            binding.viewAllExpensesCard
    };
    AnimationHelper.animateCardsEntrance(cards);
}

private void animateAdvancedCards() {
    View[] cards = {binding.advancedUser.advancedSummary.getRoot(), binding.viewAllExpensesCard};
    AnimationHelper.animateCardsEntrance(cards);
}

private void animateIntermediateCards() {
    View[] cards = {binding.intermediateUser.intermediateBalance.getRoot(), binding.intermediateUser.budgetCard, binding.intermediateUser.achievementsPreview.getRoot(), binding.viewAllExpensesCard};
    AnimationHelper.animateCardsEntrance(cards);
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
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
}

@Override
protected void onResume() {
    super.onResume();
    // Refrescar datos a través del ViewModel (dispara checkWellbeing en 2º plano)
    if (viewModel != null) {
        viewModel.refreshData();
        // Verificar si se mantiene dentro del presupuesto diario y registrar racha
        if (budgetController != null) {
            budgetController.checkDailyBudgetStreak();
        }
    }
}

@Override
protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacksAndMessages(null);
}

}
