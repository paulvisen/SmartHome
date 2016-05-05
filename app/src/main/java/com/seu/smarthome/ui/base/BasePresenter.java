package com.seu.smarthome.ui.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Liujilong on 16/1/20.
 * liujilong.me@gmail.com
 */
public abstract class BasePresenter<View> {
    protected Reference<View> mViewRef;

    public void attachView(View view){
        mViewRef = new WeakReference<>(view);
    }

    protected View getView(){
        return mViewRef.get();
    }

    @SuppressWarnings("unused")
    public boolean isViewAttached(){
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView(){
        if(mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }

}
