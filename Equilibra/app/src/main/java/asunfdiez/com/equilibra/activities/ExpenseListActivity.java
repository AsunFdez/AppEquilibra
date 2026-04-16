package asunfdiez.com.equilibra.activities;

import android.app.Dialog;
import android.content.Intent;
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
import asunfdiez.com.equilibra.databinding.DialogDetailExpenseBinding;
import asunfdiez.com.equilibra.models.ExpenseWithCategory;
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
        animateViews();
    }


    public void animateViews(){
        View[] cards = {binding.cardTotalAmount, binding.cardCalendar, binding.rvExpenses};
        AnimationHelper.animateCardsEntrance(cards);
    }

    private void setupRecyclerViews() {
        // RecyclerView de Gastos
        expenseAdapter = new ExpenseAdapter(this, this);
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExpenses.setAdapter(expenseAdapter);

        // RecyclerView de Calendario con listener
        calendarAdapter = new CalendarAdapter(day -> viewModel.onDateSelected(day));
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

        viewModel.getSelectedDateTimestamp().observe(this, timestamp -> {
            if (timestamp == null) {
                calendarAdapter.setSelectedDay(-1);
            } else {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTimeInMillis(timestamp);
                calendarAdapter.setSelectedDay(cal.get(java.util.Calendar.DAY_OF_MONTH));
            }
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


    @Override
    public void onExpenseClick(ExpenseWithCategory expense) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        DialogDetailExpenseBinding dialogBinding = DialogDetailExpenseBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnConfirmExpense.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.setViewModel(viewModel);
        dialogBinding.setExpense(expense);
        dialog.show();
    }


    @Override
    public void onExpenseEdit(ExpenseWithCategory expense) {
        // Abrir AddExpenseActivity en modo edición
        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("expense_id", expense.getExpense().getId());
        startActivity(intent);
    }

    @Override
    public void onExpenseDelete(ExpenseWithCategory expense) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        DialogDeleteConfirmationBinding dialogBinding = DialogDeleteConfirmationBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnDelete.setOnClickListener(v -> {
            viewModel.deleteExpense(expense.getExpense());
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadData();
        animateViews();
    }

}