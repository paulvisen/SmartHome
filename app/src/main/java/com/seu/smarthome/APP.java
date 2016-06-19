package com.seu.smarthome;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

public class APP extends Application {
    private static APP mSingleton;
    public static boolean networkConnected;
    public static Context context(){
        return mSingleton.getApplicationContext();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Fresco.initialize(this);
        mSingleton = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null)
            {
                networkConnected = networkInfo.isAvailable();
            }
            else
            {
                networkConnected = false;
            }
        }
    };

}
