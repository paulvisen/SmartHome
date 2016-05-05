package com.seu.smarthome.ui.base;

import android.app.Fragment;

import com.seu.smarthome.util.OkHttpUtils;

/**
 * Created by Liujilong on 16/1/27.
 * liujilong.me@gmail.com
 */
public abstract class BaseFragment extends Fragment {
    /**
     * set the tag for Activity, which may used for cancel okhttp calls
     * @return the tag for this activity;
     */
    protected abstract String tag();

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.cancel(tag());
    }
}
