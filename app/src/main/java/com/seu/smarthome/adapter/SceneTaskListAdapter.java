package com.seu.smarthome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.model.DelayTask;
import com.seu.smarthome.model.ManualTask;
import com.seu.smarthome.model.Task;

import java.util.List;

/**
 * Created by jwcui on 2016/6/26.
 */
public class SceneTaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Task> list;
    private LayoutInflater layoutInflater;

    public SceneTaskListAdapter(Context context,List<Task> taskList){
        this.list = taskList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<Task> taskList){
        this.list = taskList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = layoutInflater.inflate(R.layout.scene_task_list_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
        Task task = list.get(position);
        if(task != null){
            ItemViewHolder itemViewHolder=(ItemViewHolder) holder;
            if(task.taskType == Task.TASK_TYPE_DELAY)
            {
                DelayTask temp = (DelayTask)task;
                String text = "延时";
                text += temp.delayTime/60 > 0 ? temp.delayTime/60 + "时" : "";
                text += temp.delayTime%60 > 0 ? temp.delayTime%60 + "分" : "";
                itemViewHolder.textView.setText(text);
                itemViewHolder.imageView.setImageResource(R.mipmap.clock2);
            }
            else
            {
                ManualTask temp = (ManualTask)task;
                switch (temp.taskType)
                {
                    case Task.TASK_TYPE_LIGHT:
                        itemViewHolder.textView.setText(temp.deviceName + (temp.amount > 0 ? " 开启" : " 关闭"));
                        itemViewHolder.imageView.setImageResource(R.mipmap.light);
                        break;
                    case Task.TASK_TYPE_WATER:
                        itemViewHolder.textView.setText(temp.deviceName +" 浇水量： " + Integer.toString(temp.amount));
                        itemViewHolder.imageView.setImageResource(R.mipmap.water);
                        break;
                    case Task.TASK_TYPE_FEED:
                        itemViewHolder.textView.setText(temp.deviceName +" 喂食量： " + Integer.toString(temp.amount));
                        itemViewHolder.imageView.setImageResource(R.mipmap.pet);
                        break;
                }
            }
        }

    }

    @Override
    public int getItemCount(){
        return list==null?0:list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ItemViewHolder(View view){
            super(view);
            textView = (TextView)view.findViewById(R.id.scene_task);
            imageView = (ImageView)view.findViewById(R.id.scene_image);
        }
    }
}
