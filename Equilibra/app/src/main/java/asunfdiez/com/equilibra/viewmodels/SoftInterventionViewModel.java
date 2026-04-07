package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.repositories.ExpenseRepository;
import asunfdiez.com.equilibra.controllers.WellbeingController;
import asunfdiez.com.equilibra.models.FinancialData;
import asunfdiez.com.equilibra.models.InterventionMessage;

public class SoftInterventionViewModel extends AndroidViewModel {

    private final ExpenseRepository expenseRepository;
    private final WellbeingController wellbeingController;

    private final MutableLiveData<FinancialData> financialData = new MutableLiveData<>();
    private final MutableLiveData<InterventionMessage> interventionMessage = new MutableLiveData<>();

    public SoftInterventionViewModel(@NonNull Application application) {
        super(application);
        expenseRepository = new ExpenseRepository(application);
        wellbeingController = new WellbeingController(application);
        loadData();
    }

    public void loadData() {
        new Thread(() -> {
            double income = expenseRepository.getTotalIncomeThisMonth();
            double expenses = expenseRepository.getTotalExpensesThisMonth();
            double percentage = (income > 0) ? (expenses / income) * 100 : 0;
            String topCategory = expenseRepository.getTopExpenseCategoryThisMonth();
            double topCategoryAmount = 0;
            if (topCategory != null) {
                topCategoryAmount = expenseRepository.getCategoryExpensesThisMonth(topCategory);
            }

            FinancialData data = new FinancialData(income, expenses, percentage, topCategory, topCategoryAmount);
            InterventionMessage message = wellbeingController.getInterventionMessage();

            financialData.postValue(data);
            interventionMessage.postValue(message);
        }).start();
    }

    public LiveData<FinancialData> getFinancialData() { return financialData; }
    public LiveData<InterventionMessage> getInterventionMessage() { return interventionMessage; }

    public void markAsSeen() {
        wellbeingController.markInterventionAsSeen();
    }


}
