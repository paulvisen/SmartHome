package com.seu.smarthome.ui.main;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seu.smarthome.R;


/**
 * Created by Administrator on 2016-04-20.
 */
public class TimeTaskActivity extends  AppCompatActivity implements TextView.OnClickListener{

    private Switch timeTaskSwitch;
    private TextView startTimeLabel;
    private TextView endTimeLabel;

    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_time_task);
        setTitle("");

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timeTaskSwitch=(Switch)findViewById(R.id.enable_time_task_switch);
        timeTaskSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
             public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                LinearLayout timetaskSet=(LinearLayout)findViewById(R.id.time_task_set);
                setSubControlsEnabled(timetaskSet,isChecked);
            }});
        Intent intent = getIntent();
        timeTaskSwitch.setChecked(intent.getBooleanExtra("auto",false));
        startTimeLabel=(TextView)findViewById(R.id.start_time_set);
        startTimeLabel.setOnClickListener(this);
        endTimeLabel=(TextView)findViewById(R.id.end_time_set);
        endTimeLabel.setOnClickListener(this);
        }



    private void setSubControlsEnabled(ViewGroup viewGroup,boolean enabled){
        for(int i=0;i<viewGroup.getChildCount();i++) {
            View v=viewGroup.getChildAt(i);
            if(v instanceof ViewGroup)
            {
                setSubControlsEnabled((ViewGroup)v,enabled);
            }else if(v instanceof  TextView)
            {
                ((TextView)v).setEnabled(enabled);
                ((TextView)v).setEnabled(enabled);
            }

        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.start_time_set) {
            new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startTimeLabel.setText(hourOfDay + ":" + minute);
                }
            }, start_hour, start_minute, true).show();

        }
        else if(v.getId() == R.id.end_time_set){
            new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endTimeLabel.setText(hourOfDay + ":" + minute);
                }
            }, end_hour, end_minute, true).show();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home)
        {
            Intent intent = new Intent();
            intent.putExtra("data_return", timeTaskSwitch.isChecked());
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent();
            intent.putExtra("data_return", timeTaskSwitch.isChecked());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}