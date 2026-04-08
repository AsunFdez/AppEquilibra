package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import asunfdiez.com.equilibra.databinding.ActivitySplashBinding;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/*
* SplashActivity con implementación nativa de animaciones
* utilizando la clase específica para AnimationHelper
* */

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPrefsManager prefsManager;
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = SharedPrefsManager.getInstance(this);

        // Hide system UI for full screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        startAnimations();
    }


    private void startAnimations() {
        // Background fade in
        View decorView = getWindow().getDecorView();
        AnimationHelper.backgroundFadeIn(decorView, 100);

        // Contenedor Logo: animación inicial con escalado y rotación
        AnimationHelper.logoRotation(binding.logoContainer, 600, 100);

        // Piggy bank bounce loop
        AnimationHelper.animatePiggyBankBounce(binding.piggyBankIcon, 3000, 700);

        // Coin sparkle animation
        AnimationHelper.animateCoinSparkle(binding.coinEmoji, 2500, 700);

        // Sparkle animations
        AnimationHelper.animateSparkle(binding.sparkleLeft, -20f, -80f, -15f, -30f, 2500, 800);
        AnimationHelper.animateSparkle(binding.sparkleRight, 20f, 80f, 15f, 30f, 2500, 1500);

        // Title and subtitle animations
        AnimationHelper.animateTitle(binding.appTitle, 500, 800);
        AnimationHelper.animateTitle(binding.appSubtitle, 500, 1200);

        navigateAfterDelay(2500);
    }

    private void navigateAfterDelay(int delay) {
        handler.postDelayed(() -> {
            Intent intent = determineNextScreen();
            startActivity(intent);
            AnimationHelper.transitionFade(this);
            finish();
        }, delay);
    }

    /**
     * Determina la siguiente pantalla según el estado del onboarding
     */
    private Intent determineNextScreen() {
        if (prefsManager.isOnboardingComplete()) {
            return new Intent(this, MainActivity.class);
        } else {
            return new Intent(this, OnboardingActivity.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar handlers para evitar memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}
