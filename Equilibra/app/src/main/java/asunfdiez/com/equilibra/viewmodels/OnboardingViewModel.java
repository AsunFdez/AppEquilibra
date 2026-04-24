package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.Executors;

import asunfdiez.com.equilibra.controllers.UserProfileController;
import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.models.EmotionalState;
import asunfdiez.com.equilibra.models.SavingGoal;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

/**
 * ViewModel que orquestra el flujo del proceso de configuración inicial (onboarding).
 * 
 * <p>Mantiene el estado de progreso a través de los diferentes pasos, captura la 
 * información del perfil del usuario, su estado emocional y sus metas de ahorro iniciales. 
 * Gestiona el historial de navegación hacia atrás y coordina la persistencia final 
 * del perfil de usuario una vez completado el proceso.</p>
 */
public class OnboardingViewModel extends AndroidViewModel {
    /**
     * Enumeración de los diferentes pasos que componen el flujo de bienvenida.
     */
    public enum OnboardingStep { EMOTIONAL, VALIDATION, QUESTIONS, SAVING_GOAL, SUMMARY, MISSION }

    // Paso actual del flujo en el que se encuentra el usuario
    private final MutableLiveData<OnboardingStep> currentStep = new MutableLiveData<>(OnboardingStep.EMOTIONAL);
    
    // Historial para permitir la navegación hacia atrás de forma coherente
    private final Stack<OnboardingStep> stepHistory = new Stack<>();
    
    // Objeto temporal que recopila los datos del perfil
    private final UserProfile profile = new UserProfile();

    // Controlador para gestionar el almacenamiento del perfil
    private final UserProfileController controller;
    
    // Estado emocional seleccionado por el usuario
    private final MutableLiveData<EmotionalState> selectedEmotion = new MutableLiveData<>();
    
    // Índice de la pregunta actual dentro del paso QUESTIONS
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);

    // Meta de ahorro inicial configurada por el usuario
    private final MutableLiveData<SavingGoal> savingGoal = new MutableLiveData<>(new SavingGoal());// ...


    public OnboardingViewModel(@NonNull Application application) {
        super(application);
        // Force database creation in background while onboarding progresses
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(application).getOpenHelper().getWritableDatabase();
        });

        this.controller = new UserProfileController(getApplication());
    }

    public LiveData<OnboardingStep> getCurrentStep() { return currentStep; }

    public LiveData<SavingGoal> getSavingGoal() {
        return savingGoal;
    }

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

    public UserProfile getProfile() {
        return profile;
    }

    public void setMainConcern(String id, String text) {
        profile.setMainConcernId(id);
        profile.setMainConcern(text);
        android.util.Log.d("OnboardingViewModel", "Main concern set: " + id + " " + text);
    }

    public void setTimeAvailable(String id) {
        profile.setTimeAvailable(id);
        android.util.Log.d("OnboardingViewModel", "Time available set: " + id);
    }

    public void setSavingGoal(String goal, String amount, String date) {
        SavingGoal currentGoal = savingGoal.getValue();
        if (currentGoal != null) {
            currentGoal.setUserId(profile.getId());
            currentGoal.setName(goal);
            
            double targetAmount = 0.0;
            try {
                targetAmount = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                targetAmount = 0.0;
            }
            currentGoal.setTargetAmount(targetAmount);
            currentGoal.setDeadline(date);

            // Cálculo de meses y ahorro mensual
            int months = Constants.MONTHLY_DIVISION_FACTOR; // Default
            if (date != null && !date.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
                    Date deadlineDate = sdf.parse(date);
                    if (deadlineDate != null) {
                        Calendar startCalendar = Calendar.getInstance();
                        Calendar endCalendar = Calendar.getInstance();
                        endCalendar.setTime(deadlineDate);

                        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                        
                        // Si el día del mes de fin es menor que el de inicio, restamos un mes si queremos meses completos,
                        // pero para ahorro suele ser más seguro redondear hacia arriba o dejar la diferencia bruta.
                        // Usaremos al menos 1 mes.
                        months = Math.max(1, diffMonth);
                    }
                } catch (ParseException e) {
                    android.util.Log.e("OnboardingViewModel", "Error parsing date: " + date, e);
                }
            }
            
            currentGoal.setMonthsRemaining(months);
            currentGoal.setMonthlySavingAmount(targetAmount / months);

            // Notificar al LiveData para que la UI se entere
            savingGoal.setValue(currentGoal);
            controller.saveSavingGoal(currentGoal, null);
        }
        android.util.Log.d("OnboardingViewModel", "Saving goal set: " + goal + " Months: " + currentGoal.getMonthsRemaining());
    }

    public LiveData<EmotionalState> getSelectedEmotion() { return selectedEmotion; }
    public void setSelectedEmotion(EmotionalState emotion) {
        selectedEmotion.setValue(emotion);
        profile.setEmotionalState(emotion);
        android.util.Log.d("OnboardingViewModel", "Selected emotion: " + emotion.toString());
    }
    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    public void setCurrentQuestionIndex(int index) {
        currentQuestionIndex.setValue(index);
    }


}
