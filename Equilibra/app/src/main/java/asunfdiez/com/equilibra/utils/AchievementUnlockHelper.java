package asunfdiez.com.equilibra.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.models.Achievement;

/**
 * AchievementUnlockHelper - Helper para mostrar desbloqueos de logros
 *
 * Responsabilidad:
 * - Mostrar diálogo de desbloqueo de logro
 * - Aplicar animaciones de celebración
 * - Reproducir sonido de logro (opcional)
 * - Agradecer y motivar al usuario
 *
 * Patrón:
 * - Utility class con métodos estáticos
 */
public class AchievementUnlockHelper {

    private static final String TAG = "AchievementUnlock";

    /**
     * Mostrar diálogo de desbloqueo con animación
     *
     * @param activity Activity actual
     * @param achievement Logro desbloqueado
     * @param onDismiss Callback al cerrar (opcional)
     */
    public static void showUnlockDialog(
            @NonNull Activity activity,
            @NonNull Achievement achievement,
            @Nullable Runnable onDismiss) {

        try {
            // Crear vista personalizada
            LayoutInflater inflater = LayoutInflater.from(activity);
            View dialogView = inflater.inflate(R.layout.dialog_achievement_unlock, null);

            // Referencias a vistas
            ImageView iconView = dialogView.findViewById(R.id.achievement_icon);
            TextView titleView = dialogView.findViewById(R.id.achievement_title);
            TextView descriptionView = dialogView.findViewById(R.id.achievement_description);
            TextView pointsView = dialogView.findViewById(R.id.achievement_points);
            View sparklesView = dialogView.findViewById(R.id.sparkles);

            // Llenar datos
            iconView.setImageResource(getAchievementIcon(achievement));
            titleView.setText("¡" + achievement.getTitle() + "!");
            descriptionView.setText(achievement.getDescription());
            pointsView.setText(String.format("+%d pts", achievement.getPoints()));

            // Crear diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AchievementDialogStyle)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setPositiveButton("¡Genial!", (dialog, which) -> {
                        dialog.dismiss();
                        if (onDismiss != null) {
                            onDismiss.run();
                        }
                    });

            AlertDialog dialog = builder.create();

            // Mostrar y animar
            dialog.show();

            // Aplicar animaciones
            animateUnlock(activity, dialogView, sparklesView);

            // Reproducir sonido (si existe)
            playUnlockSound(activity);

        } catch (Exception e) {
            android.util.Log.e(TAG, "Error showing unlock dialog", e);
        }
    }

    /**
     * Animar el desbloqueo del logro
     *
     * @param context Contexto
     * @param dialogView Vista del diálogo
     * @param sparklesView Vista de efectos
     */
    private static void animateUnlock(
            @NonNull Context context,
            @NonNull View dialogView,
            @NonNull View sparklesView) {

        try {
            // Animación principal: scale up + bounce
            Animation scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.achievement_unlock_scale);
            dialogView.startAnimation(scaleAnimation);

            // Animación de chispas después de 300ms
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Iniciar animación de chispas
                    Animation sparkleAnimation = AnimationUtils.loadAnimation(context, R.anim.achievement_sparkle);
                    sparklesView.startAnimation(sparkleAnimation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        } catch (Exception e) {
            android.util.Log.e(TAG, "Error animating unlock", e);
        }
    }

    /**
     * Reproducir sonido de desbloqueo (si está disponible)
     *
     * @param context Contexto
     */
    private static void playUnlockSound(@NonNull Context context) {
        try {
            // Cargar y reproducir sonido (opcional)
            // Para Android, se puede usar MediaPlayer
            // Por ahora, solo usar vibración si está disponible

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.os.VibrationEffect effect = android.os.VibrationEffect.createPredefined(
                        android.os.VibrationEffect.DEFAULT_AMPLITUDE
                );
                android.os.Vibrator vibrator = (android.os.Vibrator)
                        context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(effect);
                }
            }

        } catch (Exception e) {
            android.util.Log.e(TAG, "Error playing unlock sound", e);
        }
    }

    /**
     * Obtener el icono del logro basado en su key
     *
     * @param achievement Logro
     * @return ID del drawable
     */
    private static int getAchievementIcon(@NonNull Achievement achievement) {
        String key = achievement.getKey();

        // Mapear keys a iconos (estos deben existir en drawable/)
        switch (key) {
            case "first_step":
            case Constants.ACHIEVEMENT_FIRST_EXPENSE:
                return R.drawable.ic_achievement_first_step;
            case "week_strong":
            case Constants.ACHIEVEMENT_WEEK_STREAK:
                return R.drawable.ic_achievement_week;
            case "active_tracking":
            case Constants.ACHIEVEMENT_10_EXPENSES:
                return R.drawable.ic_achievement_explorer;
            case "goal_set":
                return R.drawable.ic_ach_target;
            case "insight_seeker":
                return R.drawable.ic_fea_lightbulb;
            case "consistent_saver":
            case Constants.ACHIEVEMENT_SAVER_50:
                return R.drawable.ic_achievement_saver_beginner;
            case "budget_master":
            case Constants.ACHIEVEMENT_UNDER_BUDGET_WEEK:
                return R.drawable.ic_achievement_budget_week;
            case "category_expert":
            case Constants.ACHIEVEMENT_CATEGORY_EXPLORER:
                return R.drawable.ic_achievement_category_master;
            case "streak_champion":
            case Constants.ACHIEVEMENT_MONTH_STREAK:
                return R.drawable.ic_achievement_consistent;
            case "budget_guru":
            case Constants.ACHIEVEMENT_UNDER_BUDGET_MONTH:
                return R.drawable.ic_achievement_legend;
            default:
                return R.drawable.ic_fea_lightbulb;
        }
    }

    /**
     * Mostrar notificación tipo "toast" para desbloqueo rápido
     *
     * @param context Contexto
     * @param achievement Logro
     */
    public static void showUnlockToast(@NonNull Context context, @NonNull Achievement achievement) {
        String message = String.format("🏆 %s +%d pts", achievement.getTitle(), achievement.getPoints());
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
