package com.seu.smarthome.widgt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seu.smarthome.util.DimensionUtils;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class LoadingView extends LinearLayout {
    ProgressBar mProgressBar;
    TextView mTextView;
    Context mContext;

    public static WindowManager.LayoutParams mWindowParams;

    static {
        mWindowParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,0,0, PixelFormat.TRANSPARENT);
        mWindowParams.gravity = Gravity.CENTER;
    }

    public LoadingView(Context context) {
        super(context);
        mContext = context;
        init();
    }
    private void init(){
        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLACK);
        mProgressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleLarge);
        LayoutParams params = new LayoutParams(DimensionUtils.dp2px(100), DimensionUtils.dp2px(100));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mProgressBar.setLayoutParams(params);
        mTextView = new TextView(mContext);
        mTextView.setText("Text");
        mTextView.setTextSize(20);
        mTextView.setTextColor(Color.WHITE);
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textParams.gravity = Gravity.CENTER_HORIZONTAL;
        mTextView.setLayoutParams(textParams);
        mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(mProgressBar);
        addView(mTextView);

    }
}
