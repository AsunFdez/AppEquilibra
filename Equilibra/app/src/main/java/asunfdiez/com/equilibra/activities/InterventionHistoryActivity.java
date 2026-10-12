package asunfdiez.com.equilibra.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.InterventionLogAdapter;
import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.databinding.ActivityInterventionLogHistoryBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.databinding.DialogDetailExpenseBinding;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;
import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.InterventionHistoryViewModel;

public class InterventionHistoryActivity extends AppCompatActivity implements BottomNavigationHandler, InterventionLogAdapter.OnLogClickListener {

    private ActivityInterventionLogHistoryBinding binding;
    private InterventionHistoryViewModel viewModel;
    private InterventionLogAdapter adapter;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intervention_log_history);
        viewModel = new ViewModelProvider(this).get(InterventionHistoryViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        setupBottomNavigation();
        setupRecyclerViews();
        setupListeners();
        setupObservers();

    }

    private void setupRecyclerViews() {
        // RecyclerView de Gastos
        adapter = new InterventionLogAdapter(this, this);
        binding.interventionLogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.interventionLogsRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onInterventionLogClick(InterventionLog log) {
        if (log.getExpenseId() <= 0) {
            Toast.makeText(this, "No hay gasto asociado a esta intervención", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            ExpenseController expenseController = new ExpenseController(this);
            ExpenseWithCategory expenseWithCategory = expenseController.getExpenseWithCategoryById(log.getExpenseId());
            runOnUiThread(() -> {
                if (expenseWithCategory != null && expenseWithCategory.getExpense() != null) {
                    showExpenseDetailDialog(expenseWithCategory);
                } else {
                    Toast.makeText(this, "No se encontró el gasto asociado", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showExpenseDetailDialog(ExpenseWithCategory expense) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        DialogDetailExpenseBinding dialogBinding = DialogDetailExpenseBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.btnConfirmExpense.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.setExpense(expense);
        dialog.show();
    }

    @Override
    public void onLogDelete(InterventionLog log) {
        viewModel.deleteLog(log);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupObservers() {
        viewModel.getInterventions().observe(this, logs -> {
            adapter.setInterventionLogs(logs);
        });
    }

    private void setupBottomNavigation() {

        if (binding.bottomNav != null) {
            BottomNavigationBinding navBinding = DataBindingUtil.getBinding(binding.bottomNav.bottomNavigation);
            if (navBinding != null) {
                navBinding.setActiveTab("none");
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
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }



}