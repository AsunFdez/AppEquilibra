package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.Stack;
import java.util.concurrent.Executors;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.EmotionalState;
import asunfdiez.com.equilibra.models.UserProfile;

public class OnboardingViewModel extends AndroidViewModel {
    public enum OnboardingStep { EMOTIONAL, VALIDATION, QUESTIONS, SAVING_GOAL, SUMMARY, MISSION }

    private final MutableLiveData<OnboardingStep> currentStep = new MutableLiveData<>(OnboardingStep.EMOTIONAL);
    private final Stack<OnboardingStep> stepHistory = new Stack<>();
    private final UserProfile profile = new UserProfile();
    private final MutableLiveData<EmotionalState> selectedEmotion = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);

    public OnboardingViewModel(@NonNull Application application) {
        super(application);
        // Force database creation in background while onboarding progresses
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(application).getOpenHelper().getWritableDatabase();
        });
    }

    public LiveData<OnboardingStep> getCurrentStep() { return currentStep; }

    public void nextStep(OnboardingStep step) {
        if (currentStep.getValue() != null) stepHistory.push(currentStep.getValue());
        currentStep.setValue(step);
    }

    public boolean previousStep() {
        if (!stepHistory.isEmpty()) {
            currentStep.setValue(stepHistory.pop());
            return true;
        }
        return false;
    }

    public UserProfile getProfile() { return profile; }

    public void setMainConcern(String id, String text) {
        profile.setMainConcernId(id);
        profile.setMainConcern(text);
    }

    public void setTimeAvailable(String id) {
        profile.setTimeAvailable(id);
    }

    public void setSavingGoal(String goal, String amount, String date) {
        profile.setSavingGoal(goal);
        profile.setSavingAmount(amount);
        profile.setSavingDate(date);
    }

    public LiveData<EmotionalState> getSelectedEmotion() { return selectedEmotion; }
    public void setSelectedEmotion(EmotionalState emotion) {
        selectedEmotion.setValue(emotion);
        profile.setEmotionalState(emotion);
    }
    public LiveData<Integer> getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(int index) { currentQuestionIndex.setValue(index); }

}
