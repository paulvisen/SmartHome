package com.seu.smarthome.ui.device;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceInfoActivity extends BaseActivity{
    private final static String TAG = "DeviceInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_info);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    private void getData(){
        Intent intent = getIntent();
        int deviceID = intent.getIntExtra("deviceid", 0);

        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(deviceID));
        OkHttpUtils.post(StrUtils.GET_DEVICE_DATAILS_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if(j == null)
                    return;
                ((TextView)findViewById(R.id.aty_device_info_name)).setText(j.optString("devicename"));
                ((TextView)findViewById(R.id.aty_device_info_state)).setText(j.optString("devicestate"));
                ((TextView)findViewById(R.id.aty_device_info_code)).setText(j.optString("devicecode"));
            }
        });

    }

    @Override
    protected String tag() {
        return TAG;
    }
}
