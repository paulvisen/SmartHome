package com.seu.smarthome.model;

import org.json.JSONObject;

public class User {
    public int ID;
    public String phone;
    public String qq;
    public String wechat;
    public String username;
    public String gender;

    public String avatar;

    public static User fromJSON(JSONObject j){
        User user = new User();
        user.gender = j.optString("gender");
        user.ID = j.optInt("id");
        user.phone = j.optString("phone");
        user.qq = j.optString("qq");
        user.username = j.optString("username");
        user.wechat = j.optString("wechat");

        user.avatar = j.optString("avatar");
        return user;
    }

    public String toJSONString(){
        JSONObject j = new JSONObject();
        try {
            j.put("gender",gender);
            j.put("id",ID);
            j.put("phone",phone);
            j.put("qq",qq);
            j.put("username",username);
            j.put("wechat",wechat);
            j.put("avatar",avatar);
        }catch(Exception e){
            // ignore
        }
        return j.toString();
    }
}
