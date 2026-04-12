package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.AchievementAdapter;
import asunfdiez.com.equilibra.databinding.ActivityAchievementsBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.models.Achievement;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.AchievementsViewModel;

/**
 * AchievementsActivity - Pantalla de logros y gamificación
 * Muestra el progreso del usuario y los logros desbloqueados
 */

public class AchievementsActivity extends AppCompatActivity implements AchievementAdapter.OnAchievementClickListener, BottomNavigationHandler {

    // Data Binding
    private ActivityAchievementsBinding binding;

    // ViewModel
    private AchievementsViewModel viewModel;

    // Adapter
    private AchievementAdapter adapter;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_achievements);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AchievementsViewModel.class);
        binding.setViewModel(viewModel);

        // Configurar UI
        setupToolbar();
        setupRecyclerView();
        setupFilters();
        setupObservers();
        setupListeners();
        setupBottomNavigation();


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
     * Configura el RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new AchievementAdapter(this, this);

        // 2. Configurar el Grid de 2 columnas
        binding.rvAchievements.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvAchievements.setAdapter(adapter);
    }

    // método de la interfaz
    @Override
    public void onAchievementClick(Achievement achievement) {
        showAchievementDetails(achievement);
    }


    /**
     * Configura los filtros (Chips)
     */
    private void setupFilters() {
        binding.chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) {
                viewModel.setFilter(AchievementsViewModel.FilterType.ALL);
            } else if (checkedId == R.id.chipUnlocked) {
                viewModel.setFilter(AchievementsViewModel.FilterType.UNLOCKED);
            } else if (checkedId == R.id.chipLocked) {
                viewModel.setFilter(AchievementsViewModel.FilterType.LOCKED);
            }
        });
    }

    /**
     * Configura los observadores de LiveData
     */
    private void setupObservers() {
        // Observar logros filtrados
        viewModel.getFilteredAchievements().observe(this, achievements -> {
            if (achievements != null) {
                adapter.setAchievements(achievements);
                
                // Solo animar si hay elementos para mostrar
                if (!achievements.isEmpty()) {
                    binding.rvAchievements.post(() -> {
                        binding.rvAchievements.scheduleLayoutAnimation();
                    });
                }
            }
        });

        // Observar nuevos logros desbloqueados
        viewModel.getNewlyUnlockedAchievement().observe(this, achievement -> {
            if (achievement != null) {
                showUnlockedAchievementDialog(achievement);
            }
        });

        // Observar estado de carga
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (!isLoading) {
                // Detener animación del FAB si estaba rotando
                binding.fabRefresh.clearAnimation();
            }
        });
    }

    /**
     * Configura los listeners de botones
     */
    private void setupListeners() {
        // FAB para refrescar
        binding.fabRefresh.setOnClickListener(v -> {
            // Animar FAB
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_360));

            // Verificar logros
            viewModel.checkAchievements();

            // Feedback
            Snackbar.make(binding.getRoot(),
                    R.string.checking_achievements,
                    Snackbar.LENGTH_SHORT).show();
        });
    }

    private void setupBottomNavigation() {

        if (binding.bottomNav != null) {
            BottomNavigationBinding navBinding = DataBindingUtil.getBinding(binding.bottomNav.bottomNavigation);
            if (navBinding != null) {
                navBinding.setActiveTab("achievements");
                navBinding.setHandler(this);
            }
        }
    }

    /**
     * Muestra el diálogo de detalles de un logro
     */
    private void showAchievementDetails(Achievement achievement) {
        String message = achievement.getDescription() + "\n\n" +
                "Puntos: " + achievement.getPoints();

        if (achievement.isUnlocked()) {
            message += "\n\nDesbloqueado el " +
                    android.text.format.DateFormat.format("dd/MM/yyyy HH:mm",
                            achievement.getUnlockedAt());
        } else {
            message += "\n\n🔒 Aún no desbloqueado";
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(achievement.getTitle())
                .setMessage(message)
                .setIcon(achievement.isUnlocked() ?
                        R.drawable.ic_achievement_star :
                        R.drawable.ic_achievement_locked)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Muestra un diálogo animado cuando se desbloquea un nuevo logro
     */
    private void showUnlockedAchievementDialog(Achievement achievement) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("🎉 ¡Logro Desbloqueado!")
                .setMessage(achievement.getTitle() + "\n\n" +
                        achievement.getDescription() + "\n\n" +
                        "+" + achievement.getPoints() + " puntos")
                .setIcon(R.drawable.ic_achievement_unlocked)
                .setPositiveButton(R.string.awesome, null)
                .setCancelable(false)
                .show();

        // Reproducir animación/sonido (opcional)
        // playUnlockAnimation();
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
        // Limpiar callbacks del handler para evitar fugas
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onHomeClick() {
        navigateTo(MainActivity.class);
    }

    @Override
    public void onAnalysisClick() { navigateTo(AnalysisActivity.class);}

    @Override
    public void onAddExpenseClick(View view) {
        AnimationHelper.animateFABPress(view);
        handler.postDelayed(() -> navigateTo(AddExpenseActivity.class), Constants.FAB_ANIM_DURATION + 50);
    }

    @Override
    public void onAchievementsClick() {
        binding.contentScroll.smoothScrollTo(0, 0);
    }
    @Override
    public void onSettingsClick() {
        navigateTo(SettingsActivity.class);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}