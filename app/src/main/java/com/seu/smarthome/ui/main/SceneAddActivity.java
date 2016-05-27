package com.seu.smarthome.ui.main;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seu.smarthome.R;
import com.seu.smarthome.model.DelayTask;
import com.seu.smarthome.model.ManualTask;
import com.seu.smarthome.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016-04-21.
 */
public class SceneAddActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView sceneTaskList;
    private RelativeLayout startMode;

    private List<Task> list;
    private SceneTaskListAdapter adapter;

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

    }
    @Override
    public void onClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.start_mode:
                intent.setClass(this, TimeTaskActivity.class);
                TextView textView = (TextView) findViewById(R.id.start_mode_text);
                intent.putExtra("auto", textView.getText().toString() == "定时启动");
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
                    boolean returnData = data.getBooleanExtra("data_return", false);
                    TextView textView = (TextView) findViewById(R.id.start_mode_text);
                    if(returnData)
                        textView.setText("定时启动");
                    else
                        textView.setText("手动启动");
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


