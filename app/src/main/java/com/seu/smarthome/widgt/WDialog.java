package com.seu.smarthome.widgt;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by Liujilong on 2016/2/17.
 * liujilong.me@gmail.com
 */
public class WDialog extends Dialog {
    public WDialog(Context context) {
        super(context);
    }

    public WDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
//    @Override
//    public void show() {
//        super.show();
//        //set the dialog fullscreen
//
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContentView
//                .getLayoutParams();
//        layoutParams.width = display.getWidth();
//        layoutParams.height = display.getHeight();
//        mContentView.setLayoutParams(layoutParams);
//    }
}
