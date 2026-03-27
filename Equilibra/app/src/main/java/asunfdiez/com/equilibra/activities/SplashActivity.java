package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/*
* SplashActivity con implementación nativa de animaciones
* utilizando la clase específica para AnimationHelper
* */

public class SplashActivity extends AppCompatActivity {

    private View logoContainer;
    private View piggyBankIcon;
    private View coinEmoji;
    private View sparkleLeft;
    private View sparkleRight;
    private View appTitle;
    private View appSubtitle;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefsManager = SharedPrefsManager.getInstance(this);

        // Hide system UI for full screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        initViews();
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        piggyBankIcon = findViewById(R.id.piggyBankIcon);
        coinEmoji = findViewById(R.id.coinEmoji);
        sparkleLeft = findViewById(R.id.sparkleLeft);
        sparkleRight = findViewById(R.id.sparkleRight);
        appTitle = findViewById(R.id.appTitle);
        appSubtitle = findViewById(R.id.appSubtitle);
    }

    private void startAnimations() {
        // Background fade in
        View decorView = getWindow().getDecorView();
        AnimationHelper.backgroundFadeIn(decorView, 100);

        // Contenedor Logo: animación inicial con escalado y rotación
        AnimationHelper.logoRotation(logoContainer, 600, 100);

        // Piggy bank bounce loop
        AnimationHelper.animatePiggyBankBounce(piggyBankIcon, 3000, 700);

        // Coin sparkle animation
        AnimationHelper.animateCoinSparkle(coinEmoji, 2500, 700);

        // Sparkle animations
        AnimationHelper.animateSparkle(sparkleLeft, -20f, -80f, -15f, -30f, 2500, 800);
        AnimationHelper.animateSparkle(sparkleRight, 20f, 80f, 15f, 30f, 2500, 1500);

        // Title and subtitle animations
        AnimationHelper.animateTitle(appTitle, 500, 800);
        AnimationHelper.animateTitle(appSubtitle, 500, 1200);

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
