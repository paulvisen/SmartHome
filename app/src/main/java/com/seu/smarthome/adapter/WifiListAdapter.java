package com.seu.smarthome.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.util.DimensionUtils;

import java.util.List;

/**
 * Created by jwcui on 2016/6/27.
 */
public class WifiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScanResult> list;
    private Context context;

    public WifiListAdapter(Context context, List<ScanResult> list){
        this.list = list;
        this.context = context;
    }

    public void setData(List<ScanResult> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_list_item, parent, false);
        viewHolder = new WifiItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ScanResult item = list.get(position);
        if(item != null){
            WifiItemViewHolder itemViewHolder = (WifiItemViewHolder)holder;
            itemViewHolder.wifiName.setText(item.SSID);

            int image[] = {R.mipmap.wifi0, R.mipmap.wifi1, R.mipmap.wifi2, R.mipmap.wifi3, R.mipmap.wifi4};
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int level = wifiManager.calculateSignalLevel(item.level, 5);
            itemViewHolder.wifiLevelImage.setImageResource(image[level]);

            if(item.capabilities.contains("WPA")){
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        final EditText editText = new EditText(context);
                        editText.setHint("密码");
                        TextView title = new TextView(context);
                        title.setText(item.SSID);
                        title.setGravity(Gravity.CENTER);
                        title.setPadding(0, DimensionUtils.dp2px(16), 0, DimensionUtils.dp2px(16));
                        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
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
