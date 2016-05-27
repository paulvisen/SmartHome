package com.seu.smarthome.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import com.seu.smarthome.APP;
import com.seu.smarthome.R;

/**
 * Created by Liujilong on 16/1/22.
 * liujilong.me@gmail.com
 */
public final class StrUtils {

    private StrUtils(){}



    /** ####################### URLs ############################### **/
    private static final String BASE_URL = "http://115.28.190.18:8080/";

    private static final String BASE_URL_NGINX = "http://115.28.190.18/";

    public static final String LOGIN_URL = BASE_URL + "login";

    public static final String REGISTER_URL = BASE_URL + "register";

    public static final String EDIT_PROFILE_URL = BASE_URL + "editprofile";

    public static final String GET_PROFILE_URL = BASE_URL + "getprofile";

    public static final String EDIT_PASSWORD_URL = BASE_URL + "editpasswd";

    public static final String GET_DEVICE_LIST_URL = BASE_URL + "getdevicelist";

    public static final String GET_DEVICE_STATE_URL = BASE_URL + "getdevicestate";

    public static final String GET_DEVICE_HISTORY_URL = BASE_URL + "getdevicedata";

    public static final String GET_DEVICE_DATAILS_URL = BASE_URL + "getdevevicedetails";

    public static final String GET_SCENE_TASK_URL = BASE_URL + "getscenetask";

    public static final String ADD_SCENE_TASK_URL = BASE_URL + "addscenetask";

    public static final String EDIT_SCENE_TASK_URL = BASE_URL + "editscenetask";

    public static final String DELETE_SCENE_TASK_URL = BASE_URL + "delescenetask";

    public static final String GET_AVATAR = BASE_URL_NGINX + "avatar/";

    public static final String UPLOAD_AVATAR_URL = BASE_URL_NGINX + "uploadavatar";

    public static final String GET_BACKGROUND = BASE_URL_NGINX + "background/";

    public static String thumForID(String id){
        return GET_AVATAR + id + "_thumbnail.jpg";
    }

    public static String avatarForID(String id){
        return GET_AVATAR + id;
    }

    public static String backgroundForID(String id){return GET_BACKGROUND + id;}


    /** ################## SharedPreferences ####################### **/

    public static final String SP_USER = "StrUtils_sp_user";
    public static final String SP_USER_TOKEN = SP_USER + "_token";
    public static final String SP_USER_ID = SP_USER + "_id";
    public static final String SP_USER_CAN_FOUND = SP_USER +"_can_found";

    public static final MediaType MEDIA_TYPE_IMG = MediaType.parse("image/*");

    public static String token(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_TOKEN,"");
    }

    public static String id(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_ID,"");
    }

    public static String md5(String input){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputBytes = input.getBytes();
            byte[] outputBytes = messageDigest.digest(inputBytes);
            return bytesToHex(outputBytes);
        }catch (NoSuchAlgorithmException e){
            return "";
        }
    }
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setId(int)
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
