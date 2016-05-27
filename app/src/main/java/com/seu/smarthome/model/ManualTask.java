package com.seu.smarthome.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by jwcui on 2016/5/3.
 */
public class ManualTask extends Task implements Parcelable{
    public int deviceID;
    public String deviceName;
    public int amount;

    public static ManualTask fromJSON(JSONObject j){
        ManualTask manualTask = new ManualTask();
        manualTask.taskID = j.optInt("id");
        manualTask.deviceID = j.optInt("deviceid");
        manualTask.deviceName = j.optString("devicename", "");
        manualTask.taskType = j.optInt("tasktype");
        manualTask.amount = j.optInt("amount");
        return  manualTask;
    }

    public String toJSONString(){
        JSONObject j = new JSONObject();
        try {
            j.put("deviceid",deviceID);
            j.put("tasktype",taskType);
            j.put("amount",amount);
        }catch(Exception e){
            // ignore
        }
        return j.toString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(taskType);
        dest.writeInt(deviceID);
        dest.writeString(deviceName);
        dest.writeInt(amount);
    }

    public static final Parcelable.Creator<ManualTask> CREATOR = new Parcelable.Creator<ManualTask>(){
        @Override
        public ManualTask createFromParcel(Parcel source){
            ManualTask manualTask = new ManualTask();
            manualTask.taskType = source.readInt();
            manualTask.deviceID = source.readInt();
            manualTask.deviceName = source.readString();
            manualTask.amount = source.readInt();
            return manualTask;
        }

        @Override
        public ManualTask[] newArray(int size){
            return new ManualTask[size];
        }
    };
}
