package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.activities.OnboardingActivity;
import android.widget.ImageView;
import android.widget.TextView;



public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ocultar la barra de arriba para que sea pantalla completa
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Referencias a los elementos a animar que queremos animar
        TextView imgBag = findViewById(R.id.img_bag);
        TextView imgStars = findViewById(R.id.img_stars);
        LinearLayout logoContainer = findViewById(R.id.logo_container);

        // 1. Animación del logo (la que ya teníamos)
        Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        logoContainer.startAnimation(animLogo);

        // 2. Animación del saquito (caída con rebote)
        Animation animBag = AnimationUtils.loadAnimation(this, R.anim.bag_enter);
        imgBag.startAnimation(animBag);

        // 3. Animación de las estrellitas (subida suave)
        Animation animStars = AnimationUtils.loadAnimation(this, R.anim.stars_enter);
        imgStars.startAnimation(animStars);

        // 4. que parpadeen
        Animation animStarsBlink = AnimationUtils.loadAnimation(this, R.anim.stars_blink);
        imgStars.startAnimation(animStarsBlink);


        // Tiempo de espera antes de cambiar de pantalla (3000ms = 3 segundos)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, OnboardingActivity.class);
                startActivity(intent);
                finish(); // Cerramos la Splash para que no puedan volver atrás
            }
        }, 3000);
    }
}