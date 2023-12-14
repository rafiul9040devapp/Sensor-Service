package com.rafiul.sensorservice.series;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.rafiul.sensorservice.R;
import com.rafiul.sensorservice.database.SensorData;
import com.rafiul.sensorservice.database.SensorDatabase;
import com.rafiul.sensorservice.databinding.ActivityProximitySeriesBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class LinearChart extends AppCompatActivity {
    private ActivityProximitySeriesBinding seriesBinding;
    private SensorDatabase sensorDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seriesBinding = ActivityProximitySeriesBinding.inflate(getLayoutInflater());
        setContentView(seriesBinding.getRoot());

        sensorDatabase = SensorDatabase.getSensorDataBase(getApplicationContext());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String sensorName = "";
        if (bundle != null) {
            sensorName = (String) bundle.get("Sensor");
        }
        setLineChartData(sensorName);
    }

    private void setLineChartData(String sensorName) {

        List<SensorData> proximityList = sensorDatabase.sensorDAO().getAllSensorData();

        ArrayList<Entry> entryArrayList = new ArrayList<>();


        for (SensorData sensorData : proximityList) {
//            long timeStampInMillis = sensorData.getTimeStamp();
//            long timeStampInMinutes = timeStampInMillis / (60 * 1000);

            long timeStampInMillis = sensorData.getTimeStamp();
// Convert milliseconds to a Date object
            Date date = new Date(timeStampInMillis);
// Format the Date object to a string representation with only hour and minute in 24-hour format
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(date);
            // Convert the formatted time to minutes
            long timeInMinutes = getTimeInMinutes(formattedTime);

            float sensorValue = switch (sensorName) {
                case "Proximity" -> sensorData.getProximity();
                case "Light" -> sensorData.getLight();
                case "Accelerometer" -> sensorData.getAccelerometerY();
                case "Gyroscope" -> sensorData.getGyroscopeY();
                default -> 0f;
            };
            Entry chartEntry = new Entry(timeInMinutes, sensorValue);
            entryArrayList.add(chartEntry);
        }

        //UI part of the code

        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "Lebel");
        lineDataSet.setColor(R.color.purple);
        lineDataSet.setCircleRadius(10f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setValueTextSize(20f);
        lineDataSet.setFillColor(R.color.green);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        XAxis xAxis = seriesBinding.getTheGraph.getXAxis();
        // Set a custom label count and granularity
        xAxis.setLabelCount(10, true); // Set the number of labels you want to display
        xAxis.setGranularity(1f);

        LineData data = new LineData(lineDataSet);
        seriesBinding.getTheGraph.setData(data);
        seriesBinding.getTheGraph.setBackgroundColor(getResources().getColor(R.color.white));
        seriesBinding.getTheGraph.animateXY(1000, 1000, Easing.EaseInCubic);
    }

    private long getTimeInMinutes(String formattedTime) {
        String[] timeComponents = formattedTime.split(":");
        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);
        return hours * 60L + minutes;
    }

}