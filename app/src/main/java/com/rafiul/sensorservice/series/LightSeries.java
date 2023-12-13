package com.rafiul.sensorservice.series;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.rafiul.sensorservice.R;
import com.rafiul.sensorservice.database.SensorData;
import com.rafiul.sensorservice.database.SensorDatabase;
import com.rafiul.sensorservice.databinding.ActivityLightSeriesBinding;

import java.util.ArrayList;
import java.util.List;

public class LightSeries extends AppCompatActivity {

    private ActivityLightSeriesBinding binding;
    private SensorDatabase sensorDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLightSeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorDatabase = SensorDatabase.getSensorDataBase(getApplicationContext());

        setLineChartData();
    }

    private void setLineChartData() {

        List<SensorData> proximityList = sensorDatabase.sensorDAO().getAllSensorData();

        ArrayList<Entry> entryArrayList = new ArrayList<>();


        for (SensorData sensorData : proximityList) {
            long timeStamp = sensorData.getTimeStamp();
            float proximityValue = sensorData.getLight();
            Entry chartEntry = new Entry(timeStamp, proximityValue);
            entryArrayList.add(chartEntry);
        }

        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "Lebel");

        lineDataSet.setColor(R.color.purple);
        lineDataSet.setCircleRadius(10f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setValueTextSize(20f);
        lineDataSet.setFillColor(R.color.green);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(lineDataSet);
        binding.getTheGraph.setData(data);
        binding.getTheGraph.setBackgroundColor(getResources().getColor(R.color.white));
        binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInCubic);
    }
}