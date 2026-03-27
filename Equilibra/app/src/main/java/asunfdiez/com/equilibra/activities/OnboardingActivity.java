package asunfdiez.com.equilibra.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import java.util.List;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Arrays;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.QuestionOptionAdapter;
import asunfdiez.com.equilibra.models.EmotionalState;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.models.QuestionOption;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.InputValidator;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.utils.DatePickerHelper;
import asunfdiez.com.equilibra.utils.SavingGoalValidator;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;


public class OnboardingActivity extends AppCompatActivity {

    private FrameLayout stepContainer;
    private OnboardingStep currentStep = OnboardingStep.EMOTIONAL;

    // Datos del usuario
    private final UserProfile profile = new UserProfile();
    private EmotionalState selectedEmotion;
    private String mainConcern = "";
    private String timeAvailable = "";
    private String savingGoal = "";
    private String savingAmount = "";
    private String savingDate = "";

    private int currentQuestionIndex = 0;

    private Handler handler;
    private QuestionOptionAdapter questionAdapter;
    private SharedPrefsManager sharedPrefsManager;

    private enum OnboardingStep {
        EMOTIONAL,
        VALIDATION,
        QUESTIONS,
        SAVING_GOAL,
        SUMMARY,
        MISSION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initializeComponents();
        hideSystemUI();
        showStep(OnboardingStep.EMOTIONAL);
    }

    private void initializeComponents() {
        handler = new Handler(Looper.getMainLooper());
        questionAdapter = new QuestionOptionAdapter();
        stepContainer = findViewById(R.id.stepContainer);
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    // ==================== NAVEGACIÓN ====================

    private void showStep(OnboardingStep step) {
        currentStep = step;
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
        View currentView = stepContainer.getChildCount() > 0
                ? stepContainer.getChildAt(0)
                : null;

        newView.setAlpha(0f);
        newView.setTranslationX(Constants.TRANSITION_DISTANCE);
        stepContainer.addView(newView);

        if (currentView != null) {
            AnimationHelper.animateViewExit(currentView);
        }

        AnimationHelper.animateViewEnter(newView);
    }



    // ==================== PASO 1: ESTADO EMOCIONAL ====================

    private View createEmotionalStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_emotional, null);

        ImageView heartIcon = view.findViewById(R.id.heartIcon);
        View iconBackground = view.findViewById(R.id.iconBackground);
        AnimationHelper.animateIcon(heartIcon, iconBackground);

        setupEmotionalOptions(view);

        LinearLayout optionsContainer = view.findViewById(R.id.optionsContainer);
        AnimationHelper.animateOptionsStagger(optionsContainer);

        return view;
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
            selectedEmotion = emotion;
            profile.setEmotion(emotion);
            AnimationHelper.animateCardSelection(v, () ->
                    showStep(OnboardingStep.VALIDATION));
        });
    }

    // ==================== PASO 2: VALIDACION ====================

    private View createValidationStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_validation, null);

        TextView emojiText = view.findViewById(R.id.emojiText);
        TextView validationMessage = view.findViewById(R.id.validationMessage);

        if (selectedEmotion != null) {
            emojiText.setText(selectedEmotion.emoji());
            validationMessage.setText(selectedEmotion.validation());
        }

        ImageView sparklesIcon = view.findViewById(R.id.sparklesIcon);
        View iconBackground = view.findViewById(R.id.iconBackground);
        AnimationHelper.animateSparklesIcon(sparklesIcon, iconBackground);

        CardView messageCard = view.findViewById(R.id.messageCard);
        AnimationHelper.animateCardScale(messageCard);

        MaterialButton continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(v, () -> {
                currentQuestionIndex = 0;
                showStep(OnboardingStep.QUESTIONS);
            });
        });

        return view;
    }

    // ==================== PASO 3: PREGUNTAS ====================

    private View createQuestionStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_question, null);

        updateProgressUI(view);
        setupQuestionContent(view);

        LinearLayout optionsContainer = view.findViewById(R.id.questionOptionsContainer);
        AnimationHelper.animateOptionsStagger(optionsContainer);

        return view;
    }

    private void updateProgressUI(View view) {
        TextView progressText = view.findViewById(R.id.progressText);
        View progressBar = view.findViewById(R.id.progressBar);

        String progress = getString(R.string.onboarding_progress_text,
                currentQuestionIndex + 1,
                Constants.TOTAL_QUESTIONS);

        progressText.setText(progress);

        float progressValue = (currentQuestionIndex + 1) / (float) Constants.TOTAL_QUESTIONS;
        AnimationHelper.animateProgressBar(progressBar, progressValue);
    }

    private void setupQuestionContent(View view) {
        TextView questionTitle = view.findViewById(R.id.questionTitle);
        ImageView questionIcon = view.findViewById(R.id.questionIcon);
        LinearLayout optionsContainer = view.findViewById(R.id.questionOptionsContainer);

        if (currentQuestionIndex == Constants.QUESTION_INDEX_FIRST) {
            questionTitle.setText(getString(R.string.question_1_title));
            questionIcon.setImageResource(R.drawable.ic_onb_target);
            setupFirstQuestion(optionsContainer);
        } else if (currentQuestionIndex == Constants.QUESTION_INDEX_SECOND) {
            questionTitle.setText(getString(R.string.question_2_title));
            questionIcon.setImageResource(R.drawable.ic_onb_clock);
            setupSecondQuestion(optionsContainer);
        }
    }

    private void setupFirstQuestion(LinearLayout container) {
        questionAdapter.setOnOptionSelectedListener((id, text) -> {
            profile.setMainConcern(text);
            handleFirstQuestionAnswer(id);
        });

        List <QuestionOption> options = QuestionOption.getFirstQuestions(this);
        questionAdapter.setOptions(options);
        questionAdapter.addOptionsToContainer(container, LayoutInflater.from(this));

    }

    private void setupSecondQuestion(LinearLayout container) {
        questionAdapter.setOnOptionSelectedListener((id, text) -> {
            profile.setTimeAvailable(text);
            handleSecondQuestionAnswer();
        });

        List <QuestionOption> options = QuestionOption.getSecondQuestions(this);;
        questionAdapter.setOptions(options);
        questionAdapter.addOptionsToContainer(container, LayoutInflater.from(this));
    }

    private void handleFirstQuestionAnswer(String answerId) {
        if (Constants.ANSWER_ID_SAVE_SPECIFIC.equals(answerId)) {
            currentQuestionIndex++;
            showStep(OnboardingStep.QUESTIONS);
        } else {
            proceedToNextStep();
        }
    }

    private void handleSecondQuestionAnswer() {
        if (Constants.ANSWER_ID_SAVE_SPECIFIC.equals(profile.getMainConcern())) {
            showStep(OnboardingStep.SAVING_GOAL);
        } else {
            showStep(OnboardingStep.SUMMARY);
        }
    }

    private void proceedToNextStep() {
        currentQuestionIndex++;
        if (currentQuestionIndex < Constants.TOTAL_QUESTIONS) {
            showStep(OnboardingStep.QUESTIONS);
        } else {
            showStep(OnboardingStep.SUMMARY);
        }
    }

    // ==================== PASO 4: ESTABLECER OBJETIVO ====================

    private View createSavingGoalStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_saving_goal, null);

        setupSavingGoalForm(view);

        return view;
    }

    private void setupSavingGoalForm(View view) {
        com.google.android.material.textfield.TextInputEditText goalInput =
                view.findViewById(R.id.goalInput);
        com.google.android.material.textfield.TextInputEditText amountInput =
                view.findViewById(R.id.amountInput);
        com.google.android.material.textfield.TextInputEditText dateInput =
                view.findViewById(R.id.dateInput);
        MaterialButton submitButton = view.findViewById(R.id.submitButton);

        dateInput.setOnClickListener(v ->
                new DatePickerHelper(this).showDatePicker(dateInput));

        SavingGoalValidator validator = new SavingGoalValidator(
                goalInput, amountInput, submitButton, handler);
        validator.setupValidation();

        submitButton.setOnClickListener(v -> {
            if (saveSavingGoalData(goalInput, amountInput, dateInput)) {
                AnimationHelper.animateButtonPress(v,
                        () -> showStep(OnboardingStep.SUMMARY));
            }
        });

        submitButton.setEnabled(false);
        submitButton.setAlpha(0.5f);
    }

    private boolean saveSavingGoalData(
            com.google.android.material.textfield.TextInputEditText goalInput,
            com.google.android.material.textfield.TextInputEditText amountInput,
            com.google.android.material.textfield.TextInputEditText dateInput) {

        String goal = goalInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        if (!InputValidator.isValidSavingGoalInputs(goal, amount)) {
            return false;
        }

        profile.setSavingGoal(goal);
        profile.setSavingAmount(amount);
        profile.setSavingDate(date);
        return true;
    }

    // ==================== PASO 5: RESUMEN ====================

    private View createSummaryStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_summary, null);

        View iconBackground = view.findViewById(R.id.iconBackground);
        AnimationHelper.animateCheckIcon(iconBackground);

        setupSummaryContent(view);

        MaterialButton continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(v, () -> showStep(OnboardingStep.MISSION));
        });

        return view;
    }

    private void setupSummaryContent(View view) {
        displayGoalSection(view);
        displaySavingAmountSection(view);
        displayEmotionSection(view);
    }

    private void displayGoalSection(View view) {
        if (profile.getSavingGoal() != null && !profile.getSavingGoal().isEmpty()) {
            LinearLayout section = view.findViewById(R.id.goalSection);
            TextView text = view.findViewById(R.id.goalText);
            section.setVisibility(View.VISIBLE);
            text.setText(profile.getSavingGoal());
        }
    }

    private void displaySavingAmountSection(View view) {
        if (profile.getSavingAmount() != null && !profile.getSavingAmount().isEmpty()) {
            try {
                double amount = Double.parseDouble(profile.getSavingAmount());
                displayAmountUI(view, amount);
            } catch (NumberFormatException e) {
                // ignorar formato inválido
            }
        }
    }

    private void displayAmountUI(View view, double amount) {
        LinearLayout amountSection = view.findViewById(R.id.savingAmountSection);
        TextView amountText = view.findViewById(R.id.savingAmountText);
        amountSection.setVisibility(View.VISIBLE);
        amountText.setText(String.format(Constants.CURRENCY_FORMAT, amount));

        double monthlyAmount = amount / Constants.MONTHLY_DIVISION_FACTOR;
        LinearLayout monthlySection = view.findViewById(R.id.monthlySavingSection);
        TextView monthlyText = view.findViewById(R.id.monthlySavingText);
        monthlySection.setVisibility(View.VISIBLE);
        monthlyText.setText(String.format(Constants.CURRENCY_FORMAT, monthlyAmount));
    }

    private void displayEmotionSection(View view) {
        if (selectedEmotion != null) {
            TextView emotionEmoji = view.findViewById(R.id.emotionEmoji);
            TextView emotionText = view.findViewById(R.id.emotionText);
            emotionEmoji.setText(selectedEmotion.emoji());
            emotionText.setText(selectedEmotion.text());
        }
    }

    // ==================== PASO 6: GUARDAR MISION ====================

    private View createMissionStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_mission, null);

        ImageView zapIcon = view.findViewById(R.id.zapIcon);
        View iconBackground = view.findViewById(R.id.iconBackground);
        AnimationHelper.animateZapIcon(zapIcon, iconBackground);

        MaterialButton startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(v, this::completeOnboarding);
        });

        return view;
    }

    private void completeOnboarding() {
        sharedPrefsManager.setOnboardingComplete(true);
        sharedPrefsManager.setOnboardingCompletedAt(System.currentTimeMillis());
        sharedPrefsManager.saveUserProfile(profile);
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    // ==================== GUARDAR PERFIL ====================

    private void saveUserProfile() {
        profile.setEmotionalState(selectedEmotion);
        profile.setMainConcern(mainConcern);
        profile.setTimeAvailable(timeAvailable);
        profile.setSavingGoal(savingGoal);
        profile.setSavingAmount(savingAmount);
        profile.setSavingDate(savingDate);

        sharedPrefsManager.saveUserProfile(profile);
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