package com.rafiul.sensorservice.series;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.rafiul.sensorservice.R;
import com.rafiul.sensorservice.database.SensorData;
import com.rafiul.sensorservice.database.SensorDatabase;
import com.rafiul.sensorservice.databinding.ActivityLightSeriesBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BarChart extends AppCompatActivity {

    private ActivityLightSeriesBinding binding;
    private SensorDatabase sensorDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLightSeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorDatabase = SensorDatabase.getSensorDataBase(getApplicationContext());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String sensorName = "";

        if (bundle != null) {
            sensorName = (String) bundle.get("Sensor");
        }
        setupActionBar(sensorName);
        setBarChartData(sensorName);
    }

    private void setBarChartData(String sensorName) {
        List<SensorData> allSensorData = sensorDatabase.sensorDAO().getAllSensorData();

        Map<String, ArrayList<BarEntry>> entryArrayListMap = new HashMap<>();

        for (SensorData sensorData : allSensorData) {
            long timeStampInMillis = sensorData.getTimeStamp();
            Date date = new Date(timeStampInMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(date);
            long timeInMinutes = getTimeInMinutes(formattedTime);

            for (String axis : new String[]{"X", "Y", "Z"}) {
                float sensorValue = getSensorValue(sensorName, sensorData, axis);
                BarEntry chartEntry = new BarEntry(timeInMinutes, sensorValue);
                entryArrayListMap.computeIfAbsent(axis, k -> new ArrayList<>()).add(chartEntry);
            }
        }

        for (Map.Entry<String, ArrayList<BarEntry>> entry : entryArrayListMap.entrySet()) {
            BarDataSet dataSet = new BarDataSet(entry.getValue(), entry.getKey() + "-Axis");
            dataSet.setColor(R.color.purple);
            dataSet.setValueTextSize(20f);
            initBarChart(getChartForAxis(entry.getKey()), dataSet);
        }
    }

    private float getSensorValue(String sensorName, SensorData sensorData, String axis) {
        return switch (sensorName) {
            case "Accelerometer" -> switch (axis) {
                case "X" -> sensorData.getAccelerometerX();
                case "Y" -> sensorData.getAccelerometerY();
                case "Z" -> sensorData.getAccelerometerZ();
                default -> 0f;
            };
            case "Gyroscope" -> switch (axis) {
                case "X" -> sensorData.getGyroscopeX();
                case "Y" -> sensorData.getGyroscopeY();
                case "Z" -> sensorData.getGyroscopeZ();
                default -> 0f;
            };
            default -> 0f;
        };
    }

    private View getChartForAxis(String axis) {
        return switch (axis) {
            case "X" -> binding.getTheGraphX;
            case "Y" -> binding.getTheGraphY;
            case "Z" -> binding.getTheGraphZ;
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
        };
    }

    private void initBarChart(View barChartView, BarDataSet barDataSet) {
        if (barChartView instanceof com.github.mikephil.charting.charts.BarChart barChart) {
            BarData data = new BarData(barDataSet);
            barChart.setData(data);
            barChart.setBackgroundColor(getResources().getColor(R.color.white));

            barChart.setDrawGridBackground(false);
            barChart.setDrawBarShadow(false);
            barChart.setDrawBorders(false);

            Description description = new Description();
            description.setEnabled(false);
            barChart.setDescription(description);

            barChart.animateY(1000);
            barChart.animateX(1000);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(10);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setDrawAxisLine(true);
            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setDrawAxisLine(true);

            Legend legend = barChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextSize(11f);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(true);
        } else {
            throw new IllegalArgumentException("Invalid view type for BarChart initialization");
        }
    }

    private void setupActionBar(String sensorName) {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(sensorName);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private long getTimeInMinutes(String formattedTime) {
        String[] timeComponents = formattedTime.split(":");
        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);
        return hours * 60L + minutes;
    }
}