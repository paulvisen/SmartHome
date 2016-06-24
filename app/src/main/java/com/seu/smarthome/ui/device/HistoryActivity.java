package com.seu.smarthome.ui.device;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.seu.smarthome.R;

import java.util.ArrayList;
import java.util.Random;

public class HistoryActivity extends AppCompatActivity {

    private BarChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_history);

        chart = (BarChart) findViewById(R.id.history_chart);
        setData();
    }

    void setData() {

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 12; i++){
            float profix = random.nextFloat() * 10;
            yVals.add(new BarEntry(profix,i));
            xVals.add((i+1)+":00");
        }
        BarDataSet dataSet = new BarDataSet(yVals,"value");
        dataSet.setColor(Color.rgb(89, 199, 250));
        BarData data = new BarData(xVals,dataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinValue(0.0F);

        chart.setData(data);
        chart.setDescription("");
        chart.animateY(3000);
        chart.setScaleEnabled(false);
        chart.setTouchEnabled(false);
        chart.getAxisRight().setEnabled(false);
    }
}
