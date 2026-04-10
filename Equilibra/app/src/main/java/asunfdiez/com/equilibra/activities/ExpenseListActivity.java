package asunfdiez.com.equilibra.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.CalendarAdapter;
import asunfdiez.com.equilibra.adapters.ExpenseAdapter;
import asunfdiez.com.equilibra.databinding.ActivityExpenseListBinding;
import asunfdiez.com.equilibra.databinding.BottomNavigationBinding;
import asunfdiez.com.equilibra.databinding.DialogDeleteConfirmationBinding;
import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.BottomNavigationHandler;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.ExpenseListViewModel;

public class ExpenseListActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener, BottomNavigationHandler {

    private ActivityExpenseListBinding binding;
    private ExpenseListViewModel viewModel;
    private ExpenseAdapter expenseAdapter;
    private CalendarAdapter calendarAdapter;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense_list);
        viewModel = new ViewModelProvider(this).get(ExpenseListViewModel.class);
        
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupBottomNavigation();
        setupRecyclerViews();
        setupListeners();
        setupObservers();
    }

    private void setupRecyclerViews() {
        // RecyclerView de Gastos
        expenseAdapter = new ExpenseAdapter(this, this);
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExpenses.setAdapter(expenseAdapter);

        // RecyclerView de Calendario
        calendarAdapter = new CalendarAdapter();
        calendarAdapter.setCalendar(viewModel.getCurrentMonthCalendar());
        binding.rvCalendar.setAdapter(calendarAdapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnPrevMonth.setOnClickListener(v -> {
            viewModel.previousMonth();
            calendarAdapter.setCalendar(viewModel.getCurrentMonthCalendar());
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            viewModel.nextMonth();
            calendarAdapter.setCalendar(viewModel.getCurrentMonthCalendar());
        });



    }

    private void setupObservers() {
        viewModel.getExpenses().observe(this, expenses -> {
            expenseAdapter.setExpenses(expenses);
        });

        viewModel.getDaysWithExpenses().observe(this, daysMap -> {
            calendarAdapter.setDaysWithExpenses(daysMap);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadData();
    }

    @Override
    public void onExpenseClick(Expense expense) {
        // Abrir AddExpenseActivity en modo edición
        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("expense_id", expense.getId());
        startActivity(intent);
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    public void onExpenseDelete(Expense expense) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        DialogDeleteConfirmationBinding dialogBinding = DialogDeleteConfirmationBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnDelete.setOnClickListener(v -> {
            viewModel.deleteExpense(expense);
            dialog.dismiss();
        });

        dialog.show();
    }
}