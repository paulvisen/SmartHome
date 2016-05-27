package com.seu.smarthome.model;

import org.json.JSONObject;

/**
 * Created by jwcui on 2016/5/3.
 */
public class TimedTask extends Task {
    public int deviceID;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
    public int amount;

    public static TimedTask fromJSON(JSONObject j){
        TimedTask timedTask = new TimedTask();
        timedTask.taskID = j.optInt("id");
        timedTask.deviceID = j.optInt("deviceid");
        timedTask.taskType = j.optInt("tasktype");
        timedTask.amount = j.optInt("amount");
        timedTask.startHour = j.optInt("starthour");
        timedTask.startMinute = j.optInt("startminute");
        timedTask.endHour = j.optInt("endhour");
        timedTask.endMinute = j.optInt("endminute");
        return  timedTask;
    }

    public String toJSONString(){
        JSONObject j = new JSONObject();
        try {
            j.put("deviceid",deviceID);
            j.put("tasktype",taskType);
            j.put("amount",amount);
            j.put("starthour",startHour);
            j.put("startminute",startMinute);
            j.put("endhour",endHour);
            j.put("endminute",endMinute);
        }catch(Exception e){
            // ignore
        }
        return j.toString();
    }
}
