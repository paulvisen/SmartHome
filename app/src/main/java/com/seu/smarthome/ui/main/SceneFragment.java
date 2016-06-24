package com.seu.smarthome.ui.main;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.scene.SceneDatabaseHelper;
import com.seu.smarthome.ui.scene.SceneDetailActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneFragment extends Fragment{

    private RecyclerView sceneList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<String> list;
    private SceneListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fgt_scene,null);
        sceneList=(RecyclerView)view.findViewById(R.id.scene_list);
        sceneList.setLayoutManager(new LinearLayoutManager(getActivity()));
        sceneList.setHasFixedSize(true);

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.scene_list_swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        list=new ArrayList<>();

        adapter=new SceneListAdapter(list);
        sceneList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateData();
    }

    void updateData(){
        SceneDatabaseHelper dbHelper = new SceneDatabaseHelper(getActivity(), "scene.db", null, SceneDatabaseHelper.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from scene", null);
        if(cursor.moveToFirst()){
            list.clear();
            do{
                String name = cursor.getString(cursor.getColumnIndex("name"));
                list.add(name);
            }while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }

    class SceneListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<String> list;

        public SceneListAdapter(List<String> itemList){
            this.list=itemList;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View view =LayoutInflater.from(getActivity()).inflate(R.layout.scene_list_item, parent, false);
            viewHolder = new ItemViewHolder(view);
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
            return list==null?0:list.size();
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
                        intent.setClass(getActivity(), SceneDetailActivity.class);
                        startActivity(intent);
                    }
                });


                sceneName = (TextView)view.findViewById(R.id.scene_name);
            }
        }


    }



}
