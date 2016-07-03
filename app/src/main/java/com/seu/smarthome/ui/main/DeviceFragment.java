package com.seu.smarthome.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seu.smarthome.R;
import com.seu.smarthome.adapter.DeviceListAdapter;
import com.seu.smarthome.model.Device;
import com.seu.smarthome.ui.base.BaseFragment;
import com.seu.smarthome.ui.device.DeviceControlActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-21.
 */
public class DeviceFragment extends BaseFragment {
    private final static String TAG = "DeviceFragment";

    private SwipeRefreshLayout swipeRefreshLayout;
    private DeviceListAdapter adapter;
    private List<Device> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fgt_device,null);

        RecyclerView deviceList = (RecyclerView)view.findViewById(R.id.fgt_device_list);
        deviceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceList.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.fgt_device_swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        list = new ArrayList<>();
        adapter = new DeviceListAdapter(getActivity(), list);
        adapter.setListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int i = (int)v.getTag();
                final Device item = list.get(i);

                Intent intent = new Intent();
                intent.putExtra("device", item);
                intent.setClass(getActivity(), DeviceControlActivity.class);
                startActivity(intent);
            }
        });

        deviceList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateData();
    }

    private void updateData(){
        Map<String, String> map = new HashMap<>();
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
    protected String tag() {
        return TAG;
    }

}
