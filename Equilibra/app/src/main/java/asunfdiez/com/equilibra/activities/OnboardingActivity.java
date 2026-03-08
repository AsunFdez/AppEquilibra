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


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Arrays;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.QuestionOptionAdapter;
import asunfdiez.com.equilibra.controllers.OnboardingController;
import asunfdiez.com.equilibra.models.EmotionalState;
import asunfdiez.com.equilibra.models.OnboardingData;
import asunfdiez.com.equilibra.models.QuestionOption;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.InputValidator;
import asunfdiez.com.equilibra.utils.OnboardingConstants;
import asunfdiez.com.equilibra.utils.DatePickerHelper;
import asunfdiez.com.equilibra.utils.SavingGoalValidator;

public class OnboardingActivity extends AppCompatActivity {

    private FrameLayout stepContainer;
    private OnboardingStep currentStep = OnboardingStep.EMOTIONAL;

    private final OnboardingData data = new OnboardingData();
    private EmotionalState selectedEmotion;
    private int currentQuestionIndex = 0;

    private Handler handler;
    private AnimationHelper animationHelper;
    private OnboardingController controller;
    private QuestionOptionAdapter questionAdapter;

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
        animationHelper = new AnimationHelper();
        controller = new OnboardingController(this);
        questionAdapter = new QuestionOptionAdapter();
        stepContainer = findViewById(R.id.stepContainer);
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
        newView.setTranslationX(OnboardingConstants.TRANSITION_DISTANCE);
        stepContainer.addView(newView);

        if (currentView != null) {
            animateViewExit(currentView);
        }

        animateViewEnter(newView);
    }

    private void animateViewExit(View view) {
        view.animate()
                .alpha(0f)
                .translationX(-OnboardingConstants.TRANSITION_DISTANCE)
                .setDuration(OnboardingConstants.DURATION_MEDIUM)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        stepContainer.removeView(view);
                    }
                })
                .start();
    }

    private void animateViewEnter(View view) {
        view.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(OnboardingConstants.DURATION_MEDIUM)
                .setStartDelay(OnboardingConstants.DELAY_MEDIUM)
                .start();
    }

    // ==================== PASO 1: EMOTIONAL ====================

    private View createEmotionalStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_emotional, null);

        ImageView heartIcon = view.findViewById(R.id.heartIcon);
        View iconBackground = view.findViewById(R.id.iconBackground);
        animationHelper.animateIcon(heartIcon, iconBackground);

        setupEmotionalOptions(view);

        LinearLayout optionsContainer = view.findViewById(R.id.optionsContainer);
        animationHelper.animateOptionsStagger(optionsContainer);

        return view;
    }

    private void setupEmotionalOptions(View parent) {
        EmotionalState[] emotions = {
                new EmotionalState("tranquilo",
                        getString(R.string.emotion_tranquilo_text),
                        getString(R.string.emotion_tranquilo_emoji),
                        getString(R.string.validation_calm)),
                new EmotionalState("ansioso",
                        getString(R.string.emotion_ansioso_text),
                        getString(R.string.emotion_ansioso_emoji),
                        getString(R.string.validation_anxious)),
                new EmotionalState("estresado",
                        getString(R.string.emotion_estresado_text),
                        getString(R.string.emotion_estresado_emoji),
                        getString(R.string.validation_stressed)),
                new EmotionalState("confundido",
                        getString(R.string.emotion_confundido_text),
                        getString(R.string.emotion_confundido_emoji),
                        getString(R.string.validation_confused))
        };

        int[] cardIds = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};

        for (int i = 0; i < emotions.length; i++) {
            setupEmotionalOption(parent, cardIds[i], emotions[i]);
        }
    }

    private void setupEmotionalOption(View parent, int cardId, final EmotionalState emotion) {
        CardView card = parent.findViewById(cardId);
        card.setOnClickListener(v -> {
            selectedEmotion = emotion;
            data.setEmotion(emotion.id());
            animationHelper.animateCardSelection(v, () -> showStep(OnboardingStep.VALIDATION));
        });
    }

    // ==================== PASO 2: VALIDATION ====================

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
        animationHelper.animateSparklesIcon(sparklesIcon, iconBackground);

        CardView messageCard = view.findViewById(R.id.messageCard);
        animationHelper.animateCardScale(messageCard);

        MaterialButton continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            animationHelper.animateButtonPress(v, () -> {
                currentQuestionIndex = 0;
                showStep(OnboardingStep.QUESTIONS);
            });
        });

        return view;
    }

    // ==================== PASO 3: QUESTIONS ====================

    private View createQuestionStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_question, null);

        updateProgressUI(view);
        setupQuestionContent(view);

        LinearLayout optionsContainer = view.findViewById(R.id.questionOptionsContainer);
        animationHelper.animateOptionsStagger(optionsContainer);

        return view;
    }

    private void updateProgressUI(View view) {
        TextView progressText = view.findViewById(R.id.progressText);
        View progressBar = view.findViewById(R.id.progressBar);

        String progress = "Pregunta " + (currentQuestionIndex + 1) + " de " +
                OnboardingConstants.TOTAL_QUESTIONS;
        progressText.setText(progress);

        float progressValue = (currentQuestionIndex + 1) / (float) OnboardingConstants.TOTAL_QUESTIONS;
        animationHelper.animateProgressBar(progressBar, progressValue);
    }

    private void setupQuestionContent(View view) {
        TextView questionTitle = view.findViewById(R.id.questionTitle);
        ImageView questionIcon = view.findViewById(R.id.questionIcon);
        LinearLayout optionsContainer = view.findViewById(R.id.questionOptionsContainer);

        if (currentQuestionIndex == OnboardingConstants.QUESTION_INDEX_FIRST) {
            questionTitle.setText(getString(R.string.question_1_title));
            questionIcon.setImageResource(R.drawable.ic_onb_target);
            setupFirstQuestion(optionsContainer);
        } else {
            questionTitle.setText(getString(R.string.question_2_title));
            questionIcon.setImageResource(R.drawable.ic_onb_clock);
            setupSecondQuestion(optionsContainer);
        }
    }

    private void setupFirstQuestion(LinearLayout container) {
        questionAdapter.setOnOptionSelectedListener((id, text) -> {
            data.setMainConcern(text);
            handleFirstQuestionAnswer(id);
        });

        QuestionOption[] options = {
                new QuestionOption("control", getString(R.string.q1_option_1)),
                new QuestionOption(OnboardingConstants.ANSWER_ID_SAVE_SPECIFIC,
                        OnboardingConstants.ANSWER_TEXT_SAVE_SPECIFIC),
                new QuestionOption("reduce-stress", getString(R.string.q1_option_3)),
                new QuestionOption("learn", getString(R.string.q1_option_4))
        };

        questionAdapter.setOptions(Arrays.asList(options));
        questionAdapter.addOptionsToContainer(container, LayoutInflater.from(this));
    }

    private void setupSecondQuestion(LinearLayout container) {
        questionAdapter.setOnOptionSelectedListener((id, text) -> {
            data.setTimeAvailable(text);
            handleSecondQuestionAnswer();
        });

        QuestionOption[] options = {
                new QuestionOption("5min", getString(R.string.q2_option_1)),
                new QuestionOption("10min", getString(R.string.q2_option_2)),
                new QuestionOption("15min", getString(R.string.q2_option_3))
        };

        questionAdapter.setOptions(Arrays.asList(options));
        questionAdapter.addOptionsToContainer(container, LayoutInflater.from(this));
    }

    private void handleFirstQuestionAnswer(String answerId) {
        if (OnboardingConstants.ANSWER_ID_SAVE_SPECIFIC.equals(answerId)) {
            currentQuestionIndex++;
            showStep(OnboardingStep.QUESTIONS);
        } else {
            proceedToNextStep();
        }
    }

    private void handleSecondQuestionAnswer() {
        if (OnboardingConstants.ANSWER_TEXT_SAVE_SPECIFIC.equals(data.getMainConcern())) {
            showStep(OnboardingStep.SAVING_GOAL);
        } else {
            showStep(OnboardingStep.SUMMARY);
        }
    }

    private void proceedToNextStep() {
        currentQuestionIndex++;
        if (currentQuestionIndex < OnboardingConstants.TOTAL_QUESTIONS) {
            showStep(OnboardingStep.QUESTIONS);
        } else {
            showStep(OnboardingStep.SUMMARY);
        }
    }

    // ==================== PASO 4: SAVING GOAL ====================

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
                animationHelper.animateButtonPress(v,
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

        data.setSavingGoal(goal);
        data.setSavingAmount(amount);
        data.setSavingDate(date);
        return true;
    }

    // ==================== PASO 5: SUMMARY ====================

    private View createSummaryStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_summary, null);

        View iconBackground = view.findViewById(R.id.iconBackground);
        animationHelper.animateCheckIcon(iconBackground);

        setupSummaryContent(view);

        MaterialButton continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            animationHelper.animateButtonPress(v, () -> showStep(OnboardingStep.MISSION));
        });

        return view;
    }

    private void setupSummaryContent(View view) {
        displayGoalSection(view);
        displaySavingAmountSection(view);
        displayEmotionSection(view);
    }

    private void displayGoalSection(View view) {
        if (data.getSavingGoal() != null && !data.getSavingGoal().isEmpty()) {
            LinearLayout section = view.findViewById(R.id.goalSection);
            TextView text = view.findViewById(R.id.goalText);
            section.setVisibility(View.VISIBLE);
            text.setText(data.getSavingGoal());
        }
    }

    private void displaySavingAmountSection(View view) {
        if (data.getSavingAmount() != null && !data.getSavingAmount().isEmpty()) {
            try {
                double amount = Double.parseDouble(data.getSavingAmount());
                displayAmountUI(view, amount);
            } catch (NumberFormatException e) {
                // Silenciosamente ignorar formato inválido
            }
        }
    }

    private void displayAmountUI(View view, double amount) {
        LinearLayout amountSection = view.findViewById(R.id.savingAmountSection);
        TextView amountText = view.findViewById(R.id.savingAmountText);
        amountSection.setVisibility(View.VISIBLE);
        amountText.setText(String.format(OnboardingConstants.CURRENCY_FORMAT, amount));

        double monthlyAmount = amount / OnboardingConstants.MONTHLY_DIVISION_FACTOR;
        LinearLayout monthlySection = view.findViewById(R.id.monthlySavingSection);
        TextView monthlyText = view.findViewById(R.id.monthlySavingText);
        monthlySection.setVisibility(View.VISIBLE);
        monthlyText.setText(String.format(OnboardingConstants.CURRENCY_FORMAT, monthlyAmount));
    }

    private void displayEmotionSection(View view) {
        if (selectedEmotion != null) {
            TextView emotionEmoji = view.findViewById(R.id.emotionEmoji);
            TextView emotionText = view.findViewById(R.id.emotionText);
            emotionEmoji.setText(selectedEmotion.emoji());
            emotionText.setText(selectedEmotion.text());
        }
    }

    // ==================== PASO 6: MISSION ====================

    private View createMissionStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.onboarding_step_mission, null);

        ImageView zapIcon = view.findViewById(R.id.zapIcon);
        View iconBackground = view.findViewById(R.id.iconBackground);
        animationHelper.animateZapIcon(zapIcon, iconBackground);

        MaterialButton startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            animationHelper.animateButtonPress(v, this::completeOnboarding);
        });

        return view;
    }

    private void completeOnboarding() {
        controller.saveOnboardingData(data);
        navigateToMainActivity();
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
        animationHelper.cleanup();
    }
}