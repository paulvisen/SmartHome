package com.seu.smarthome.model;

import org.json.JSONObject;

/**
 * Created by jwcui on 2016/5/3.
 */
public class Task {
    public static final int TASK_TYPE_DELAY = 0;
    public static final int TASK_TYPE_LIGHT = 1;
    public static final int TASK_TYPE_FEED = 2;
    public static final int TASK_TYPE_WATER = 3;

    public int taskID;
    public int taskType;
}
