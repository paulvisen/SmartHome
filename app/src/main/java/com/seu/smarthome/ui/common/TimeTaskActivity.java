package com.seu.smarthome.ui.common;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.util.StrUtils;


/**
 * Created by Administrator on 2016-04-20.
 */
public class TimeTaskActivity extends  AppCompatActivity implements TextView.OnClickListener{

    private Switch timeTaskSwitch;
    private TextView startTimeLabel;
    private ViewStub cover;

    private int startTime;
    private int days;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_time_task);
        setTitle("");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cover = (ViewStub)findViewById(R.id.aty_time_task_cover);
        cover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //阻止下层视图的点击
                return true;
            }
        });

        timeTaskSwitch = (Switch)findViewById(R.id.enable_time_task_switch);
        final LinearLayout timetaskSet = (LinearLayout) findViewById(R.id.time_task_set);
        timeTaskSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSubControlsEnabled(timetaskSet, isChecked);
            }
        });

        Intent intent = getIntent();
        boolean auto = intent.getBooleanExtra("auto", false);
        timeTaskSwitch.setChecked(auto);
        setSubControlsEnabled(timetaskSet, auto);

        boolean editable = intent.getBooleanExtra("editable", true);
        setClickable(editable);

        startTimeLabel = (TextView)findViewById(R.id.start_time_set);
        startTimeLabel.setOnClickListener(this);
        startTime = intent.getIntExtra("start_time", 0);
        startTimeLabel.setText(StrUtils.timeInt2Str(startTime));

        days = intent.getIntExtra("days", 0);
        getDay();

        findViewById(R.id.toggle_button_monday).setOnClickListener(this);
        findViewById(R.id.toggle_button_tuesday).setOnClickListener(this);
        findViewById(R.id.toggle_button_wednesday).setOnClickListener(this);
        findViewById(R.id.toggle_button_thursday).setOnClickListener(this);
        findViewById(R.id.toggle_button_friday).setOnClickListener(this);
        findViewById(R.id.toggle_button_saturday).setOnClickListener(this);
        findViewById(R.id.toggle_button_sunday).setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.start_time_set:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime = hourOfDay * 60 + minute;
                        startTimeLabel.setText(StrUtils.timeInt2Str(startTime));
                    }
                }, startTime / 60, startTime % 60, true).show();
                break;
            case R.id.toggle_button_monday:
            case R.id.toggle_button_tuesday:
            case R.id.toggle_button_wednesday:
            case R.id.toggle_button_thursday:
            case R.id.toggle_button_friday:
            case R.id.toggle_button_saturday:
            case R.id.toggle_button_sunday:
                setDay((ToggleButton)v);
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home)
        {
            back();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setSubControlsEnabled(ViewGroup viewGroup, boolean enabled){
        for(int i = 0; i < viewGroup.getChildCount(); ++i) {
            View v = viewGroup.getChildAt(i);
            if(v instanceof ViewGroup)
            {
                setSubControlsEnabled((ViewGroup) v, enabled);
            }else
            {
                v.setEnabled(enabled);
            }

        }
    }

    private void setClickable(boolean clickable){
        if(!clickable) {
            cover.setVisibility(View.VISIBLE);
            Toast.makeText(APP.context(), "打开编辑状态可进行修改", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDay(ToggleButton button){
        int i = Integer.parseInt(button.getTag().toString());
        if(button.isChecked()){
            days = days | (0x80 >> i);
        }
        else{
            days = days & ~(0x80 >> i);
        }
    }

    private void getDay(){
        ((ToggleButton)findViewById(R.id.toggle_button_monday)).setChecked((days & 0x40) != 0);
        ((ToggleButton)findViewById(R.id.toggle_button_tuesday)).setChecked((days & 0x20) != 0);
        ((ToggleButton)findViewById(R.id.toggle_button_wednesday)).setChecked((days & 0x10) != 0);
        ((ToggleButton)findViewById(R.id.toggle_button_thursday)).setChecked((days & 0x08) != 0);
        ((ToggleButton)findViewById(R.id.toggle_button_friday)).setChecked((days & 0x04) != 0);
        ((ToggleButton)findViewById(R.id.toggle_button_saturday)).setChecked((days & 0x02) != 0);
        ((ToggleButton)findViewById(R.id.toggle_button_sunday)).setChecked((days & 0x01) != 0);
    }

    private void back(){
        Intent intent = new Intent();
        intent.putExtra("auto", timeTaskSwitch.isChecked());
        intent.putExtra("start_time", startTime);
        intent.putExtra("days", days);
        setResult(RESULT_OK, intent);
        finish();
    }
}