package com.seu.smarthome.ui.main;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seu.smarthome.R;
import com.seu.smarthome.adapter.SceneListAdapter;
import com.seu.smarthome.model.Scene;
import com.seu.smarthome.ui.base.BaseFragment;
import com.seu.smarthome.database.SceneDatabaseHelper;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneFragment extends BaseFragment{

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<String> list;
    private SceneListAdapter adapter;

    private final static String TAG = "SceneFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fgt_scene,null);

        RecyclerView sceneList = (RecyclerView) view.findViewById(R.id.scene_list);
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

        list = new ArrayList<>();
        adapter = new SceneListAdapter(getActivity(), list);
        sceneList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateData();

    }

    private void updateData(){
        SceneDatabaseHelper dbHelper = new SceneDatabaseHelper(getActivity(), "scene.db", null, SceneDatabaseHelper.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from scene", null);
        list.clear();
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex("name"));
                list.add(name);
            }while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        cursor.close();
        db.close();
    }




    @Override
    protected String tag() {
        return TAG;
    }
}
