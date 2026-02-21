package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.OnboardingPagerAdapter;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private OnboardingPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Opcional: mostrar indicador de página
        }).attach();

        // Detectar cuando el usuario llega a la última página
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Si es la última página (índice 4), habilitar navegación a MainActivity
                if (position == adapter.getItemCount() - 1) {
                    // El botón "Empezar" redirigirá a MainActivity
                }
            }
        });
    }

    // Método para navegar a MainActivity (llamado desde los fragmentos)
    public void goToMainActivity() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        startActivity(intent);
        finish(); // Cierra OnboardingActivity
    }
}