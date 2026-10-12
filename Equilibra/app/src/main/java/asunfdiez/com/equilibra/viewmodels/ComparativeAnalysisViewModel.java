package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.controllers.AnalyticsController;
import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.models.CategoryAnalysis;
import asunfdiez.com.equilibra.models.ComparativeAnalysisState;
import asunfdiez.com.equilibra.models.UserProfile;

public class ComparativeAnalysisViewModel extends AndroidViewModel {

    private final AnalyticsController analyticsController;
    private final UserProfileController userProfileController;

    private final MutableLiveData<Boolean> isWeekly = new MutableLiveData<>(true);
    private final MutableLiveData<List<String>> periodOptions = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedPeriodIndex = new MutableLiveData<>(0);
    private final List<Calendar> periodStartDates = new ArrayList<>();
    
    private final MutableLiveData<List<ComparativeAnalysisState>> comparativeStates = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ComparativeAnalysisViewModel(@NonNull Application application) {
        super(application);
        this.analyticsController = new AnalyticsController(application);
        this.userProfileController = new UserProfileController(application);
        setupPeriods();
        loadComparativeData();
    }

    private void setupPeriods() {
        periodOptions.setValue(new ArrayList<>());
        periodStartDates.clear();
        List<String> options = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        boolean weekly = Boolean.TRUE.equals(isWeekly.getValue());

        if (weekly) {
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            for (int i = 0; i < 12; i++) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                
                Calendar start = (Calendar) cal.clone();
                periodStartDates.add(start);
                
                Calendar end = (Calendar) cal.clone();
                end.add(Calendar.DAY_OF_MONTH, 6);
                
                String label = String.format(Locale.getDefault(), "%02d/%02d - %02d/%02d",
                        start.get(Calendar.DAY_OF_MONTH), start.get(Calendar.MONTH) + 1,
                        end.get(Calendar.DAY_OF_MONTH), end.get(Calendar.MONTH) + 1);
                options.add(i == 0 ? getApplication().getString(R.string.this_week) : label);
                
                cal.add(Calendar.WEEK_OF_YEAR, -1);
            }
        } else {
            for (int i = 0; i < 12; i++) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                
                periodStartDates.add((Calendar) cal.clone());
                
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                if (monthName != null && !monthName.isEmpty()) {
                    monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
                }
                String label = monthName + " " + cal.get(Calendar.YEAR);
                options.add(i == 0 ? getApplication().getString(R.string.this_month) : label);
                
                cal.add(Calendar.MONTH, -1);
            }
        }
        periodOptions.setValue(options);
        selectedPeriodIndex.setValue(0);
    }

    public void setPeriodType(boolean weekly) {
        if (Boolean.TRUE.equals(isWeekly.getValue()) != weekly) {
            isWeekly.setValue(weekly);
            setupPeriods();
            loadComparativeData();
        }
    }

    public void selectPeriod(int index) {
        if (selectedPeriodIndex.getValue() != null && selectedPeriodIndex.getValue() != index) {
            selectedPeriodIndex.setValue(index);
            loadComparativeData();
        }
    }

    public void loadComparativeData() {
        isLoading.setValue(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            Integer indexVal = selectedPeriodIndex.getValue();
            int baseIndex = (indexVal != null) ? indexVal : 0;
            boolean weekly = Boolean.TRUE.equals(isWeekly.getValue());

            List<ComparativeAnalysisState> results = new ArrayList<>();
            
            // Cargar base y 2 anteriores
            for (int i = 0; i < 3; i++) {
                int currentIndex = baseIndex + i;
                if (currentIndex >= periodStartDates.size()) break;

                Calendar startCal = (Calendar) periodStartDates.get(currentIndex).clone();
                long start = startCal.getTimeInMillis();
                long end;

                if (weekly) {
                    startCal.add(Calendar.DAY_OF_MONTH, 6);
                } else {
                    startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
                startCal.set(Calendar.HOUR_OF_DAY, 23);
                startCal.set(Calendar.MINUTE, 59);
                startCal.set(Calendar.SECOND, 59);
                startCal.set(Calendar.MILLISECOND, 999);
                end = startCal.getTimeInMillis();

                results.add(fetchStateForPeriod(start, end, weekly, currentIndex));
            }
            
            comparativeStates.postValue(results);
            isLoading.postValue(false);
        });
    }

    private ComparativeAnalysisState fetchStateForPeriod(long start, long end, boolean weekly, int index) {
        double totalExpenses = analyticsController.getTotalExpensesInRange(start, end);
        double totalIncome = analyticsController.getTotalIncomeInRange(start, end);

        if (totalIncome == 0) {
            UserProfile profile = userProfileController.getUserProfileSync();
            if (profile != null && profile.getMonthlyIncome() != null) {
                totalIncome = profile.getMonthlyIncome();
                if (weekly) totalIncome /= 4.0;
            }
        }

        List<CategoryAnalysis> pieEntries = analyticsController.getTopCategoriesAnalysis(start, end, 5);
        for (CategoryAnalysis entry : pieEntries) {
            if (totalExpenses > 0) {
                entry.setPercentage((entry.getAmount() / totalExpenses) * 100);
            }
        }

        String label = periodOptions.getValue().get(index);
        return new ComparativeAnalysisState(label, totalExpenses, totalIncome, totalIncome - totalExpenses, pieEntries, totalExpenses > 0);
    }

    public LiveData<List<String>> getPeriodOptions() { return periodOptions; }
    public LiveData<Integer> getSelectedPeriodIndex() { return selectedPeriodIndex; }
    public LiveData<Boolean> getIsWeekly() { return isWeekly; }
    public LiveData<List<ComparativeAnalysisState>> getComparativeStates() { return comparativeStates; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}