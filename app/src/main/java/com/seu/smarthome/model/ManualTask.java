package com.seu.smarthome.model;

import org.json.JSONObject;

/**
 * Created by jwcui on 2016/5/3.
 */
public class ManualTask extends Task {
    public int deviceID;
    public int quatity;

    public static ManualTask fromJSON(JSONObject j){
        ManualTask manualTask = new ManualTask();
        manualTask.taskID = j.optInt("id");
        manualTask.deviceID = j.optInt("deviceid");
        manualTask.taskType = j.optInt("tasktype");
        manualTask.quatity = j.optInt("quatity");
        return  manualTask;
    }

    public String toJSONString(){
        JSONObject j = new JSONObject();
        try {
            j.put("deviceid",deviceID);
            j.put("tasktype",taskType);
            j.put("quatity",quatity);
        }catch(Exception e){
            // ignore
        }
        return j.toString();
    }
}
