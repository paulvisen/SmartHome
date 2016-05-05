package com.seu.smarthome.model;

import org.json.JSONObject;

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

    public static DelayTask fromJSON(JSONObject j){
        DelayTask delayTask = new DelayTask();
        delayTask.taskID = j.optInt("id");
        delayTask.taskType = j.optInt("tasktype");
        delayTask.delayTime = j.optInt("delaytime");
        return  delayTask;
    }

    public String toJSONString(){
        JSONObject j = new JSONObject();
        try {
            j.put("tasktype",taskType);
            j.put("delaytime",delayTime);
        }catch(Exception e){
            // ignore
        }
        return j.toString();
    }
}
