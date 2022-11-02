package com.sadmeepo.meowbook.ui.gallery;

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

import com.sadmeepo.meowbook.DBHandler;
import com.sadmeepo.meowbook.databinding.FragmentWeightTrackerBinding;
import com.sadmeepo.meowbook.R;

import java.util.Calendar;

public class WeightTrackerFragment extends Fragment {

    private FragmentWeightTrackerBinding binding;
    private DBHandler dbHandler;
    DatePickerDialog datePicker;

    Button weightRecordDate;
    Switch weightUnitSwitch;
    TextView textInputWeightUnit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)  {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        Context ctx = this.getContext();
        dbHandler = new DBHandler(ctx);

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

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}