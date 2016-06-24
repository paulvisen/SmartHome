package com.seu.smarthome.ui.scene;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.model.DelayTask;
import com.seu.smarthome.model.ManualTask;
import com.seu.smarthome.model.Scene;
import com.seu.smarthome.model.Task;
import com.seu.smarthome.ui.common.TimeTaskActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout buttonBar;
    private RelativeLayout startMode;
    private RecyclerView sceneTaskList;
    private SceneTaskListAdapter adapter;
    private List<Task> list;
    private Switch swUseScene;

    private boolean editable = false;
    private Scene scene;
    private SceneDatabaseHelper dbHelper;

    private ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT){
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
    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

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
        scene = new Scene();
        scene.name = intent.getStringExtra("sceneName");
        title.setText(scene.name);

        swUseScene = (Switch)findViewById(R.id.use_scene_switch);
        swUseScene.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                scene.state = isChecked;
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.scene_task_list_swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        list = new ArrayList<>();

        adapter = new SceneTaskListAdapter(this, list);
        sceneTaskList.setAdapter(adapter);

        startMode=(RelativeLayout)findViewById(R.id.start_mode);
        startMode.setOnClickListener(this);

        buttonBar = (LinearLayout) findViewById(R.id.button_bar);
        Button deviceAddButton = (Button)findViewById(R.id.device_add_button);
        deviceAddButton.setOnClickListener(this);
        Button delayAddButton = (Button)findViewById(R.id.delay_add_button);
        delayAddButton.setOnClickListener(this);

        dbHelper = new SceneDatabaseHelper(APP.context(), "scene.db", null, SceneDatabaseHelper.VERSION);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from scene where name = '" + scene.name + "'", null);
                cursor.moveToFirst();
                scene.sceneID = cursor.getInt(cursor.getColumnIndex("id"));
                scene.auto = cursor.getInt(cursor.getColumnIndex("auto")) > 0;
                scene.state = cursor.getInt(cursor.getColumnIndex("state")) > 0;
                scene.startTime = cursor.getInt(cursor.getColumnIndex("starttime"));
                scene.endTime = cursor.getInt(cursor.getColumnIndex("endtime"));
                scene.days = cursor.getInt(cursor.getColumnIndex("days"));

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

                cursor = db.rawQuery("select * from task where sceneid = " + scene.sceneID, null);
                if(cursor.moveToFirst()){
                    do{
                        if(cursor.getInt(cursor.getColumnIndex("tasktype")) == Task.TASK_TYPE_DELAY){
                            DelayTask task = new DelayTask();
                            task.delayTime = cursor.getInt(cursor.getColumnIndex("amount"));
                            list.add(task);
                        }
                        else{
                            ManualTask task = new ManualTask();
                            task.deviceID = cursor.getInt(cursor.getColumnIndex("deviceid"));
                            task.deviceName = cursor.getString(cursor.getColumnIndex("devicename"));
                            task.taskType = cursor.getInt(cursor.getColumnIndex("tasktype"));
                            task.amount = cursor.getInt(cursor.getColumnIndex("amount"));
                            list.add(task);
                        }
                    }while (cursor.moveToNext());
                }
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
                intent.setClass(this, TimeTaskActivity.class);
                intent.putExtra("auto", scene.auto);
                intent.putExtra("start_time", scene.startTime);
                intent.putExtra("end_time", scene.endTime);
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
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("state", scene.state);
            values.put("auto", scene.auto);
            values.put("days", scene.days);
            values.put("starttime", scene.startTime);
            values.put("endtime", scene.endTime);
            db.update("scene", values, "id = ?", new String[]{Integer.toString(scene.sceneID)});
            finish();
        }
        else if(item.getItemId() == R.id.menu_edit){
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
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete("scene", "id = ?", new String[]{Integer.toString(scene.sceneID)});
                            db.delete("task", "sceneid = ?", new String[]{Integer.toString(scene.sceneID)});
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null);
            dialog.show();

        }
        else{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("state", scene.state);
            values.put("auto", scene.auto);
            values.put("days", scene.days);
            values.put("starttime", scene.startTime);
            values.put("endtime", scene.endTime);
            db.update("scene", values, "id = ?", new String[]{Integer.toString(scene.sceneID)});
            db.delete("task", "sceneid = ?", new String[]{Integer.toString(scene.sceneID)});
            for(Task task : list){
                values.clear();
                if(task.taskType == Task.TASK_TYPE_DELAY){
                    DelayTask temp = (DelayTask)task;
                    values.put("sceneid", scene.sceneID);
                    values.put("tasktype", temp.taskType);
                    values.put("amount", temp.delayTime);
                }
                else {
                    ManualTask temp = (ManualTask)task;
                    values.put("sceneid", scene.sceneID);
                    values.put("deviceid", temp.deviceID);
                    values.put("devicename", temp.deviceName);
                    values.put("tasktype", temp.taskType);
                    values.put("amount", temp.amount);
                }
                db.insert("task", null, values);
            }


            buttonBar.setVisibility(View.GONE);
            itemTouchHelper.attachToRecyclerView(null);
            editable = false;
            invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("state", scene.state);
            values.put("auto", scene.auto);
            values.put("days", scene.days);
            values.put("starttime", scene.startTime);
            values.put("endtime", scene.endTime);
            db.update("scene", values, "id = ?", new String[]{Integer.toString(scene.sceneID)});
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    scene.endTime = data.getIntExtra("end_time", 0);
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

    class SceneTaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<Task> list;
        private LayoutInflater layoutInflater;

        public SceneTaskListAdapter(Context context,List<Task> taskList){
            this.list=taskList;
            this.layoutInflater=LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            RecyclerView.ViewHolder viewHolder;
            View view=layoutInflater.inflate(R.layout.scene_task_list_item,parent,false);
            viewHolder=new ItemViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
            Task task=list.get(position);
            if(task!=null){
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

}
