package com.seu.smarthome.ui.scene;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.adapter.DeviceListAdapter;
import com.seu.smarthome.model.Device;
import com.seu.smarthome.model.ManualTask;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceListActivity extends BaseActivity {

    private ManualTask selectedTask;
    private DeviceListAdapter adapter;
    private List<Device> list;

    private final static String TAG = "DeviceListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_list);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();
        RecyclerView deviceList = (RecyclerView)findViewById(R.id.device_list);
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.setHasFixedSize(true);
        adapter = new DeviceListAdapter(this, list);
        DeviceItemClickListener listener = new DeviceItemClickListener(this);
        adapter.setListener(listener);
        deviceList.setAdapter(adapter);

        selectedTask = new ManualTask();
        updateData();
    }

    private void updateData(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_DEVICE_LIST_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null) {
                    return;
                }
                JSONArray array = j.optJSONArray("devicelist");
                if (array == null)
                    return;
                list.clear();
                for (int i = 0; i < array.length(); ++i) {
                    Device device = Device.fromJSON(array.optJSONObject(i));
                    list.add(device);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }

    class DeviceItemClickListener implements View.OnClickListener{
        private Context context;
        DeviceItemClickListener(Context context){
            this.context = context;
        }

        @Override
        public void onClick(View v){
            int i = (int)v.getTag();
            final Device item = list.get(i);
            AlertDialog.Builder dialog=new AlertDialog.Builder(context);
            switch (item.deviceType) {
                case Device.DEVICE_TYPE_LIGHT:
                    selectedTask.amount = 1;
                    dialog.setSingleChoiceItems(new String[]{"开启", "关闭"}, 0, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            switch(which){
                                case 0:
                                    selectedTask.amount = 1;
                                    break;
                                case 1:
                                    selectedTask.amount = 0;
                                    break;
                            }
                        }
                    });
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            Intent intent = new Intent();
                            selectedTask.deviceID = item.id;
                            selectedTask.taskType = item.deviceType;
                            selectedTask.deviceName = item.deviceName;

                            intent.putExtra("task", selectedTask);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                    break;
                case Device.DEVICE_TYPE_WATER:
                    final EditText feedEditText = new EditText(context);
                    feedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dialog.setTitle("浇水量");
                    dialog.setView(feedEditText);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            if(!feedEditText.getText().toString().isEmpty()){
                                Intent intent = new Intent();
                                selectedTask.deviceID = item.id;
                                selectedTask.taskType = item.deviceType;
                                selectedTask.deviceName = item.deviceName;
                                selectedTask.amount = Integer.parseInt(feedEditText.getText().toString());
                                intent.putExtra("task", selectedTask);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else{
                                Toast.makeText(APP.context(), "请输入浇水量", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                    break;
                case Device.DEVICE_TYPE_FEED:
                    final EditText waterEditText = new EditText(context);
                    waterEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dialog.setTitle("喂食量");
                    dialog.setView(waterEditText);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            if(!waterEditText.getText().toString().isEmpty()){
                                Intent intent = new Intent();
                                selectedTask.deviceID = item.id;
                                selectedTask.taskType = item.deviceType;
                                selectedTask.deviceName = item.deviceName;
                                selectedTask.amount = Integer.parseInt(waterEditText.getText().toString());
                                intent.putExtra("task", selectedTask);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else{
                                Toast.makeText(APP.context(), "请输入喂食量", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                    break;
            }
        }
    }

    @Override
    protected String tag(){
        return TAG;
    }
}
