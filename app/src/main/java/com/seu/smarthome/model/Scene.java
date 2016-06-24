package com.seu.smarthome.model;

import java.util.ArrayList;

/**
 * Created by jwcui on 2016/5/3.
 */
public class Scene {
    public int sceneID;
    public String name;
    public boolean auto;  //手动/自动
    public boolean state;  //开/关
    public ArrayList<Task> tasklist;
    public int startTime;
    public int endTime;
    public int days;
}
