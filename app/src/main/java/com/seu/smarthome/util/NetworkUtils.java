package com.seu.smarthome.util;

import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;

/**
 * Created by jwcui on 2016/6/25.
 */
public final class NetworkUtils {
    private NetworkUtils(){}

    public static boolean checkNetwork(){
        if(!APP.networkConnected){
            Toast.makeText(APP.context(), R.string.network_unconnected, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
