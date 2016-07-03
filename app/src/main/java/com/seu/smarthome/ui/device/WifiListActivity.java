package com.seu.smarthome.ui.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.seu.smarthome.R;
import com.seu.smarthome.adapter.WifiListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiListActivity extends AppCompatActivity {
    private List<ScanResult> list;
    private WifiManager wifiManager;
    private WifiListAdapter adapter;
    private WifiReceiver receiver;
    private ProgressBar progressBar;

    private final static String SSID = "seu-wlan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_wifi_list);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar)findViewById(R.id.loading);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        else{
            wifiManager.startScan();
            progressBar.setVisibility(View.VISIBLE);
        }

        receiver = new WifiReceiver();
        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(receiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        RecyclerView wifiList = (RecyclerView)findViewById(R.id.wifi_list);
        wifiList.setLayoutManager(new LinearLayoutManager(this));
        wifiList.setHasFixedSize(true);
        adapter = new WifiListAdapter(this, list);
        wifiList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    private int getNetworkID(String ssid){
        int id = -1;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if(list != null){
            for(int i = 0; i < list.size(); ++i){
                if(list.get(i).SSID.equals("\"" + ssid + "\"")){
                    id = list.get(i).networkId;
                    break;
                }
            }
        }
        return id;
    }

    class WifiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().endsWith(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult lhs, ScanResult rhs) {
                        return rhs.level - lhs.level;
                    }
                };
                List<ScanResult> wifiList = wifiManager.getScanResults();
                if(wifiList == null)
                    return;
                Collections.sort(wifiList, comparator);
                list = new ArrayList<>();
                boolean added;
                for(int i = 0; i < wifiList.size(); ++i){
                    added = false;
                    for(int j = 0; j < i; ++j){
                        if(wifiList.get(i).SSID.equals(wifiList.get(j).SSID)){
                            added = true;
                        }
                    }
                    if(!added)
                        list.add(wifiList.get(i));
                }
                adapter.setData(list);
                progressBar.setVisibility(View.GONE);
                if(list == null)
                    Toast.makeText(context, "找不到可用wifi", Toast.LENGTH_SHORT).show();

                int id = getNetworkID(SSID);
                if(id != -1)
                    wifiManager.enableNetwork(id, true);
            }
            else if(intent.getAction().endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                if(wifiManager.isWifiEnabled()) {
                    wifiManager.startScan();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
