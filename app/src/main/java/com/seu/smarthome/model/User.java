package com.seu.smarthome.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class User implements Parcelable{
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
        user.username = j.optString("name");
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
            j.put("name",username);
            j.put("wechat",wechat);
            j.put("avatar",avatar);
        }catch(Exception e){
            e.printStackTrace();
        }
        return j.toString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(username);
        dest.writeString(gender);
        dest.writeString(phone);
        dest.writeString(qq);
        dest.writeString(wechat);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){
        @Override
        public User createFromParcel(Parcel source){
            User user = new User();
            user.username = source.readString();
            user.gender = source.readString();
            user.phone = source.readString();
            user.qq = source.readString();
            user.wechat = source.readString();
            return user;
        }

        @Override
        public User[] newArray(int size){
            return new User[size];
        }
    };
}
