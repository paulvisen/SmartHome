package com.seu.smarthome.ui.scene;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.model.DelayTask;
import com.seu.smarthome.model.ManualTask;
import com.seu.smarthome.model.Task;
import com.seu.smarthome.ui.common.TimeTaskActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneAddActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView sceneTaskList;
    private RelativeLayout startMode;
    private EditText sceneName;

    private int days;
    private int start_time;
    private int end_time;
    private boolean auto;

    private List<Task> list;
    private SceneTaskListAdapter adapter;

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

        list = new ArrayList<>();

        adapter=new SceneTaskListAdapter(this,list);
        sceneTaskList.setAdapter(adapter);

        startMode=(RelativeLayout)findViewById(R.id.start_mode);
        startMode.setOnClickListener(this);

        sceneName = (EditText)findViewById(R.id.aty_scene_add_scene_name);

        dbHelper = new SceneDatabaseHelper(this, "scene.db", null, SceneDatabaseHelper.VERSION);

    }
    @Override
    public void onClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.start_mode:
                intent.setClass(this, TimeTaskActivity.class);
                intent.putExtra("auto", auto);
                intent.putExtra("start_time", start_time);
                intent.putExtra("end_time", end_time);
                intent.putExtra("days", days);
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
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from scene where name = '" + sceneName.getText() + "'", null);
            if(cursor.getCount() != 0){
                Toast.makeText(APP.context(), "场景已存在", Toast.LENGTH_SHORT).show();
                return true;
            }

            ContentValues values = new ContentValues();
            values.put("name", sceneName.getText().toString());
            values.put("state", 0);
            values.put("auto", auto?1:0);
            values.put("days", days);
            values.put("starttime", start_time);
            values.put("endtime", end_time);
            db.insert("scene", null, values);

            cursor = db.rawQuery("select id from scene where name = '" + sceneName.getText() + "'", null);
            cursor.moveToFirst();

            int sceneid = cursor.getInt(cursor.getColumnIndex("id"));


            for(Task task : list){
                values.clear();
                if(task.taskType == Task.TASK_TYPE_DELAY){
                    DelayTask temp = (DelayTask)task;
                    values.put("sceneid", sceneid);
                    values.put("tasktype", temp.taskType);
                    values.put("amount", temp.delayTime);
                }
                else {
                    ManualTask temp = (ManualTask)task;
                    values.put("sceneid", sceneid);
                    values.put("deviceid", temp.deviceID);
                    values.put("devicename", temp.deviceName);
                    values.put("tasktype", temp.taskType);
                    values.put("amount", temp.amount);
                }
                db.insert("task", null, values);
            }

        }
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
                    auto = data.getBooleanExtra("auto", false);

                    TextView textView = (TextView) findViewById(R.id.start_mode_text);
                    if(auto)
                        textView.setText("定时启动");
                    else
                        textView.setText("手动启动");

                    start_time = data.getIntExtra("start_time", 0);
                    end_time = data.getIntExtra("end_time", 0);
                    days = data.getIntExtra("days", 0);
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

    class  SceneTaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
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
        public int getItemCount() {
            return list==null?0:list.size();
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            ImageView imageView;

            public ItemViewHolder(View view){
                super(view);
                textView=(TextView)view.findViewById(R.id.scene_task);
                imageView = (ImageView)view.findViewById(R.id.scene_image);
            }

        }
    }


}

