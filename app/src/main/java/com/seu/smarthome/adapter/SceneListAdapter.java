package com.seu.smarthome.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.scene.SceneDetailActivity;

import java.util.List;

/**
 * Created by jwcui on 2016/6/27.
 */
public class SceneListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<String> list;
    private Context context;

    public SceneListAdapter(Context context, List<String> itemList){
        this.list = itemList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scene_list_item, parent, false);
        RecyclerView.ViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String item = list.get(position);
        if(item != null){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.sceneName.setText(item);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView sceneName;

        public ItemViewHolder(View view){
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView = (TextView)view.findViewById(R.id.scene_name);
                    String sceneName = textView.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("sceneName", sceneName);
                    intent.setClass(context, SceneDetailActivity.class);
                    context.startActivity(intent);
                }
            });


            sceneName = (TextView)view.findViewById(R.id.scene_name);
        }
    }


}
