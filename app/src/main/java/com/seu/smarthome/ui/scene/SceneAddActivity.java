package com.seu.smarthome.ui.scene;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneAddActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "SceneAddActivity";

    private RecyclerView sceneTaskList;
    private EditText sceneName;

    private Scene scene;

    private List<Task> list;
    private SceneTaskListAdapter adapter;

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_scene_add);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button deviceAddButton = (Button)findViewById(R.id.device_add_button);
        deviceAddButton.setOnClickListener(this);
        Button delayAddButton = (Button)findViewById(R.id.delay_add_button);
        delayAddButton.setOnClickListener(this);

        sceneTaskList = (RecyclerView) findViewById(R.id.scene_task_list);
        sceneTaskList.setLayoutManager(new LinearLayoutManager(this));
        sceneTaskList.setHasFixedSize(true);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(sceneTaskList);

        scene = new Scene();
        list = new ArrayList<>();
        scene.tasklist = list;

        adapter = new SceneTaskListAdapter(this,list);
        sceneTaskList.setAdapter(adapter);

        findViewById(R.id.start_mode).setOnClickListener(this);

        sceneName = (EditText)findViewById(R.id.aty_scene_add_scene_name);

    }
    @Override
    public void onClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.start_mode:
                intent.setClass(this, TimeTaskActivity.class);
                intent.putExtra("auto", scene.auto);
                intent.putExtra("start_time", scene.startTime);
                intent.putExtra("days", scene.days);
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.menu_finish){
            if(sceneName.getText().toString().equals("")){
                Toast.makeText(APP.context(), "请输入场景名", Toast.LENGTH_SHORT).show();
                return true;
            }


            if(!APP.networkConnected){
                Toast.makeText(APP.context(), R.string.network_unconnected, Toast.LENGTH_SHORT).show();
                return true;
            }

            scene.name = sceneName.getText().toString();
            if(!Scene.existInDB(scene.name)){
                return true;
            }

            addScene();
        }
        else
            finish();
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
                    if(scene.auto)
                        textView.setText("定时启动");
                    else
                        textView.setText("手动启动");

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

    private void addScene(){
        JSONObject object = Scene.toJSON(scene);
        OkHttpUtils.post(StrUtils.SUBMIT_SCENE_URL, object, TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(s);
                if (j == null)
                    return;
                Scene.addToDB(scene);
                finish();
            }
        });
    }

    @Override
    protected String tag(){
        return TAG;
    }


}


