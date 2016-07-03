package com.seu.smarthome.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by jwcui on 2016/5/3.
 */
public class Device implements Parcelable{

    public static final int DEVICE_TYPE_LIGHT = 1;
    public static final int DEVICE_TYPE_FEED = 2;
    public static final int DEVICE_TYPE_WATER = 3;

    public int id;
    public String deviceName;
    public int deviceType;

    public static Device fromJSON(JSONObject j) {
        Device device = new Device();
        device.id = j.optInt("deviceid");
        device.deviceName = j.optString("name");
        String type = j.optString("type");
        switch(type){
            case "照明":
                device.deviceType = DEVICE_TYPE_LIGHT;
                break;
            case "喂食":
                device.deviceType = DEVICE_TYPE_FEED;
                break;
            case "浇花":
                device.deviceType = DEVICE_TYPE_WATER;
                break;
        }
        return device;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(deviceName);
        dest.writeInt(deviceType);
    }

    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>(){
        @Override
        public Device createFromParcel(Parcel source){
            Device device = new Device();
            device.id = source.readInt();
            device.deviceName = source.readString();
            device.deviceType = source.readInt();
            return device;
        }

        @Override
        public Device[] newArray(int size){
            return new Device[size];
        }
    };

}
