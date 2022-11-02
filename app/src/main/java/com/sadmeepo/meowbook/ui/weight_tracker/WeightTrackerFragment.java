package com.sadmeepo.meowbook.ui.weight_tracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sadmeepo.meowbook.database.HealthRecord;
import com.sadmeepo.meowbook.database.HealthRecordDao;
import com.sadmeepo.meowbook.database.HealthRecordDatabase;
import com.sadmeepo.meowbook.databinding.FragmentWeightTrackerBinding;
import com.sadmeepo.meowbook.R;

import java.util.Calendar;
import java.util.List;

public class WeightTrackerFragment extends Fragment {

    private FragmentWeightTrackerBinding binding;
    DatePickerDialog datePicker;

    Button weightRecordDate;
    Switch weightUnitSwitch;
    TextView textInputWeightUnit;
    TextView editTextWeightValue;
    Button buttonAddWeight;
    TextView recordWeightMessage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)  {
        WeightTrackerViewModel weightTrackerViewModel =
                new ViewModelProvider(this).get(WeightTrackerViewModel.class);

        Context ctx = this.getContext();

        binding = FragmentWeightTrackerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set unit switch event listener.
        weightUnitSwitch = root.findViewById(R.id.weightUnitSwitch);
        textInputWeightUnit = root.findViewById(R.id.textInputWeightUnit);
        weightUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weightUnitSwitch.setText(isChecked ? "kg" : "lbs");
                textInputWeightUnit.setText(isChecked ? "kg" : "lbs");
            }
        });

        // OnClick event for setting weight record date.
        weightRecordDate = root.findViewById(R.id.weightRecordDate);
        weightRecordDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // Date picker dialog.
                datePicker = new DatePickerDialog(ctx,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                weightRecordDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        // Set today's date as the default value in weightRecordDate.
        final Calendar cldr = Calendar.getInstance();
        weightRecordDate.setText(new StringBuilder()
                .append(cldr.get(Calendar.MONTH) + 1).append("/")
                .append(cldr.get(Calendar.DAY_OF_MONTH)).append("/")
                .append(cldr.get(Calendar.YEAR)));

        // Set weight record button event listener.
        editTextWeightValue = root.findViewById(R.id.editTextWeightValue);
        buttonAddWeight = root.findViewById(R.id.buttonAddWeight);
        recordWeightMessage = root.findViewById(R.id.recordWeightMessage);
        buttonAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthRecordDao healthRecordDao =
                        HealthRecordDatabase.getInstance(ctx).healthRecordDao();

                // TODO: Validate date string.

                double weightValue = 0;
                try {
                    String weightValueString = editTextWeightValue.getText().toString();
                    weightValue = Double.parseDouble(weightValueString);
                } catch (NumberFormatException e) {
                    // TODO
                }
                if (!weightUnitSwitch.isChecked()) {
                    weightValue *= 0.453592;
                }

                HealthRecord record = new HealthRecord(
                        0,
                        "Meepo",
                        weightRecordDate.getText().toString(),
                        weightValue);

                try {
                    healthRecordDao.insert(record);
                } catch (Exception e) {
                    recordWeightMessage.setText(e.getMessage());
                }

                // FIXME: For debugging.
                try {
                    List<HealthRecord> records = healthRecordDao.getAll();
                    String summary = new String();
                    for (HealthRecord rd : records) {
                        summary += rd.toString() + "\n";
                    }
                    TextView weightHistory = root.findViewById(R.id.weightHistory);
                    weightHistory.setText(summary);
                } catch (Exception e) {
                    recordWeightMessage.setText(e.getMessage());
                }
            }
        });

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        // FIXME: For debugging.
        HealthRecordDao healthRecordDao =
                HealthRecordDatabase.getInstance(getContext()).healthRecordDao();
        healthRecordDao.nukeTable();

        super.onDestroyView();
        binding = null;
    }
}