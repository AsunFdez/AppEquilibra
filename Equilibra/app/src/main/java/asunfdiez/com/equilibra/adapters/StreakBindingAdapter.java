package asunfdiez.com.equilibra.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;

import asunfdiez.com.equilibra.models.Streak;
import asunfdiez.com.equilibra.utils.AnimationHelper;

/**
 * Binding adapters para animar vistas de rachas cuando el modelo cambia
 */
public class StreakBindingAdapter {

    /**
     * Dispara animación cuando la racha se actualiza
     * Se aplica al chip que muestra los días
     */
    @BindingAdapter("streakDays")
    public static void animateStreakUpdate(View chipView, Integer consecutiveDays) {
        if (chipView == null || consecutiveDays == null || consecutiveDays == 0) {
            return;
        }

        // Reproducir pulso al actualizar
        AnimationHelper.animateStreakPulse(chipView);
    }

    /**
     * Detecta cuando se alcanza un hito y celebra
     * Se aplica al contenedor de racha completo
     */
    @BindingAdapter("streakMilestone")
    public static void celebrateStreakMilestone(ViewGroup container, Streak streak) {
        if (container == null || streak == null) {
            return;
        }

        int days = streak.getConsecutiveDays();
        
        // Milestones: 30, 60, 90, 120+ días
        if (days > 0 && (days == 30 || days == 60 || days == 90 || (days > 90 && days % 30 == 0))) {
            AnimationHelper.celebrateStreakMilestone(container);
        }
    }

    /**
     * Anima la barra de progreso cuando cambia
     * Calcula el progreso basado en días % 30 (ciclo de 30 días)
     */
    @BindingAdapter("streakProgress")
    public static void animateProgressBar(ProgressBar progressBar, Integer consecutiveDays) {
        if (progressBar == null || consecutiveDays == null) {
            return;
        }

        // Ciclo de 30 días: mostrar progreso 0-100%
        int cycleProgress = (consecutiveDays % 30) * 3;  // (0-30 días) * 3 = 0-90%
        cycleProgress = Math.min(cycleProgress, 100);

        AnimationHelper.animateProgressBarFill(progressBar, cycleProgress);
    }

    /**
     * Efecto pop-in cuando aparece un chip de racha
     */
    @BindingAdapter("chipPopIn")
    public static void popInChip(View chip, Streak streak) {
        if (chip == null || streak == null) {
            return;
        }

        // Reproducir pop-in solo si este es un cambio inicial
        if (streak.getConsecutiveDays() > 0) {
            AnimationHelper.animateChipPopIn(chip);
        }
    }
}
