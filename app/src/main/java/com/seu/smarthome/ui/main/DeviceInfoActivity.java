package com.seu.smarthome.ui.main;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;

import com.seu.smarthome.R;

/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceInfoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_info);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        CharSequence deviceName = intent.getCharSequenceExtra("deviceName");
        ((TextView)findViewById(R.id.device_name_text)).setText(deviceName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
