package com.seu.smarthome.ui.main;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.seu.smarthome.R;


/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceAddActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button deviceLightButton;
    private Button deviceFeedButton;
    private Button deviceWaterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_add);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deviceLightButton=(Button)findViewById(R.id.device_light_button);
        deviceLightButton.setOnClickListener(this);
        deviceFeedButton=(Button)findViewById(R.id.device_feed_button);
        deviceFeedButton.setOnClickListener(this);
        deviceWaterButton=(Button)findViewById(R.id.device_water_button);
        deviceWaterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        intent.setClass(this,DeviceIdentifyActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.home){
            finish();
        }
        return true;
    }

}
