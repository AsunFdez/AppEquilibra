package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.controllers.WellbeingController;
import asunfdiez.com.equilibra.database.DatabaseHelper;
import asunfdiez.com.equilibra.models.FinancialData;
import asunfdiez.com.equilibra.models.InterventionMessage;

public class SoftInterventionViewModel extends AndroidViewModel {

    private final DatabaseHelper databaseHelper;
    private final WellbeingController wellbeingController;

    private final MutableLiveData<FinancialData> financialData = new MutableLiveData<>();
    private final MutableLiveData<InterventionMessage> interventionMessage = new MutableLiveData<>();

    public SoftInterventionViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = DatabaseHelper.getInstance(application);
        wellbeingController = new WellbeingController(application);
        loadData();
    }

    public void loadData() {
        double income = databaseHelper.getTotalIncomeThisMonth();
        double expenses = databaseHelper.getTotalExpensesThisMonth();
        double percentage = (income > 0) ? (expenses / income) * 100 : 0;
        String topCategory = databaseHelper.getTopExpenseCategory();
        double topCategoryAmount = databaseHelper.getCategoryExpensesThisMonth(topCategory);

        financialData.setValue(new FinancialData(income, expenses, percentage, topCategory, topCategoryAmount));
        interventionMessage.setValue(wellbeingController.getInterventionMessage());
    }

    public LiveData<FinancialData> getFinancialData() { return financialData; }
    public LiveData<InterventionMessage> getInterventionMessage() { return interventionMessage; }

    public void markAsSeen() {
        wellbeingController.markInterventionAsSeen();
    }


}
