package com.seu.smarthome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.smarthome.widgt.LoadingView;

import java.util.List;

public class WifiListActivity extends AppCompatActivity {
    private List<ScanResult> list;
    private WifiManager wifiManager;
    private WifiListAdapter adapter;
    private WifiReceiver receiver;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
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
        registerReceiver(receiver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(receiver, new IntentFilter(wifiManager.WIFI_STATE_CHANGED_ACTION));

        RecyclerView wifiList = (RecyclerView)findViewById(R.id.wifi_list);
        wifiList.setLayoutManager(new LinearLayoutManager(this));
        wifiList.setHasFixedSize(true);
        adapter = new WifiListAdapter(list);
        wifiList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.home){
            finish();
        }
        return true;
    }

    class WifiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().endsWith(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                list = wifiManager.getScanResults();
                adapter.setData(list);
                progressBar.setVisibility(View.GONE);
                if(list == null)
                    Toast.makeText(context, "找不到可用wifi", Toast.LENGTH_SHORT);
            }
            else if(intent.getAction().endsWith(wifiManager.WIFI_STATE_CHANGED_ACTION)){
                if(wifiManager.isWifiEnabled()) {
                    wifiManager.startScan();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    class WifiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ScanResult> list;

        public WifiListAdapter(List<ScanResult> list){
            this.list = list;
        }

        public void setData(List<ScanResult> list){
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            RecyclerView.ViewHolder viewHolder;
            View view = LayoutInflater.from(WifiListActivity.this).inflate(R.layout.wifi_list_item, parent, false);
            viewHolder = new WifiItemViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ScanResult item = list.get(position);
            if(item != null){
                WifiItemViewHolder itemViewHolder = (WifiItemViewHolder)holder;
                itemViewHolder.wifiName.setText(item.SSID);
                switch (wifiManager.calculateSignalLevel(item.level, 5)){
                    case 0:
                        itemViewHolder.wifiLevelImage.setImageResource(R.mipmap.wifi0);
                        break;
                    case 1:
                        itemViewHolder.wifiLevelImage.setImageResource(R.mipmap.wifi1);
                        break;
                    case 2:
                        itemViewHolder.wifiLevelImage.setImageResource(R.mipmap.wifi2);
                        break;
                    case 3:
                        itemViewHolder.wifiLevelImage.setImageResource(R.mipmap.wifi3);
                        break;
                    case 4:
                        itemViewHolder.wifiLevelImage.setImageResource(R.mipmap.wifi4);
                        break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return list==null?0:list.size();
        }

        class WifiItemViewHolder extends RecyclerView.ViewHolder{
            ImageView wifiLevelImage;
            TextView wifiName;

            public WifiItemViewHolder(View view){
                super(view);
                wifiLevelImage = (ImageView) view.findViewById(R.id.wifi_level);
                wifiName = (TextView) view.findViewById(R.id.wifi_name);
            }
        }
    }
}
