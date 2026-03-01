package asunfdiez.com.equilibra.utils;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.button.MaterialButton;

import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

public class SavingGoalValidator {
    private final TextInputEditText goalInput;
    private final TextInputEditText amountInput;
    private final MaterialButton submitButton;
    private final Handler handler;

    public SavingGoalValidator(TextInputEditText goalInput,
                               TextInputEditText amountInput,
                               MaterialButton submitButton,
                               Handler handler) {
        this.goalInput = goalInput;
        this.amountInput = amountInput;
        this.submitButton = submitButton;
        this.handler = handler;
    }

    public void setupValidation() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        goalInput.addTextChangedListener(watcher);
        amountInput.addTextChangedListener(watcher);
    }

    private void validateInputs() {
        String goal = goalInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();

        boolean isValid = InputValidator.isValidSavingGoalInputs(goal, amount);
        updateButtonState(isValid);
    }

    private void updateButtonState(boolean isValid) {
        submitButton.setEnabled(isValid);
        submitButton.setAlpha(isValid ? 1f : 0.5f);
    }
}