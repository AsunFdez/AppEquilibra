package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.List;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.QuestionOptionAdapter;
import asunfdiez.com.equilibra.controllers.UserController;
import asunfdiez.com.equilibra.databinding.ActivityOnboardingBinding;
import asunfdiez.com.equilibra.databinding.OnboardingStepEmotionalBinding;
import asunfdiez.com.equilibra.databinding.OnboardingStepMissionBinding;
import asunfdiez.com.equilibra.databinding.OnboardingStepQuestionBinding;
import asunfdiez.com.equilibra.databinding.OnboardingStepSavingGoalBinding;
import asunfdiez.com.equilibra.databinding.OnboardingStepSummaryBinding;
import asunfdiez.com.equilibra.databinding.OnboardingStepValidationBinding;
import asunfdiez.com.equilibra.models.EmotionalState;
import asunfdiez.com.equilibra.models.QuestionOption;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.DatePickerHelper;
import asunfdiez.com.equilibra.utils.InputValidator;
import asunfdiez.com.equilibra.utils.SavingGoalValidator;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;
import asunfdiez.com.equilibra.viewmodels.OnboardingViewModel;
import asunfdiez.com.equilibra.viewmodels.OnboardingViewModel.OnboardingStep;


public class OnboardingActivity extends AppCompatActivity {

    private OnboardingViewModel viewModel;
    private ActivityOnboardingBinding activityBinding;

    private Handler handler;
    private QuestionOptionAdapter questionAdapter;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        initializeComponents();
        hideSystemUI();
        setupObservers();
        setupBackNavigation();
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!viewModel.previousStep()) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
        handler = new Handler(Looper.getMainLooper());
        questionAdapter = new QuestionOptionAdapter();
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
    }

    private void setupObservers() {

        viewModel.getCurrentStep().observe(this, step -> {
            if (step != null) {
                showStep(step);
            }
        });

        // Forzar el primer paso si es necesario
        if (viewModel.getCurrentStep().getValue() != null) {
            showStep(viewModel.getCurrentStep().getValue());
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    // ==================== NAVEGACIÓN ====================

    private void showStep(OnboardingStep step) {
        View newView = createViewForStep(step);

        if (newView != null) {
            transitionToView(newView);
        }
    }

    private View createViewForStep(OnboardingStep step) {

        switch (step) {
            case EMOTIONAL:
                return createEmotionalStep();
            case VALIDATION:
                return createValidationStep();
            case QUESTIONS:
                return createQuestionStep();
            case SAVING_GOAL:
                return createSavingGoalStep();
            case SUMMARY:
                return createSummaryStep();
            case MISSION:
                return createMissionStep();
            default:
                return null;
        }

    }

    private void transitionToView(View newView) {
        View currentView = activityBinding.stepContainer.getChildCount() > 0
                ? activityBinding.stepContainer.getChildAt(0)
                : null;

        newView.setAlpha(0f);
        newView.setTranslationX(Constants.TRANSITION_DISTANCE);
        activityBinding.stepContainer.addView(newView);

        if (currentView != null) {
            AnimationHelper.animateViewExit(currentView);
        }

        AnimationHelper.animateViewEnter(newView);
    }



    // ==================== PASO 1: ESTADO EMOCIONAL ====================

    private View createEmotionalStep() {
        OnboardingStepEmotionalBinding binding = OnboardingStepEmotionalBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        AnimationHelper.animateIcon(binding.heartIcon, binding.iconBackground);
        setupEmotionalOptions(binding.getRoot());
        AnimationHelper.animateOptionsStagger(binding.optionsContainer);

        return binding.getRoot();
    }

    private void setupEmotionalOptions(View parent) {
        List <EmotionalState> emotions = EmotionalState.getDefaultEmotions(this);

        int[] cardIds = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};

        for (int i = 0; i < emotions.size(); i++) {
            setupEmotionalOption(parent, cardIds[i], emotions.get(i));
        }
    }

    private void setupEmotionalOption(View parent, int cardId, final EmotionalState emotion) {
        CardView card = parent.findViewById(cardId);
        card.setOnClickListener(v -> {
            viewModel.setSelectedEmotion(emotion);
            AnimationHelper.animateCardSelection(v, () ->
                    viewModel.nextStep(OnboardingStep.VALIDATION));
        });
    }

    // ==================== PASO 2: VALIDACION ====================

    private View createValidationStep() {
        OnboardingStepValidationBinding binding = OnboardingStepValidationBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        AnimationHelper.animateSparklesIcon(binding.sparklesIcon, binding.iconBackground);
        AnimationHelper.animateCardScale(binding.messageCard);

        binding.continueButton.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(v, () -> {
                viewModel.setCurrentQuestionIndex(0);
                viewModel.nextStep(OnboardingStep.QUESTIONS);
            });
        });

        return binding.getRoot();
    }

    // ==================== PASO 3: PREGUNTAS ====================

    private View createQuestionStep() {
        OnboardingStepQuestionBinding binding = OnboardingStepQuestionBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        updateProgressUI(binding);
        setupQuestionContent(binding);

        AnimationHelper.animateOptionsStagger(binding.questionOptionsContainer);

        return binding.getRoot();
    }

    private void updateProgressUI(OnboardingStepQuestionBinding binding) {
        // Data binding handles most of this now
        int currentQuestionIndex = viewModel.getCurrentQuestionIndex().getValue() != null ? viewModel.getCurrentQuestionIndex().getValue() : 0;
        float progressValue = (currentQuestionIndex + 1) / (float) Constants.TOTAL_QUESTIONS;
        AnimationHelper.animateProgressBar(binding.progressBar, progressValue);
    }

    private void setupQuestionContent(OnboardingStepQuestionBinding binding) {
        int currentQuestionIndex = viewModel.getCurrentQuestionIndex().getValue() != null ? viewModel.getCurrentQuestionIndex().getValue() : 0;
        if (currentQuestionIndex == Constants.QUESTION_INDEX_FIRST) {
            binding.questionTitle.setText(getString(R.string.question_1_title));
            binding.questionIcon.setImageResource(R.drawable.ic_onb_target);
            setupFirstQuestion(binding.questionOptionsContainer);
        } else if (currentQuestionIndex == Constants.QUESTION_INDEX_SECOND) {
            binding.questionTitle.setText(getString(R.string.question_2_title));
            binding.questionIcon.setImageResource(R.drawable.ic_onb_clock);
            setupSecondQuestion(binding.questionOptionsContainer);
        }
    }

    private void setupFirstQuestion(LinearLayout container) {
        questionAdapter.setOnOptionSelectedListener((id, text) -> {
            viewModel.setMainConcern(id, text);
            handleFirstQuestionAnswer(id);
        });

        List <QuestionOption> options = QuestionOption.getFirstQuestions(this);
        questionAdapter.setOptions(options);
        questionAdapter.addOptionsToContainer(container, LayoutInflater.from(this));

    }

    private void setupSecondQuestion(LinearLayout container) {
        questionAdapter.setOnOptionSelectedListener((id, text) -> {
            viewModel.setTimeAvailable(id);
            handleSecondQuestionAnswer();
        });

        List <QuestionOption> options = QuestionOption.getSecondQuestions(this);;
        questionAdapter.setOptions(options);
        questionAdapter.addOptionsToContainer(container, LayoutInflater.from(this));
    }

    private void handleFirstQuestionAnswer(String answerId) {
        if (Constants.ANSWER_ID_SAVE_SPECIFIC.equals(answerId)) {
            int currentQuestionIndex = viewModel.getCurrentQuestionIndex().getValue() != null ? viewModel.getCurrentQuestionIndex().getValue() : 0;
            viewModel.setCurrentQuestionIndex(currentQuestionIndex + 1);
            viewModel.nextStep(OnboardingStep.QUESTIONS);
        } else {
            proceedToNextStep();
        }
    }

    private void handleSecondQuestionAnswer() {
        if (Constants.ANSWER_ID_SAVE_SPECIFIC.equals(viewModel.getProfile().getMainConcernId())) {
            viewModel.nextStep(OnboardingStep.SAVING_GOAL);
        } else {
            viewModel.nextStep(OnboardingStep.SUMMARY);
        }
    }

    private void proceedToNextStep() {
        int currentQuestionIndex = viewModel.getCurrentQuestionIndex().getValue() != null ? viewModel.getCurrentQuestionIndex().getValue() : 0;
        int nextIndex = currentQuestionIndex + 1;
        if (nextIndex < Constants.TOTAL_QUESTIONS) {
            viewModel.setCurrentQuestionIndex(nextIndex);
            viewModel.nextStep(OnboardingStep.QUESTIONS);
        } else {
            viewModel.nextStep(OnboardingStep.SUMMARY);
        }
    }

    // ==================== PASO 4: ESTABLECER OBJETIVO ====================

    private View createSavingGoalStep() {
        OnboardingStepSavingGoalBinding binding = OnboardingStepSavingGoalBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        setupSavingGoalForm(binding);

        return binding.getRoot();
    }

    private void setupSavingGoalForm(OnboardingStepSavingGoalBinding binding) {
        Calendar calendar = Calendar.getInstance();
        binding.dateInput.setOnClickListener(v ->
                new DatePickerHelper(this).showDatePicker(binding.dateInput, calendar));

        SavingGoalValidator validator = new SavingGoalValidator(
                binding.goalInput, binding.amountInput, binding.submitButton, handler);
        validator.setupValidation();

        binding.submitButton.setOnClickListener(v -> {
            if (saveSavingGoalData(binding)) {
                AnimationHelper.animateButtonPress(v,
                        () -> viewModel.nextStep(OnboardingStep.SUMMARY));
            }
        });

        binding.submitButton.setEnabled(false);
        binding.submitButton.setAlpha(0.5f);
    }

    private boolean saveSavingGoalData(OnboardingStepSavingGoalBinding binding) {
        String goal = binding.goalInput.getText().toString().trim();
        String amount = binding.amountInput.getText().toString().trim();
        String date = binding.dateInput.getText().toString().trim();

        if (!InputValidator.isValidSavingGoalInputs(goal, amount)) {
            return false;
        }

        viewModel.setSavingGoal(goal, amount, date);
        return true;
    }

    // ==================== PASO 5: RESUMEN ====================

    private View createSummaryStep() {
        OnboardingStepSummaryBinding binding = OnboardingStepSummaryBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        binding.setUser(viewModel.getProfile());
        binding.setViewModel(viewModel);

        AnimationHelper.animateCheckIcon(binding.iconBackground);

        binding.continueButton.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(v, () -> viewModel.nextStep(OnboardingStep.MISSION));
        });

        return binding.getRoot();
    }

    // ==================== PASO 6: GUARDAR MISION ====================

    private View createMissionStep() {
        OnboardingStepMissionBinding binding = OnboardingStepMissionBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        AnimationHelper.animateZapIcon(binding.zapIcon, binding.iconBackground);

        binding.startButton.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(v, this::completeOnboarding);
        });

        return binding.getRoot();
    }


    private void completeOnboarding() {
        sharedPrefsManager.setOnboardingComplete(false);
        UserProfile profile = viewModel.getProfile();
        android.util.Log.d("OnboardingActivity_completeOnboarding Profile", profile.toString());

        UserController userController = new UserController(getApplicationContext());
        userController.updateUser(profile, () -> {
            sharedPrefsManager.setOnboardingComplete(true);
            sharedPrefsManager.setOnboardingCompletedAt(System.currentTimeMillis());
            navigateToMainActivity();
        });
    }
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    private void cleanup() {
        handler.removeCallbacksAndMessages(null);
        AnimationHelper.cleanup();
    }
}