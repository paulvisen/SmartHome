package com.seu.smarthome.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.seu.smarthome.APP;
import com.seu.smarthome.database.SceneDatabaseHelper;

/**
 * Created by jwcui on 2016/5/3.
 */
public class DelayTask extends Task {
    public int delayTime;
    public DelayTask()
    {
        taskType = TASK_TYPE_DELAY;
        delayTime = 0;
    }

    public static void addToDB(Task task, int sceneid){
        SceneDatabaseHelper dbHelper = new SceneDatabaseHelper(APP.context(), "scene.db", null, SceneDatabaseHelper.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        DelayTask temp = (DelayTask)task;
        values.put("sceneid", sceneid);
        values.put("tasktype", temp.taskType);
        values.put("amount", temp.delayTime);
        db.insert("task", null, values);
        db.close();
    }
}
