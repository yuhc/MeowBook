package com.sadmeepo.meowbook.ui.weight_tracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.sadmeepo.meowbook.database.HealthRecord;
import com.sadmeepo.meowbook.database.HealthRecordDao;
import com.sadmeepo.meowbook.database.HealthRecordDatabase;
import com.sadmeepo.meowbook.databinding.FragmentWeightTrackerBinding;
import com.sadmeepo.meowbook.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightTrackerFragment extends Fragment {

    private FragmentWeightTrackerBinding binding;
    DatePickerDialog datePicker;
    HealthRecordDao healthRecordDao;

    Button weightRecordDate;
    Switch weightUnitSwitch;
    TextView textInputWeightUnit;
    TextView editTextWeightValue;
    Button buttonAddWeight;
    TextView recordWeightMessage;

    LineChart weightHistoryLineChart;

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

        recordWeightMessage = root.findViewById(R.id.recordWeightMessage);
        healthRecordDao =
                HealthRecordDatabase.getInstance(ctx).healthRecordDao();

        // Set today's date as the default value in weightRecordDate.
        final Calendar cldr = Calendar.getInstance();
        weightRecordDate.setText(new StringBuilder()
                .append(cldr.get(Calendar.MONTH) + 1).append("/")
                .append(cldr.get(Calendar.DAY_OF_MONTH)).append("/")
                .append(cldr.get(Calendar.YEAR)));

        // Set up line chart for weight history.
        weightHistoryLineChart = root.findViewById(R.id.weightHistoryLineChart);
        configureWeightHistoryLineChart();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadWeightHistoryInLineChart(root);
            }
        });

        // Set weight record button event listener.
        editTextWeightValue = root.findViewById(R.id.editTextWeightValue);
        buttonAddWeight = root.findViewById(R.id.buttonAddWeight);
        buttonAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Validate date string.

                double weightValue = 0;
                try {
                    String weightValueString = editTextWeightValue.getText().toString();
                    weightValue = Double.parseDouble(weightValueString);
                } catch (NumberFormatException e) {
                    recordWeightMessage.setText("Please enter a number");
                    return;
                }
                if (!weightUnitSwitch.isChecked()) {
                    // Convert to kg.
                    weightValue *= 0.453592;
                }

                // Validate weight value.
                if (weightValue < 0.1) {
                    recordWeightMessage.setText("Meepo is too slim to be true. Newborn average weight is 150-250 grams");
                    return;
                } else if (weightValue > 12) {
                    recordWeightMessage.setText("Meepo is too chonky to be true");
                    return;
                }

                HealthRecord record = new HealthRecord(
                        "Meepo",
                        weightRecordDate.getText().toString(),
                        weightValue);

                try {
                    healthRecordDao.insert(record);
                }
                catch (SQLiteConstraintException e) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    healthRecordDao.update(record);
                                    // TODO: Error handling.
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("Meepo's weight on " + weightRecordDate.getText().toString() + " has been recorded. Overwrite?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }

                // TODO: Do not reload all data points.
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadWeightHistoryInLineChart(root);
                    }
                });
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

    private void configureWeightHistoryLineChart() {
        Description desc = new Description();
        desc.setText("Weight History");
        desc.setTextSize(24);
        weightHistoryLineChart.setDescription(desc);

        XAxis xAxis = weightHistoryLineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });
    }

    private void loadWeightHistoryInLineChart(View root) {
        // Load existing weight data.
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        try {
            List<HealthRecord> records = healthRecordDao.getAll();
            for (HealthRecord rd : records) {
                Date date = formatter.parse(rd.date);
                entries.add(new Entry((float)(date.getTime() / 1000.0), ((float) rd.weightInKg)));
            }
        } catch (Exception e) {
            recordWeightMessage.setText(e.getClass().toString());
        }
        setWeightHistoryLineChartData(entries);
    }

    private void setWeightHistoryLineChartData(ArrayList<Entry> weightInKg) {
        Collections.sort(weightInKg, new EntryXComparator());
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        // TODO: Check unit.
        LineDataSet weightDataSet = new LineDataSet(weightInKg, "Weight (kg)");
        weightDataSet.setDrawCircles(true);
        weightDataSet.setCircleRadius(4);
        weightDataSet.setDrawValues(false);
        weightDataSet.setLineWidth(3);
        weightDataSet.setColor(Color.GREEN);
        weightDataSet.setCircleColor(Color.GREEN);
        dataSets.add(weightDataSet);

        LineData lineData = new LineData(dataSets);
        weightHistoryLineChart.setData(lineData);
        weightHistoryLineChart.invalidate();
    }
}