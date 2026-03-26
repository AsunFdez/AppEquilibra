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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.app.Activity;
import asunfdiez.com.equilibra.R;


/**
 * Utilidades para animaciones
 * Proporciona métodos reutilizables para animar vistas
 */
public class AnimationHelper {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public AnimationHelper() {
        throw new AssertionError("No se puede instanciar, solo se puede utilizar estáticamente.");
    }

    public static void animateIcon(View icon, View background) {
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
                .setDuration(Constants.DURATION_LONG)
                .setStartDelay(Constants.DELAY_MEDIUM)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(Constants.DURATION_LONG)
                .setStartDelay(Constants.DELAY_LONG)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public static void animateSparklesIcon(View icon, View background) {
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
                .setStartDelay(Constants.DELAY_SHORT)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(800)
                .setStartDelay(Constants.DELAY_MEDIUM)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public static void animateZapIcon(View icon, View background) {
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
                .setDuration(Constants.DURATION_LONG)
                .setStartDelay(Constants.DELAY_SHORT)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(800)
                .setStartDelay(Constants.DELAY_MEDIUM)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public static void animateCheckIcon(View iconBackground) {
        if (iconBackground == null) {
            return;
        }

        iconBackground.setScaleX(0f);
        iconBackground.setScaleY(0f);

        iconBackground.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(Constants.DURATION_LONG)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public static void animateCardScale(View card) {
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

    public static void animateOptionsStagger(ViewGroup container) {
        if (container == null) {
            return;
        }

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child == null) continue;

            child.setAlpha(0f);
            child.setTranslationX(Constants.OPTION_TRANSLATION_X);

            child.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(Constants.DURATION_MEDIUM)
                    .setStartDelay((long) i * Constants.DELAY_SHORT)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    public static void animateProgressBar(final View progressBar, float progress) {
        if (progressBar == null || progressBar.getParent() == null) {
            return;
        }

        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        final int targetWidth = (int) (((View) progressBar.getParent()).getWidth() * progress);

        ValueAnimator animator = ValueAnimator.ofInt(0, targetWidth);
        animator.setDuration(Constants.DURATION_PROGRESS);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams p = progressBar.getLayoutParams();
            p.width = (int) animation.getAnimatedValue();
            progressBar.setLayoutParams(p);
        });
        animator.start();
    }

    public static void animateCardSelection(View card, final Runnable onComplete) {
        if (card == null) {
            return;
        }

        card.animate()
                .scaleX(Constants.SCALE_PRESSED)
                .scaleY(Constants.SCALE_PRESSED)
                .setDuration(Constants.DURATION_SHORT)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onComplete != null) {
                            handler.postDelayed(onComplete, Constants.DELAY_MEDIUM);
                        }
                    }
                })
                .start();
    }

    public static void animateButtonPress(View button, final Runnable onComplete) {
        if (button == null) {
            return;
        }

        button.animate()
                .scaleX(Constants.SCALE_PRESSED)
                .scaleY(Constants.SCALE_PRESSED)
                .setDuration(Constants.DURATION_SHORT)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        button.animate()
                                .scaleX(Constants.SCALE_NORMAL)
                                .scaleY(Constants.SCALE_NORMAL)
                                .setDuration(Constants.DURATION_SHORT)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (onComplete != null) {
                                            handler.postDelayed(onComplete, Constants.DELAY_SHORT);
                                        }
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    public static void cleanup() {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Aplica animación de fade in a una vista
     */
    public static void fadeIn(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Aplica animación de fade out a una vista
     */
    public static void fadeOut(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No action needed
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No action needed
            }
        });
        view.startAnimation(animation);
    }

    /**
     * Aplica animación de slide desde la derecha
     */
    public static void slideInRight(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_right);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Aplica animación de slide hacia la izquierda (salida)
     */
    public static void slideOutLeft(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_out_left);
        view.startAnimation(animation);
    }

    /**
     * Aplica animación de scale up (crecimiento)
     */
    public static void scaleUp(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_up);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Aplica animación de bounce
     */
    public static void bounce(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
        view.startAnimation(animation);
    }

    /**
     * Aplica animación de fade in con delay
     */
    public static void fadeInWithDelay(View view, long delayMillis) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        animation.setStartOffset(delayMillis);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Transición entre Activities con animación personalizada
     */
    public static void transitionSlideLeft(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Transición de fade entre Activities
     */
    public static void transitionFade(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Anima una lista de vistas con delay escalonado
     */
    public static void staggeredFadeIn(View[] views, long delayBetween) {
        for (int i = 0; i < views.length; i++) {
            fadeInWithDelay(views[i], i * delayBetween);
        }
    }

}
