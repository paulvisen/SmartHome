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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
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

    private final static String TAG = "DeviceListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_list);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Device> list = new ArrayList<>();
        /*for(int i=0;i<3;i++)
        {
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
        }*/
        RecyclerView deviceList = (RecyclerView)findViewById(R.id.device_list);
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.setHasFixedSize(true);
        adapter = new DeviceListAdapter(this, list);
        deviceList.setAdapter(adapter);

        selectedTask = new ManualTask();
        updateData();
    }

    private void updateData(){
        if(!APP.networkConnected){
            Toast.makeText(APP.context(), "请连接网络", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_DEVICE_LIST_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(DeviceListActivity.this, s);
                if (j == null) {
                    return;
                }
                JSONArray array = j.optJSONArray("devicelist");
                if (array == null)
                    return;
                List<Device> list = new ArrayList<>();
                for (int i = 0; i < array.length(); ++i) {
                    Device device = Device.fromJSON(array.optJSONObject(i));
                    list.add(device);
                }
                adapter.setList(list);
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

    class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<Device> list;
        private Context context;

        public DeviceListAdapter(Context context, List<Device> itemList) {
            this.list = itemList;
            this.context = context;
        }

        public void setList(List<Device> itemList){
            this.list = itemList;
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
            final Device item = list.get(position);
            if(item != null){
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                switch (item.deviceType)
                {
                    case Device.DEVICE_TYPE_LIGHT:
                        itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.light);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                selectedTask.amount = 1;
                                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
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
                            }
                        });
                        break;
                    case Device.DEVICE_TYPE_WATER:
                        itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.water);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final EditText editText = new EditText(context);
                                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                dialog.setTitle("浇水量");
                                dialog.setView(editText);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog,int which){
                                        if(!editText.getText().toString().isEmpty()){
                                            Intent intent = new Intent();
                                            selectedTask.deviceID = item.id;
                                            selectedTask.taskType = item.deviceType;
                                            selectedTask.deviceName = item.deviceName;
                                            selectedTask.amount = Integer.parseInt(editText.getText().toString());
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
                            }
                        });
                        break;
                    case Device.DEVICE_TYPE_FEED:
                        itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.pet);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final EditText editText = new EditText(context);
                                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                dialog.setTitle("喂食量");
                                dialog.setView(editText);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog,int which){
                                        if(!editText.getText().toString().isEmpty()){
                                            Intent intent = new Intent();
                                            selectedTask.deviceID = item.id;
                                            selectedTask.taskType = item.deviceType;
                                            selectedTask.deviceName = item.deviceName;
                                            selectedTask.amount = Integer.parseInt(editText.getText().toString());
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
    @Override
    protected String tag(){
        return TAG;
    }
}
