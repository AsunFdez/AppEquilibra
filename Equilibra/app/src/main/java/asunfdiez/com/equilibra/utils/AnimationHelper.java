package asunfdiez.com.equilibra.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AnimationHelper {
    private final Handler handler;

    public AnimationHelper() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void animateIcon(View icon, View background) {
        if (icon == null || background == null) {
            return;
        }

        icon.setScaleX(0f);
        icon.setScaleY(0f);
        background.setScaleX(0f);
        background.setScaleY(0f);

        background.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(OnboardingConstants.DURATION_LONG)
                .setStartDelay(OnboardingConstants.DELAY_MEDIUM)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(OnboardingConstants.DURATION_LONG)
                .setStartDelay(OnboardingConstants.DELAY_LONG)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void animateSparklesIcon(View icon, View background) {
        if (icon == null || background == null) {
            return;
        }

        icon.setScaleX(0f);
        icon.setScaleY(0f);
        icon.setRotation(-180f);
        background.setScaleX(0f);
        background.setScaleY(0f);

        background.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(OnboardingConstants.DELAY_SHORT)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(800)
                .setStartDelay(OnboardingConstants.DELAY_MEDIUM)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void animateZapIcon(View icon, View background) {
        if (icon == null || background == null) {
            return;
        }

        icon.setScaleX(0f);
        icon.setScaleY(0f);
        icon.setRotation(-180f);
        background.setScaleX(0f);
        background.setScaleY(0f);

        background.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(OnboardingConstants.DURATION_LONG)
                .setStartDelay(OnboardingConstants.DELAY_SHORT)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(800)
                .setStartDelay(OnboardingConstants.DELAY_MEDIUM)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void animateCheckIcon(View iconBackground) {
        if (iconBackground == null) {
            return;
        }

        iconBackground.setScaleX(0f);
        iconBackground.setScaleY(0f);

        iconBackground.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(OnboardingConstants.DURATION_LONG)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void animateCardScale(View card) {
        if (card == null) {
            return;
        }

        card.setScaleX(0.9f);
        card.setScaleY(0.9f);
        card.setAlpha(0f);

        card.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(400)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void animateOptionsStagger(ViewGroup container) {
        if (container == null) {
            return;
        }

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child == null) continue;

            child.setAlpha(0f);
            child.setTranslationX(OnboardingConstants.OPTION_TRANSLATION_X);

            child.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(OnboardingConstants.DURATION_MEDIUM)
                    .setStartDelay((long) i * OnboardingConstants.DELAY_SHORT)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    public void animateProgressBar(final View progressBar, float progress) {
        if (progressBar == null || progressBar.getParent() == null) {
            return;
        }

        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        final int targetWidth = (int) (((View) progressBar.getParent()).getWidth() * progress);

        ValueAnimator animator = ValueAnimator.ofInt(0, targetWidth);
        animator.setDuration(OnboardingConstants.DURATION_PROGRESS);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams p = progressBar.getLayoutParams();
            p.width = (int) animation.getAnimatedValue();
            progressBar.setLayoutParams(p);
        });
        animator.start();
    }

    public void animateCardSelection(View card, final Runnable onComplete) {
        if (card == null) {
            return;
        }

        card.animate()
                .scaleX(OnboardingConstants.SCALE_PRESSED)
                .scaleY(OnboardingConstants.SCALE_PRESSED)
                .setDuration(OnboardingConstants.DURATION_SHORT)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onComplete != null) {
                            handler.postDelayed(onComplete, OnboardingConstants.DELAY_MEDIUM);
                        }
                    }
                })
                .start();
    }

    public void animateButtonPress(View button, final Runnable onComplete) {
        if (button == null) {
            return;
        }

        button.animate()
                .scaleX(OnboardingConstants.SCALE_PRESSED)
                .scaleY(OnboardingConstants.SCALE_PRESSED)
                .setDuration(OnboardingConstants.DURATION_SHORT)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        button.animate()
                                .scaleX(OnboardingConstants.SCALE_NORMAL)
                                .scaleY(OnboardingConstants.SCALE_NORMAL)
                                .setDuration(OnboardingConstants.DURATION_SHORT)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (onComplete != null) {
                                            handler.postDelayed(onComplete, OnboardingConstants.DELAY_SHORT);
                                        }
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    public void cleanup() {
        handler.removeCallbacksAndMessages(null);
    }
}
