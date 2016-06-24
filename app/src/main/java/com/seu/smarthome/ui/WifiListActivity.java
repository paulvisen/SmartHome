package com.seu.smarthome.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.util.DimensionUtils;

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
                if(list.get(i).SSID.equals("\"" + SSID + "\"")){
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
            if(intent.getAction().endsWith(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
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
            final ScanResult item = list.get(position);
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

                if(item.capabilities.contains("WPA")){
                    holder.itemView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            final EditText editText = new EditText(WifiListActivity.this);
                            editText.setHint("密码");
                            TextView title = new TextView(WifiListActivity.this);
                            title.setText(item.SSID);
                            title.setGravity(Gravity.CENTER);
                            title.setPadding(0, DimensionUtils.dp2px(16), 0, DimensionUtils.dp2px(16));
                            AlertDialog.Builder dialog=new AlertDialog.Builder(WifiListActivity.this);
                            dialog.setCustomTitle(title);
                            dialog.setView(editText);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){
                                    if(!editText.getText().toString().isEmpty()){
                                    }
                                    else{
                                        Toast.makeText(APP.context(), "请输入密码", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            dialog.setNegativeButton("取消", null);
                            dialog.show();
                        }
                    });
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
