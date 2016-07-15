package com.seu.smarthome.ui.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.seu.smarthome.R;
import com.seu.smarthome.adapter.DeviceListAdapter;
import com.seu.smarthome.model.Device;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtyMyDevice extends BaseActivity {

    private DeviceListAdapter adapter;
    private List<Device> list;

    private final static String TAG = "AtyMyDevice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_my_device);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();
        RecyclerView deviceList = (RecyclerView)findViewById(R.id.device_list);
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.setHasFixedSize(true);
        adapter = new DeviceListAdapter(this, list);
        adapter.setClickable(false);
        deviceList.setAdapter(adapter);

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

    @Override
    protected String tag(){
        return TAG;
    }
}
