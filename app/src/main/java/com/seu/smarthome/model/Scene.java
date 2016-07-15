package com.seu.smarthome.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.database.SceneDatabaseHelper;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwcui on 2016/5/3.
 */
public class Scene {
    public int sceneID;
    public String name;
    public boolean auto;  //手动/自动
    public boolean state;  //开/关
    public List<Task> tasklist;
    public int startTime;
    public int days;

    private static final SceneDatabaseHelper dbHelper = new SceneDatabaseHelper(APP.context(), "scene.db", null, SceneDatabaseHelper.VERSION);

    public Scene(){
        tasklist = new ArrayList<>();
    }

    private static int getSceneID(Scene scene){
        if(scene.sceneID != 0)
            return scene.sceneID;
        else{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select max(id) as maxid from scene", null);
            if(cursor.moveToFirst())
                scene.sceneID = cursor.getInt(cursor.getColumnIndex("maxid")) + 1;
            else
                scene.sceneID = 1;
            cursor.close();
            db.close();
            return scene.sceneID;
        }
    }

    public static List<Scene> fromJSON(JSONObject object){
        List<Scene> list = new ArrayList<>();
        JSONArray sceneArray = object.optJSONArray("scenelist");
        for(int i = 0; i < sceneArray.length(); ++i){
            JSONObject sceneObject = sceneArray.optJSONObject(i);
            Scene scene = new Scene();
            scene.sceneID = sceneObject.optInt("sceneid");
            scene.name = sceneObject.optString("scenename");
            JSONArray taskArray = sceneObject.optJSONArray("scene");
            int lastStartTime = 0;
            for(int j = 0; j < taskArray.length(); ++j){
                JSONObject taskObject = taskArray.optJSONObject(j);
                String task = taskObject.optString("task");
                try {
                    taskObject = new JSONObject(task);
                    ManualTask manualTask = new ManualTask();
                    manualTask.taskType = taskObject.optInt("devicetype");
                    manualTask.deviceID = taskObject.optInt("deviceid");
                    manualTask.deviceName = taskObject.optString("devicename");
                    manualTask.amount = taskObject.optInt("amount");
                    int starttime = StrUtils.timeStr2Int(taskObject.optString("starttime"));
                    String days = taskObject.optString("days", "");

                    if(!days.equals("")){
                        scene.auto = true;
                        scene.days = StrUtils.daysStr2Int(days);
                        if(j == 0)
                            scene.startTime = starttime;
                    }

                    if(lastStartTime != starttime){
                        if(j != 0){
                            DelayTask delayTask = new DelayTask();
                            delayTask.delayTime = starttime - lastStartTime;
                            scene.tasklist.add(delayTask);
                        }
                        lastStartTime = starttime;
                    }

                    scene.tasklist.add(manualTask);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            list.add(scene);
        }
        return list;
    }

    public static JSONObject toJSON(Scene scene){
        try{
            JSONObject object = new JSONObject();
            object.put("token", StrUtils.token());
            object.put("sceneid", Integer.toString(getSceneID(scene)));
            object.put("name", scene.name);
            JSONArray array = new JSONArray();
            int taskTime = 0;
            if(scene.auto){
                taskTime = scene.startTime;
            }
            for(Task task : scene.tasklist) {
                if (task.taskType == Task.TASK_TYPE_DELAY) {
                    taskTime += ((DelayTask)task).delayTime;
                }
                else{
                    ManualTask temp = (ManualTask)task;
                    JSONObject taskObject = new JSONObject();
                    taskObject.put("tasktype", scene.auto ? "1" : "0");
                    taskObject.put("deviceid", Integer.toString(temp.deviceID));
                    taskObject.put("starttime", StrUtils.timeInt2Str(taskTime));
                    taskObject.put("devicename", temp.deviceName);
                    if(scene.auto){
                        taskObject.put("days", StrUtils.daysInt2Str(scene.days));
                    }

                    taskObject.put("devicetype", Integer.toString(temp.taskType));
                    taskObject.put("amount", Integer.toString(temp.amount));
                    array.put(new JSONObject().put("scenetask", taskObject.toString()));

                }
            }
            object.put("scenelist", array);
            return object;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean existInDB(String name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from scene where name = ?", new String[]{name});
        if(cursor.getCount() != 0){
            Toast.makeText(APP.context(), "场景已存在", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public static void addToDB(Scene scene){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        getSceneID(scene);

        ContentValues values = new ContentValues();
        values.put("id", scene.sceneID);
        values.put("name", scene.name);
        values.put("state", scene.state);
        values.put("auto", scene.auto?1:0);
        values.put("days", scene.days);
        values.put("starttime", scene.startTime);
        db.insert("scene", null, values);

        for(Task task : scene.tasklist){
            if(task.taskType == Task.TASK_TYPE_DELAY){
                DelayTask.addToDB(task, scene.sceneID);
            }
            else {
                ManualTask.addToDB(task, scene.sceneID);
            }
        }

        db.close();
    }

    public static void updateDB(Scene scene){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("state", scene.state);
        values.put("auto", scene.auto);
        values.put("days", scene.days);
        values.put("starttime", scene.startTime);
        db.update("scene", values, "id = ?", new String[]{Integer.toString(scene.sceneID)});

        db.delete("task", "sceneid = ?", new String[]{Integer.toString(scene.sceneID)});
        for(Task task : scene.tasklist){
            if(task.taskType == Task.TASK_TYPE_DELAY){
                DelayTask.addToDB(task, scene.sceneID);
            }
            else {
                ManualTask.addToDB(task, scene.sceneID);
            }
        }
        db.close();
    }

    public static Scene getFromDB(String name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Scene scene = new Scene();
        Cursor cursor = db.rawQuery("select * from scene where name = '" + name + "'", null);
        cursor.moveToFirst();
        scene.sceneID = cursor.getInt(cursor.getColumnIndex("id"));
        scene.name = name;
        scene.auto = cursor.getInt(cursor.getColumnIndex("auto")) > 0;
        scene.state = cursor.getInt(cursor.getColumnIndex("state")) > 0;
        scene.startTime = cursor.getInt(cursor.getColumnIndex("starttime"));
        scene.days = cursor.getInt(cursor.getColumnIndex("days"));

        cursor = db.rawQuery("select * from task where sceneid = " + scene.sceneID, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getInt(cursor.getColumnIndex("tasktype")) == Task.TASK_TYPE_DELAY){
                    DelayTask task = new DelayTask();
                    task.delayTime = cursor.getInt(cursor.getColumnIndex("amount"));
                    scene.tasklist.add(task);
                }
                else{
                    ManualTask task = new ManualTask();
                    task.deviceID = cursor.getInt(cursor.getColumnIndex("deviceid"));
                    task.deviceName = cursor.getString(cursor.getColumnIndex("devicename"));
                    task.taskType = cursor.getInt(cursor.getColumnIndex("tasktype"));
                    task.amount = cursor.getInt(cursor.getColumnIndex("amount"));
                    scene.tasklist.add(task);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return scene;
    }

    public static void deleteFromDB(Scene scene){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("scene", "id = ?", new String[]{Integer.toString(scene.sceneID)});
        db.delete("task", "sceneid = ?", new String[]{Integer.toString(scene.sceneID)});
        db.close();
    }

    public static void clearDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("scene", null, null);
        db.delete("task", null, null);
        db.close();
    }
}
