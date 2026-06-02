package asunfdiez.com.equilibra.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import asunfdiez.com.equilibra.R;
import asunfdiez.com.equilibra.adapters.ComparativeAnalysisAdapter;
import asunfdiez.com.equilibra.databinding.ActivityComparativeAnalysisBinding;
import asunfdiez.com.equilibra.viewmodels.ComparativeAnalysisViewModel;

public class ComparativeAnalysisActivity extends AppCompatActivity {

    private ActivityComparativeAnalysisBinding binding;
    private ComparativeAnalysisViewModel viewModel;
    private ComparativeAnalysisAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComparativeAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ComparativeAnalysisViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupUI();
        setObservers();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new ComparativeAnalysisAdapter();
        binding.rvComparative.setAdapter(adapter);

        binding.togglePeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                viewModel.setPeriodType(checkedId == R.id.btnWeekly);
            }
        });

        viewModel.getPeriodOptions().observe(this, options -> {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, options);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerPeriod.setAdapter(spinnerAdapter);
        });

        viewModel.getSelectedPeriodIndex().observe(this, index -> {
            if (binding.spinnerPeriod.getSelectedItemPosition() != index) {
                binding.spinnerPeriod.setSelection(index);
            }
        });

        binding.spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectPeriod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setObservers() {
        viewModel.getComparativeStates().observe(this, states -> {
            if (states != null) {
                adapter.setItems(states);
            }
        });
    }
}