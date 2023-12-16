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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
        switch (Objects.requireNonNull(sensorName)) {
            case "Accelerometer" -> binding.toolbar.setTitle("Accelerometer");
            case "Gyroscope" -> binding.toolbar.setTitle("Gyroscope");
            default -> binding.toolbar.setTitle("N/A");
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setLineChartData(sensorName);
    }

    private void setLineChartData(String sensorName) {

        List<SensorData> allSensorData = sensorDatabase.sensorDAO().getAllSensorData();

        ArrayList<BarEntry> entryArrayListAxisX = new ArrayList<>();
        ArrayList<BarEntry> entryArrayListAxisY = new ArrayList<>();
        ArrayList<BarEntry> entryArrayListAxisZ = new ArrayList<>();


        for (SensorData sensorData : allSensorData) {
            long timeStampInMillis = sensorData.getTimeStamp();
            Date date = new Date(timeStampInMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(date);
            long timeInMinutes = getTimeInMinutes(formattedTime);

            float sensorValueX = switch (sensorName) {
                case "Accelerometer" -> sensorData.getAccelerometerX();
                case "Gyroscope" -> sensorData.getGyroscopeX();
                default -> 0f;
            };
            BarEntry chartEntryX = new BarEntry(timeInMinutes, sensorValueX);
            entryArrayListAxisX.add(chartEntryX);


            float sensorValueY = switch (sensorName) {
                case "Accelerometer" -> sensorData.getAccelerometerY();
                case "Gyroscope" -> sensorData.getGyroscopeY();
                default -> 0f;
            };
            BarEntry chartEntryY = new BarEntry(timeInMinutes, sensorValueY);
            entryArrayListAxisY.add(chartEntryY);

            float sensorValueZ = switch (sensorName) {
                case "Accelerometer" -> sensorData.getAccelerometerZ();
                case "Gyroscope" -> sensorData.getGyroscopeZ();
                default -> 0f;
            };
            BarEntry chartEntryZ = new BarEntry(timeInMinutes, sensorValueZ);
            entryArrayListAxisZ.add(chartEntryZ);

        }
        BarDataSet barDataSetX = new BarDataSet(entryArrayListAxisX, "X-Axis");
        barDataSetX.setColor(R.color.purple);
        barDataSetX.setValueTextSize(20f);
        initBarChartX(barDataSetX);

        BarDataSet barDataSetY = new BarDataSet(entryArrayListAxisY, "Y-Axis");
        barDataSetY.setColor(R.color.purple);
        barDataSetY.setValueTextSize(20f);
        initBarChartY(barDataSetY);

        BarDataSet barDataSetZ = new BarDataSet(entryArrayListAxisZ, "Z-Axis");
        barDataSetZ.setColor(R.color.purple);
        barDataSetZ.setValueTextSize(20f);
        initBarChartZ(barDataSetZ);
    }

    private void initBarChartX(BarDataSet barDataSet) {

        BarData data = new BarData(barDataSet);
        binding.getTheGraphX.setData(data);
        binding.getTheGraphX.setBackgroundColor(getResources().getColor(R.color.white));
        // binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInCubic);

        //hiding the grey background of the chart, default false if not set
        binding.getTheGraphX.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        binding.getTheGraphX.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        binding.getTheGraphX.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        binding.getTheGraphX.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        binding.getTheGraphX.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        binding.getTheGraphX.animateX(1000);

        XAxis xAxis = binding.getTheGraphX.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = binding.getTheGraphX.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(true);

        YAxis rightAxis = binding.getTheGraphX.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(true);

        Legend legend = binding.getTheGraphX.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(true);

    }

    private void initBarChartY(BarDataSet barDataSet) {

        BarData data = new BarData(barDataSet);
        binding.getTheGraphY.setData(data);
        binding.getTheGraphY.setBackgroundColor(getResources().getColor(R.color.white));
        // binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInCubic);

        //hiding the grey background of the chart, default false if not set
        binding.getTheGraphY.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        binding.getTheGraphY.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        binding.getTheGraphY.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        binding.getTheGraphY.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        binding.getTheGraphY.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        binding.getTheGraphY.animateX(1000);

        XAxis xAxis = binding.getTheGraphY.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = binding.getTheGraphY.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(true);

        YAxis rightAxis = binding.getTheGraphY.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(true);

        Legend legend = binding.getTheGraphY.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(true);

    }

    private void initBarChartZ(BarDataSet barDataSet) {

        BarData data = new BarData(barDataSet);
        binding.getTheGraphZ.setData(data);
        binding.getTheGraphZ.setBackgroundColor(getResources().getColor(R.color.white));
        // binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInCubic);

        //hiding the grey background of the chart, default false if not set
        binding.getTheGraphZ.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        binding.getTheGraphZ.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        binding.getTheGraphZ.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        binding.getTheGraphZ.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        binding.getTheGraphZ.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        binding.getTheGraphZ.animateX(1000);

        XAxis xAxis = binding.getTheGraphZ.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = binding.getTheGraphZ.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(true);

        YAxis rightAxis = binding.getTheGraphZ.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(true);

        Legend legend = binding.getTheGraphZ.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(true);

    }

    private long getTimeInMinutes(String formattedTime) {
        String[] timeComponents = formattedTime.split(":");
        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);
        return hours * 60L + minutes;
    }
}