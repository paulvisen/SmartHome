package com.seu.smarthome.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;

import java.nio.BufferUnderflowException;

/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceControlActivity extends AppCompatActivity implements Button.OnClickListener{

    private Button historyButton;
    private Button timeTaskButton;
    private Button infoButton;
    private Button deleteButton;

    private CharSequence deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_control);
        setTitle("");
        Toolbar toolbar=(Toolbar)findViewById(R.id.aty_device_control_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(R.id.aty_device_control_title);
        Intent intent=getIntent();
        deviceName=intent.getCharSequenceExtra("deviceName");
        title.setText(deviceName);

        historyButton=(Button) findViewById(R.id.aty_device_history_button);
        historyButton.setOnClickListener(this);
        timeTaskButton=(Button)findViewById(R.id.aty_device_time_task_button);
        timeTaskButton.setOnClickListener(this);
        infoButton=(Button)findViewById(R.id.aty_device_info_button);
        infoButton.setOnClickListener(this);
        deleteButton=(Button)findViewById(R.id.aty_device_delete_button);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View v){
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.aty_device_time_task_button:
                intent.setClass(this,TimeTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.aty_device_info_button:
                intent.putExtra("deviceName",deviceName);
                intent.setClass(this,DeviceInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.aty_device_history_button:
                intent.setClass(this,HistoryActivity.class);
                startActivity(intent);
                break;
        }
    }


}
