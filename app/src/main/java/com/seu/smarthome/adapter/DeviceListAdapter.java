package com.seu.smarthome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.model.Device;

import java.util.List;

/**
 * Created by jwcui on 2016/6/27.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Device> list;
    private Context context;
    private View.OnClickListener listener;

    public DeviceListAdapter(Context context, List<Device> itemList) {
        this.list = itemList;
        this.context = context;
    }

    public void setListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_list_item, parent, false);
        return new ItemViewHolder(view);
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
                    break;
                case Device.DEVICE_TYPE_WATER:
                    itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.water);
                    break;
                case Device.DEVICE_TYPE_FEED:
                    itemViewHolder.deviceTypeImage.setImageResource(R.mipmap.pet);
                    break;
            }
            itemViewHolder.deviceName.setText(item.deviceName);
            itemViewHolder.itemView.setTag(position);
            itemViewHolder.itemView.setOnClickListener(listener);
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