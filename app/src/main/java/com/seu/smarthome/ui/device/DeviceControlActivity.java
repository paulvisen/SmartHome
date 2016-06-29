package com.seu.smarthome.ui.device;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.Time;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.model.Device;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.ui.common.TimeTaskActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceControlActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "DeviceControlActivity";

    private TextView tvAmount;
    private TextView tvAmountType;
    private TextView tvAutoState;
    private ToggleButton btnControl;

    private Device device;

    private int days;
    private int startTime;
    private boolean auto;
    private boolean state;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getDeviceState();
            if(!state) {
                btnControl.setChecked(false);
                handler.removeCallbacks(runnable);
            }
            else
                handler.postDelayed(runnable, 2000);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_control);
        setTitle("");

        Toolbar toolbar = (Toolbar)findViewById(R.id.aty_device_control_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView)findViewById(R.id.aty_device_control_title);
        Intent intent = getIntent();
        device = intent.getParcelableExtra("device");
        title.setText(device.deviceName);

        findViewById(R.id.aty_device_history_button).setOnClickListener(this);
        findViewById(R.id.aty_device_time_task_button).setOnClickListener(this);
        findViewById(R.id.aty_device_info_button).setOnClickListener(this);
        //findViewById(R.id.aty_device_delete_button).setOnClickListener(this);

        btnControl = (ToggleButton)findViewById(R.id.aty_device_control_button);
        btnControl.setOnClickListener(this);

        tvAmount = (TextView)findViewById(R.id.aty_device_amount);
        tvAmount.setOnClickListener(this);
        tvAmountType = (TextView)findViewById(R.id.aty_device_amount_type);
        tvAutoState = (TextView)findViewById(R.id.aty_device_control_auto_state);

        if(device.deviceType == Device.DEVICE_TYPE_LIGHT){
            findViewById(R.id.aty_device_amount_grid).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.aty_device_amount_grid).setVisibility(View.VISIBLE);
            if(device.deviceType == Device.DEVICE_TYPE_WATER){
                tvAmountType.setText(R.string.water_amount);
            }
            else{
                tvAmountType.setText(R.string.feed_amount);
            }
        }

        getDeviceState();
    }

    @Override
    protected void onDestroy(){
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent();
        intent.putExtra("deviceid", device.id);
        switch (v.getId()){
            case R.id.aty_device_time_task_button:
                intent.setClass(DeviceControlActivity.this, TimeTaskActivity.class);
                intent.putExtra("auto", auto);
                intent.putExtra("start_time", startTime);
                intent.putExtra("days", days);
                startActivityForResult(intent, 1);
                break;
            case R.id.aty_device_info_button:
                intent.setClass(DeviceControlActivity.this, DeviceInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.aty_device_history_button:
                intent.setClass(DeviceControlActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;
            /*case R.id.aty_device_delete_button:
                deleteDevice();
                finish();
                break;*/
            case R.id.aty_device_control_button:
                if(btnControl.isChecked())
                    setDeviceOn();
                else
                    setDeviceOff();
                break;
            case R.id.aty_device_amount:
                final EditText editText = new EditText(this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                dialog.setTitle(tvAmountType.getText());
                dialog.setView(editText);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().isEmpty()) {
                            tvAmount.setText(editText.getText());
                        } else {
                            Toast.makeText(APP.context(), "请输入" + tvAmountType.getText(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        startTime = data.getIntExtra("start_time", 0);
        days = data.getIntExtra("days", 0);

        auto = data.getBooleanExtra("auto", false);
        if(auto){
            setDeviceTimingOn();
        }
        else{
            setDeviceTimingOff();
        }
    }

    private void getDeviceState(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(device.id));
        OkHttpUtils.post(StrUtils.GET_DEVICE_STATE_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                if(j.optString("devicestate").equals("on")){
                    btnControl.setChecked(true);
                    state = true;
                }
                else{
                    btnControl.setChecked(false);
                    state = false;
                }

                tvAmount.setText(j.optString("amount"));

                if(j.optInt("tasktype") == 1){
                    auto = true;
                    tvAutoState.setText("定时：开");
                    startTime = StrUtils.timeStr2Int(j.optString("starttime"));
                    days = StrUtils.daysStr2Int(j.optString("days"));
                }
                else{
                    auto = false;
                    tvAutoState.setText("定时：关");
                }


            }
        });
    }

    private void setDeviceOn() {
        Map<String, String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(device.id));
        map.put("devicetype", Integer.toString(device.deviceType));
        map.put("amount", tvAmount.getText().toString());
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        map.put("starttime", hour + ":" + minute);
        OkHttpUtils.post(StrUtils.SET_DEVICE_REALTIME_TASK_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if(j == null){
                    state = false;
                    btnControl.setChecked(false);
                    return;
                }
                state = true;
                Toast.makeText(APP.context(), "设备已开启", Toast.LENGTH_SHORT).show();
                if(device.deviceType == Device.DEVICE_TYPE_WATER || device.deviceType == Device.DEVICE_TYPE_FEED)
                    handler.postDelayed(runnable, 2000);
            }
        });
    }

    private void setDeviceOff(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(device.id));
        OkHttpUtils.post(StrUtils.CANCEL_DEVICE_REALTIME_TASK_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if(j == null){
                    state = true;
                    btnControl.setChecked(true);
                    return;
                }
                Toast.makeText(APP.context(), "设备已关闭", Toast.LENGTH_SHORT).show();
                state = false;
            }
        });
    }

    private void setDeviceTimingOn(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(device.id));
        map.put("amount", tvAmount.getText().toString());
        map.put("devicetype", Integer.toString(device.deviceType));
        map.put("starttime", StrUtils.timeInt2Str(startTime));
        map.put("days", StrUtils.daysInt2Str(days));
        OkHttpUtils.post(StrUtils.SET_DEVICE_TIMING_TASK_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if(j == null) {
                    auto = false;
                    tvAutoState.setText("定时：关");
                    return;
                }
                tvAutoState.setText("定时：开");
            }
        });
    }

    private void setDeviceTimingOff(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", Integer.toString(device.id));
        OkHttpUtils.post(StrUtils.CANCEL_DEVICE_TIMING_TASK_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if(j == null) {
                    auto = true;
                    tvAutoState.setText("定时：开");
                    return;
                }
                tvAutoState.setText("定时：关");
            }
        });
    }

    private void deleteDevice(){

    }

    @Override
    protected String tag() {
        return TAG;
    }
}
