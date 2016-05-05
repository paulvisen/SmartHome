package com.seu.smarthome.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_list);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Device> list = new ArrayList<>();

        for(int i = 0; i < 3; ++i){
            Device item=new Device();
            item.deviceType = Device.DEVICE_TYPE_LIGHT;
            item.deviceName = "智能照明";
            list.add(item);
            item = new Device();
            item.deviceType = Device.DEVICE_TYPE_WATER;
            item.deviceName = "智能浇花";
            list.add(item);
            item = new Device();
            item.deviceType = Device.DEVICE_TYPE_FEED;
            item.deviceName = "智能喂食";
            list.add(item);
        }

        RecyclerView deviceList = (RecyclerView)findViewById(R.id.device_list);
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.setHasFixedSize(true);
        DeviceListAdapter adapter = new DeviceListAdapter(this, list);
        deviceList.setAdapter(adapter);
    }

    class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<Device> list;
        private Context context;

        public DeviceListAdapter(Context context, List<Device> itemList) {
            this.list = itemList;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View view = LayoutInflater.from(DeviceListActivity.this).inflate(R.layout.device_list_item, parent, false);
            viewHolder = new ItemViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Device item = list.get(position);
            if(item != null){
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                switch (item.deviceType)
                {
                    case Device.DEVICE_TYPE_LIGHT:
                        itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.light);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                dialog.setSingleChoiceItems(new String[]{"开启", "关闭"}, 0, null);
                                dialog.setPositiveButton("确定", null);
                                dialog.setNegativeButton("取消", null);
                                dialog.show();
                            }
                        });
                        break;
                    case Device.DEVICE_TYPE_WATER:
                        itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.water);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                dialog.setTitle("浇水量");
                                dialog.setView(new EditText(context));
                                dialog.setPositiveButton("确定", null);
                                dialog.setNegativeButton("取消", null);
                                dialog.show();
                            }
                        });
                        break;
                    case Device.DEVICE_TYPE_FEED:
                        itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.pet);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                dialog.setTitle("喂食量");
                                dialog.setView(new EditText(context));
                                dialog.setPositiveButton("确定", null);
                                dialog.setNegativeButton("取消", null);
                                dialog.show();
                            }
                        });
                        break;
                }
                itemViewHolder.deviceName.setText(item.deviceName);
            }
        }

        @Override
        public int getItemCount() {
            return list==null?0:list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView deviceTypeImage;
            TextView deviceName;

            public ItemViewHolder(View view){
                super(view);

                deviceTypeImage =(ImageView)view.findViewById(R.id.device_type_image);
                deviceName = (TextView)view.findViewById(R.id.device_name);
            }
        }
    }
}
