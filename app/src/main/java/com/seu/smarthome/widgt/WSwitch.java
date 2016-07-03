package com.seu.smarthome.widgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.util.DimensionUtils;


/**
 * Created by Liujilong on 2016/2/28.
 * liujilong.me@gmail.com
 */
public class WSwitch extends ViewGroup {
    private TextView tvOn;
    private TextView tvOff;
    private String textOn;
    private String textOff;
    private int colorOn;
    private int colorOff;

    private boolean isOn = true;


    public WSwitch(Context context) {
        this(context, null);
    }

    public WSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WSwitch);
        textOn = a.getString(R.styleable.WSwitch_textOn);
        textOff = a.getString(R.styleable.WSwitch_textOff);
        colorOn = a.getColor(R.styleable.WSwitch_colorOn, ContextCompat.getColor(context, R.color.colorPrimary));
        colorOff = a.getColor(R.styleable.WSwitch_colorOff, ContextCompat.getColor(context, R.color.grey_gap));
        a.recycle();

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        tvOn = new TextView(context);
        tvOn.setText(textOn);
        tvOn.setBackgroundColor(colorOn);
        tvOn.setTextColor(colorOff);
        tvOn.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        addView(tvOn,params);

        tvOff = new TextView(context);
        tvOff.setText(textOff);
        tvOff.setBackgroundColor(colorOff);
        tvOff.setTextColor(colorOn);
        tvOff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        addView(tvOff,params);

        tvOn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOn){
                    isOn = true;
                    setOn(true);
                }
            }
        });

        tvOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOn){
                    isOn = false;
                    setOn(false);
                }
            }
        });
    }

    public boolean isOn(){
        return isOn;
    }

    public void setOn(boolean on){
        isOn = on;
        if(on){
            tvOn.setBackgroundColor(colorOn);
            tvOn.setTextColor(colorOff);
            tvOff.setBackgroundColor(colorOff);
            tvOff.setTextColor(colorOn);
        }else{
            tvOn.setBackgroundColor(colorOff);
            tvOn.setTextColor(colorOn);
            tvOff.setBackgroundColor(colorOn);
            tvOff.setTextColor(colorOff);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        int desiredWidth = DimensionUtils.dp2px(100);
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        int desiredHeight = Integer.MAX_VALUE/4;

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        //MUST CALL THIS
        setMeasuredDimension(width, height);
        int widthSpec = MeasureSpec.makeMeasureSpec(width/2,MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        measureChild(tvOn,widthSpec, heightSpec);
        measureChild(tvOff,widthSpec,heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = (r-l)/2;
        int height = b-t;
        tvOn.layout(0,0,width,height);
        tvOff.layout(width,0,2*width,height);
    }
}
