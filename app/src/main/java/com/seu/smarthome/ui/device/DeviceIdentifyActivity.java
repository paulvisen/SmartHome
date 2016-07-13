package com.seu.smarthome.ui.device;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.ui.main.ActivityMain;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceIdentifyActivity extends BaseActivity implements  Button.OnClickListener{
    private static final String TAG = "DeviceIdentifyActivity";

    private EditText etDeviceID;
    private EditText etDeviceCode;

    //private int deviceType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_identify);
        setTitle("");

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Intent intent = getIntent();
        //deviceType = intent.getIntExtra("devicetype", 0);

        findViewById(R.id.submit_button).setOnClickListener(this);

        etDeviceID = (EditText) findViewById(R.id.aty_device_identify_device_id);
        etDeviceCode = (EditText) findViewById(R.id.aty_device_identify_device_code);

    }

    @Override
    public void onClick(View view){
        if(etDeviceID.getText().toString().equals("") || etDeviceCode.getText().toString().equals("")){
            Toast.makeText(APP.context(), "请输入设备码和验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("deviceid", etDeviceID.getText().toString());
        map.put("passwd", etDeviceCode.getText().toString());
        OkHttpUtils.post(StrUtils.ADD_DEVICE_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(DeviceIdentifyActivity.this, ActivityMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        /*final EditText editText = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("设备更新成功，请输入设备名");
        dialog.setView(editText);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(APP.context(), "请输入设备名", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> map = new HashMap<>();
                map.put("token", StrUtils.token());
                map.put("deviceid", etDeviceID.getText().toString());
                map.put("passwd", etDeviceCode.getText().toString());
                map.put("devicename", editText.getText().toString());
                map.put("devicetype", Integer.toString(deviceType));
                OkHttpUtils.post(StrUtils.ADD_DEVICE_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
                    @Override
                    public void onResponse(String s) {
                        JSONObject j = OkHttpUtils.parseJSON(s);
                        if (j == null) {
                            return;
                        }
                    }
                });

                Intent intent = new Intent();
                intent.setClass(DeviceIdentifyActivity.this, ActivityMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    protected String tag(){
        return TAG;
    }

}
