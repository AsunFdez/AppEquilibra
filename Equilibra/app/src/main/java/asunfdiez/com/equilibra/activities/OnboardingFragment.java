package asunfdiez.com.equilibra.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import asunfdiez.com.equilibra.R;

public class OnboardingFragment extends Fragment {
    private static final String ARG_SCREEN_TYPE = "screen_type";
    private String screenType;

    public static OnboardingFragment newInstance(String screenType) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SCREEN_TYPE, screenType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenType = getArguments().getString(ARG_SCREEN_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);

        ImageView iconView = root.findViewById(R.id.onboarding_icon);
        TextView titleView = root.findViewById(R.id.onboarding_title);
        TextView descriptionView = root.findViewById(R.id.onboarding_description);
        Button continueBtn = root.findViewById(R.id.btn_continue);
        LinearLayout emotionalContainer = root.findViewById(R.id.emotional_options);

        // Aplicar animaciones
        iconView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_in));
        titleView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        descriptionView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

        // Configurar según tipo de pantalla
        switch (screenType) {
            case "emotional":
                iconView.setImageResource(R.drawable.ic_nav_home);
                titleView.setText("¿Cómo te sientes cuando\npienasas en tu dinero?");
                descriptionView.setText("Tu bienestar emocional es importante para nosotros");
                emotionalContainer.setVisibility(View.VISIBLE);
                setupEmotionalOptions(root);
                continueBtn.setText("Siguiente");
                break;
            case "calm":
                iconView.setImageResource(R.drawable.ic_emotion_calm);
                titleView.setText("¡Genial! Tu tranquilidad es una fortaleza");
                descriptionView.setText("¡Genial! Tu tranquilidad es una fortaleza. Equilibra te ayudará a mantener ese control y seguir creciendo.");
                continueBtn.setText("Siguiente");
                break;
            case "worried":
                iconView.setImageResource(R.drawable.ic_emotion_anxious);
                titleView.setText("Entendemos tu preocupación");
                descriptionView.setText("Entendemos tu preocupación. Dar este primer paso es valiente. Juntos trabajaremos para transformar ese estrés en confianza.");
                continueBtn.setText("Siguiente");
                break;
            case "confused":
                iconView.setImageResource(R.drawable.ic_emotion_confident);
                titleView.setText("La confusión es el inicio del aprendizaje");
                descriptionView.setText("La confusión es el inicio del aprendizaje. Equilibra te guiará con claridad para que tomes control de tus finanzas.");
                continueBtn.setText("Siguiente");
                break;
            case "final":
                iconView.setImageResource(R.drawable.ic_ach_star);
                titleView.setText("¡Estás listo para comenzar!");
                descriptionView.setText("Hemos personalizado Equilibra para ti. Vamos a ayudarte a alcanzar tus metas financieras.");
                continueBtn.setText("Empezar");
                break;
        }

        // Botón Continuar/Siguiente/Empezar
        continueBtn.setOnClickListener(v -> {
            if (screenType.equals("final")) {
                // Última página: ir a MainActivity
                OnboardingActivity activity = (OnboardingActivity) getActivity();
                if (activity != null) {
                    activity.goToMainActivity();
                }
            } else {
                // Siguiente página
                ViewPager2 viewPager = ((OnboardingActivity) getActivity()).findViewById(R.id.viewPager);
                int nextPage = viewPager.getCurrentItem() + 1;
                viewPager.setCurrentItem(nextPage, true);
            }
        });

        return root;
    }

    private void setupEmotionalOptions(View root) {
        LinearLayout container = root.findViewById(R.id.emotion_buttons_container);

        String[] emotions = {"Tranquilo/a", "Un poco ansioso/a", "Muy estresado/a", "Confiado/a"};
        int[] drawables = {
                R.drawable.ic_emotion_calm,
                R.drawable.ic_emotion_anxious,
                R.drawable.ic_emotion_stressed,
                R.drawable.ic_emotion_confident
        };

        for (int i = 0; i < emotions.length; i++) {
            View optionView = LayoutInflater.from(getContext())
                    .inflate(R.layout.emotion_option_item, container, false);

            ImageView emotionIcon = optionView.findViewById(R.id.emotion_icon);
            TextView emotionLabel = optionView.findViewById(R.id.emotion_label);

            emotionIcon.setImageResource(drawables[i]);
            emotionLabel.setText(emotions[i]);

            int finalI = i;
            optionView.setOnClickListener(v -> {
                // TODO: Guardar la selección emocional del usuario
                // Por ahora, avanza a la siguiente página
            });

            optionView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
            container.addView(optionView);
        }
    }
}