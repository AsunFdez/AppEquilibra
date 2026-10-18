package asunfdiez.com.equilibra.utils;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Clase especializada en la validación en tiempo real de los objetivos de ahorro.
 * 
 * <p>Víncula escuchadores de texto (TextWatchers) a los campos de entrada de metas 
 * para habilitar o deshabilitar dinámicamente el botón de envío, asegurando que 
 * el usuario ingrese datos válidos antes de continuar.</p>
 */
public class SavingGoalValidator {
    // Campos de entrada para el nombre y el importe de la meta
    private final TextInputEditText goalInput;
    private final TextInputEditText amountInput;
    
    // Botón de confirmación que se habilita según la validez del input
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