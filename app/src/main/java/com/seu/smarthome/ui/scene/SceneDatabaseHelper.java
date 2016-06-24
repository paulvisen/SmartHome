package com.seu.smarthome.ui.scene;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jwcui on 2016/6/23.
 */
public class SceneDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_SCENE = "create table scene("
            + "id integer primary key autoincrement,"
            + "name text, "
            + "state integer, "
            + "auto integer, "
            + "days integer, "
            + "starttime integer, "
            + "endtime integer)";

    public static final int VERSION = 4;

    private final static String CREATE_TASK = "create table task ("
            + "id integer primary key autoincrement, "
            + "sceneid integer, "
            + "deviceid integer, "
            + "devicename text, "
            + "tasktype integer, "
            + "amount integer)";

    public SceneDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_SCENE);
        db.execSQL(CREATE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists scene");
        db.execSQL("drop table if exists task");
        onCreate(db);
    }
}
