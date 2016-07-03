package com.seu.smarthome.ui.device;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends BaseActivity {

    private BarChart chart;
    private final static String TAG = "HistoryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_history);
        setTitle("");

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chart = (BarChart) findViewById(R.id.history_chart);
        getHistory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    private void getHistory(){
        Intent intent = getIntent();
        int deviceID = intent.getIntExtra("deviceid", 0);

        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(deviceID));
        OkHttpUtils.post(StrUtils.GET_DEVICE_HISTORY_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                JSONArray array = j.optJSONArray("historydata");
                ArrayList<String> xVals = new ArrayList<>();
                ArrayList<BarEntry> yVals = new ArrayList<>();
                for(int i = 0; i < array.length(); ++i){
                    JSONObject object = array.optJSONObject(i);
                    float amount = (float)object.optInt("amount");
                    yVals.add(new BarEntry(amount, i));
                    xVals.add(object.optString("starttime"));
                }

                BarDataSet dataSet = new BarDataSet(yVals,"value");
                dataSet.setColor(Color.rgb(89, 199, 250));
                BarData data = new BarData(xVals,dataSet);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setSpaceBetweenLabels(1);

                YAxis yAxis = chart.getAxisLeft();
                yAxis.setAxisMinValue(0.0F);

                chart.setData(data);
                chart.setDescription("");
                chart.animateY(3000);
                chart.setScaleEnabled(false);
                chart.setTouchEnabled(false);
                chart.getAxisRight().setEnabled(false);

            }
        });
    }

    @Override
    protected String tag(){
        return TAG;
    }
}
