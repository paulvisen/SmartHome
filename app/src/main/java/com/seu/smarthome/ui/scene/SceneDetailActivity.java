package com.seu.smarthome.ui.scene;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.adapter.SceneTaskListAdapter;
import com.seu.smarthome.model.DelayTask;
import com.seu.smarthome.model.ManualTask;
import com.seu.smarthome.model.Scene;
import com.seu.smarthome.model.Task;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.ui.common.TimeTaskActivity;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneDetailActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = "SceneDetailActivity";

    private LinearLayout buttonBar;
    private RecyclerView sceneTaskList;
    private SceneTaskListAdapter adapter;
    private List<Task> list;
    private Switch swUseScene;

    private boolean editable = false;
    private Scene scene;

    private final ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT){
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(list, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(list, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            list.remove(position);
            adapter.notifyItemRemoved(position);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            viewHolder.itemView.setAlpha(1 - Math.abs(dX) / getResources().getDisplayMetrics().widthPixels);
        }
    };
    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_scene_detail);
        setTitle("");

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sceneTaskList = (RecyclerView)findViewById(R.id.scene_task_list);
        sceneTaskList.setLayoutManager(new LinearLayoutManager(this));
        sceneTaskList.setHasFixedSize(true);

        TextView title = (TextView)findViewById(R.id.title);
        Intent intent = getIntent();
        final String sceneName = intent.getStringExtra("sceneName");
        title.setText(sceneName);

        swUseScene = (Switch)findViewById(R.id.use_scene_switch);
        swUseScene.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                scene.state = isChecked;
                if(isChecked)
                    setSceneOn();
                else
                    setSceneOff();
            }
        });

        adapter = new SceneTaskListAdapter(this, list);
        sceneTaskList.setAdapter(adapter);

        findViewById(R.id.start_mode).setOnClickListener(this);

        buttonBar = (LinearLayout) findViewById(R.id.button_bar);
        Button deviceAddButton = (Button)findViewById(R.id.device_add_button);
        deviceAddButton.setOnClickListener(this);
        Button delayAddButton = (Button)findViewById(R.id.delay_add_button);
        delayAddButton.setOnClickListener(this);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                scene = Scene.getFromDB(sceneName);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        swUseScene.setChecked(scene.state);
                        TextView textView = (TextView) findViewById(R.id.start_mode_text);
                        if (scene.auto) {
                            textView.setText("定时启动");
                        } else {
                            textView.setText("手动启动");
                        }
                    }
                });

                list = scene.tasklist;
                adapter.setList(list);
                adapter.notifyDataSetChanged();

            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(editable)
            getMenuInflater().inflate(R.menu.menu_finish, menu);
        else
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.start_mode:
                if(!editable && !scene.auto){
                    Toast.makeText(APP.context(), "请打开编辑状态进行修改", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent.setClass(this, TimeTaskActivity.class);
                intent.putExtra("auto", scene.auto);
                intent.putExtra("start_time", scene.startTime);
                intent.putExtra("days", scene.days);
                intent.putExtra("editable", editable);
                startActivityForResult(intent, 1);
                break;
            case R.id.device_add_button:
                intent.setClass(this, DeviceListActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.delay_add_button:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay == 0 && minute == 0)
                        {
                            Toast.makeText(APP.context(), "延时时间不应为0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DelayTask task = new DelayTask();
                        task.delayTime = hourOfDay * 60 + minute;
                        list.add(task);
                        adapter.notifyDataSetChanged();
                        sceneTaskList.scrollToPosition(list.size()-1);
                    }
                }, 0, 0, true).show();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        else if(item.getItemId() == R.id.menu_edit){
            if(scene.state){
                Toast.makeText(APP.context(), "请先关闭场景", Toast.LENGTH_SHORT).show();
                return true;
            }
            buttonBar.setVisibility(View.VISIBLE);
            itemTouchHelper.attachToRecyclerView(sceneTaskList);
            editable = true;
            invalidateOptionsMenu();
            Toast.makeText(APP.context(), "可以通过拖动和滑动编辑列表", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.menu_delete){
            AlertDialog.Builder dialog =new AlertDialog.Builder(this)
                    .setMessage("确认删除场景")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            deleteScene();
                        }
                    })
                    .setNegativeButton("取消", null);
            dialog.show();

        }
        else{
            if(!APP.networkConnected){
                Toast.makeText(APP.context(), R.string.network_unconnected, Toast.LENGTH_SHORT).show();
                return true;
            }
            submitScene();

            buttonBar.setVisibility(View.GONE);
            itemTouchHelper.attachToRecyclerView(null);
            editable = false;
            invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        switch(requestCode)
        {
            case 1:
                if(resultCode == RESULT_OK)
                {
                    scene.auto = data.getBooleanExtra("auto", false);
                    TextView textView = (TextView) findViewById(R.id.start_mode_text);
                    if(scene.auto) {
                        textView.setText("定时启动");
                    }
                    else {
                        textView.setText("手动启动");
                    }

                    scene.startTime = data.getIntExtra("start_time", 0);
                    scene.days = data.getIntExtra("days", 0);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    ManualTask task = data.getParcelableExtra("task");
                    list.add(task);
                    adapter.notifyDataSetChanged();
                    sceneTaskList.scrollToPosition(list.size()-1);
                }
                break;
        }
    }

    private void setSceneOn(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("sceneid", Integer.toString(scene.sceneID));
        OkHttpUtils.post(StrUtils.START_SCENE_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                scene.state = true;
                Toast.makeText(APP.context(), "场景已开启", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSceneOff(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("sceneid", Integer.toString(scene.sceneID));
        OkHttpUtils.post(StrUtils.CLOSE_SCENE_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                scene.state = false;
                Toast.makeText(APP.context(), "场景已关闭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitScene(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("sceneid", Integer.toString(scene.sceneID));
        OkHttpUtils.post(StrUtils.DELETE_SCENE_TASK_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                Scene.deleteFromDB(scene);

                JSONObject object = Scene.toJSON(scene);
                OkHttpUtils.post(StrUtils.SUBMIT_SCENE_URL, object, TAG, new OkHttpUtils.SimpleOkCallBack() {
                    @Override
                    public void onResponse(String s) {
                        JSONObject j = OkHttpUtils.parseJSON(s);
                        if (j == null)
                            return;
                        //Scene.updateDB(scene);
                        Scene.addToDB(scene);
                    }
                });
            }
        });

    }

    private void deleteScene(){
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        map.put("sceneid", Integer.toString(scene.sceneID));
        OkHttpUtils.post(StrUtils.DELETE_SCENE_TASK_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                Scene.deleteFromDB(scene);
                finish();
            }
        });
    }

    @Override
    protected String tag(){
        return TAG;
    }

}
