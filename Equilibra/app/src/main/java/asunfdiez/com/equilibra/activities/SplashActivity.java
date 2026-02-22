package asunfdiez.com.equilibra.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import asunfdiez.com.equilibra.R;

/*
* SplashActivity con implamentación nativa de animaciones
* */

public class SplashActivity extends AppCompatActivity {

    private View logoContainer;
    private View piggyBankIcon;
    private View coinEmoji;
    private View sparkleLeft;
    private View sparkleRight;
    private View appTitle;
    private View appSubtitle;



    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
        decorView.setAlpha(0f); //invisible
        decorView.animate()
                .alpha(1f)
                .setDuration(200)
                .start();

        //Contenedor Logo: animación inicial con escalado y rotación
        logoContainer.setScaleX(0f);
        logoContainer.setScaleY(0f);
        logoContainer.setRotation(-180f);
        logoContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(600)
                .setStartDelay(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Piggy bank bounce loop
        animatePiggyBankBounce(piggyBankIcon);

        // Coin sparkle animation
        animateCoinSparkle(coinEmoji);

        // Sparkle animations
        animateSparkle(sparkleLeft, -20f, -80f, -15f, -30f, 2500, 500);
        animateSparkle(sparkleRight, 20f, 80f, 15f, 30f, 2500, 1500);

        // Title animations
        appTitle.setTranslationY(20f);
        appTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(500)
                .start();

        appSubtitle.setTranslationY(20f);
        appSubtitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(700)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Navigate to OnboardingActivity after 2 seconds
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }, 2000);
                    }
                })
                .start();
    }


    private void animatePiggyBankBounce(final View view) {
        ObjectAnimator bounceY = ObjectAnimator.ofFloat(view, "translationY", 0f, -12f, 0f);
        bounceY.setDuration(3000);
        bounceY.setRepeatCount(ValueAnimator.INFINITE);
        bounceY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, -5f, 0f, 5f, 0f);
        rotation.setDuration(3000);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());

        ValueAnimator scale = ValueAnimator.ofFloat(1f, 1.05f, 1f);
        scale.setDuration(3000);
        scale.setRepeatCount(ValueAnimator.INFINITE);
        scale.setInterpolator(new AccelerateDecelerateInterpolator());
        scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scaleValue = (float) animation.getAnimatedValue();
                view.setScaleX(scaleValue);
                view.setScaleY(scaleValue);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(bounceY, rotation, scale);
        animatorSet.setStartDelay(800);
        animatorSet.start();
    }

    private void animateCoinSparkle(final View view) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f, 1f, 0f);
                alpha.setDuration(2500);

                ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", -20f, 10f, 25f, 45f);
                translationY.setDuration(2500);

                ValueAnimator scale = ValueAnimator.ofFloat(0f, 1.3f, 1f, 0.7f);
                scale.setDuration(2500);
                scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scaleValue = (float) animation.getAnimatedValue();
                        view.setScaleX(scaleValue);
                        view.setScaleY(scaleValue);
                    }
                });

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(view, "rotation", 0f, 180f, 360f, 540f);
                rotationAnim.setDuration(2500);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(alpha, translationY, scale, rotationAnim);
                animatorSet.start();
            }
        }, 800);
    }

    private void animateSparkle(final View view, final float startX, final float endX,
                                final float startY, final float endY,
                                long duration, long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f, 0f);
                alpha.setDuration(duration);

                ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0f, startY, endY);
                translationY.setDuration(duration);

                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", startX / 2, startX, endX);
                translationX.setDuration(duration);

                ValueAnimator scale = ValueAnimator.ofFloat(0f, 1.2f, 0f);
                scale.setDuration(duration);
                scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scaleValue = (float) animation.getAnimatedValue();
                        view.setScaleX(scaleValue);
                        view.setScaleY(scaleValue);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(alpha, translationY, translationX, scale);
                animatorSet.start();
            }
        }, delay);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar handlers para evitar memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}