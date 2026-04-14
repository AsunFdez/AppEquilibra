package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.BuildConfig;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityAboutBinding;

/**
 * Activity "Acerca de" la app
 * Muestra información de la app, versión, créditos, etc.
 *
 */
public class AboutActivity extends AppCompatActivity {

    // URLs
    private static final String GITHUB_URL = "https://github.com/AsunFdez";
    private static final String SUPPORT_EMAIL = "support@equilibra.app";

    // Data Binding
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        binding.setLifecycleOwner(this);

        // Configurar UI
        setupToolbar();
        setupVersionInfo();
        setupListeners();

        // Animación de entrada
        binding.getRoot().startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    /**
     * Configura el Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Configura la información de versión
     */
    private void setupVersionInfo() {
        try {
            String versionName = BuildConfig.VERSION_NAME;
            int versionCode = BuildConfig.VERSION_CODE;
            binding.tvVersion.setText(
                    getString(R.string.version_format, versionName, versionCode));
        } catch (Exception e) {
            binding.tvVersion.setText(R.string.version_unknown);
        }
    }

    /**
     * Configura los listeners
     */
    private void setupListeners() {

        // GitHub
        binding.cardGithub.setOnClickListener(v ->
                openUrl(GITHUB_URL));

        // Contacto/Soporte
        binding.cardSupport.setOnClickListener(v ->
                sendEmail());

        // Licencias open source
        binding.cardLicenses.setOnClickListener(v ->
                showLicenses());

        // Compartir app
        binding.btnShareApp.setOnClickListener(v ->
                shareApp());

        // Valorar app
        binding.btnRateApp.setOnClickListener(v ->
                rateApp());
    }

    /**
     * Abre una URL en el navegador
     */
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre el cliente de email para enviar mensaje de soporte
     */
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{SUPPORT_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.support_email_subject));

        try {
            startActivity(Intent.createChooser(intent,
                    getString(R.string.send_email)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra las licencias de librerías de terceros
     */
    private void showLicenses() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(R.string.open_source_licenses)
                .setMessage(R.string.licenses_content)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Comparte la app
     */
    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_app_message));

        startActivity(Intent.createChooser(shareIntent,
                getString(R.string.share_app)));
    }

    /**
     * Abre la Play Store para valorar la app
     */
    private void rateApp() {
        try {
            String packageName = getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName));
            startActivity(intent);
        } catch (Exception e) {
            // Si no tiene Play Store, abrir en navegador
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" +
                            getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}