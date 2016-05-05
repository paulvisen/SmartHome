package com.seu.smarthome.model;

import java.util.ArrayList;

/**
 * Created by jwcui on 2016/5/3.
 */
public class Scene {
    public int sceneID;
    public boolean manual;  //手动/自动
    public boolean status;  //开/关
    public ArrayList<Task> tasklist;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
}
