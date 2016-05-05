package com.seu.smarthome.model;

import org.json.JSONObject;

/**
 * Created by jwcui on 2016/5/3.
 */
public class Device {

    public static final int DEVICE_TYPE_LIGHT = 1;
    public static final int DEVICE_TYPE_FEED = 2;
    public static final int DEVICE_TYPE_WATER = 3;

    public int id;
    public String deviceName;
    public int deviceType;
    public String deviceCode;

    public static Device fromJSON(JSONObject j) {
        Device device = new Device();
        device.id = j.optInt("id");
        device.deviceName = j.optString("devicename");
        device.deviceCode = j.optString("devicecode");
        device.deviceType = j.optInt("devicetype");
        return device;
    }
}
