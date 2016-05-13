package com.seu.smarthome.ui.main;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.seu.smarthome.R;

import java.util.ArrayList;
import java.util.Random;

public class HistoryActivity extends AppCompatActivity {

    private LineChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_history);

        chart = (LineChart) findViewById(R.id.history_chart);
        setData();
    }

    void setData() {

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 12; i++){
            float profix = random.nextFloat() * 10;
            yVals.add(new Entry(profix,i));
            xVals.add((i+1)+"月");
        }
        LineDataSet dataSet = new LineDataSet(yVals,"value");
        dataSet.setColor(Color.rgb(89, 199, 250));
        dataSet.setLineWidth(1.75f);
        dataSet.setCircleColor(Color.rgb(89, 199, 250));
        LineData data = new LineData(xVals,dataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart.setData(data);
        chart.setDescription("");
        chart.animateY(3000);

    }
}
