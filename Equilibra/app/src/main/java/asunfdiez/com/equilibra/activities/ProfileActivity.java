package asunfdiez.com.equilibra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.databinding.ActivityProfileBinding;
import asunfdiez.com.equilibra.databinding.ViewSelectorPopupBinding;
import asunfdiez.com.equilibra.utils.AnimationHelper;
import asunfdiez.com.equilibra.utils.Constants;
import asunfdiez.com.equilibra.viewmodels.ProfileViewModel;

/**
 * Activity de perfil de usuario
 * Permite visualizar y editar información del perfil personal
 */
public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;
    private ActivityProfileBinding binding;
    private PopupWindow viewSelectorPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Vincular el ciclo de vida al binding (Importante para LiveData)
        binding.setLifecycleOwner(this);

        binding.setViewModel(viewModel);

        // Observar el usuario y pasarlo al binding
        viewModel.getUserProfile().observe(this, user -> {
            binding.setUser(user);
        });

        setupToolbar();
        setupObservers();
        setupListeners();
        setupBackNavigation();
        AnimationHelper.fadeIn(binding.layoutProfile);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }
        binding.toolbarProfile.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

    }

    private void setupObservers() {
        viewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                binding.setUser(user);
            }
        });

        viewModel.getEditMode().observe(this, enabled -> {
            if (enabled) AnimationHelper.shakeAnimation(binding.etName);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                showMessage(message);
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                showMessage(message);
            }
        });

        viewModel.isAccountDeleted().observe(this, deleted -> {
            if (deleted) {
                navigateToOnboarding();
            }
        });
    }

    private void setupListeners() {
        binding.viewSelectorButton.setOnClickListener(v -> showViewSelectorPopup());

        binding.ivProfileImage.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            changeProfileImage();
        });

        binding.btnEditProfile.setOnClickListener(v -> viewModel.setEditMode(true));

        binding.btnSaveProfile.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            viewModel.saveProfileChanges();
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            showChangePasswordDialog();
        });

        binding.btnDeleteAccount.setOnClickListener(v -> {
            AnimationHelper.scaleUp(v);
            showDeleteAccountDialog();
        });
    }

    private void showViewSelectorPopup() {

        ViewSelectorPopupBinding selectorBinding = ViewSelectorPopupBinding.inflate(getLayoutInflater());
        selectorBinding.setUser(viewModel.getUserProfile().getValue());

        View popupView = selectorBinding.getRoot();

        viewSelectorPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        selectorBinding.viewOption1.setOnClickListener(v -> selectViewMode(Constants.USER_LEVEL_BEGINNER));
        selectorBinding.viewOption2.setOnClickListener(v -> selectViewMode(Constants.USER_LEVEL_INTERMEDIATE));
        selectorBinding.viewOption3.setOnClickListener(v -> selectViewMode(Constants.USER_LEVEL_ADVANCED));

        binding.viewSelectorArrow.animate().rotation(180f).setDuration(200).start();
        viewSelectorPopup.setOnDismissListener(() ->
                binding.viewSelectorArrow.animate().rotation(0f).setDuration(200).start()
        );

        viewSelectorPopup.setOnDismissListener(() ->
                binding.viewSelectorArrow.animate().rotation(0f).setDuration(200).start()
        );

        viewSelectorPopup.showAsDropDown(binding.viewSelectorButton, 0, 10);

    }

    private void selectViewMode(String mode) {
        viewModel.changeView(mode);
        viewSelectorPopup.dismiss();
    }

    private void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeProfileImage() {
        showMessage(getString(R.string.feature_coming_soon));
    }

    private void showChangePasswordDialog() {
        showMessage(getString(R.string.feature_coming_soon));
    }

    private void showDeleteAccountDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_account_title));
        builder.setMessage(getString(R.string.delete_account_message));
        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> viewModel.deleteAccount());
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private void navigateToOnboarding() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.handleBackPress(() -> {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            }
        });
    }
}
