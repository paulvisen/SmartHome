package com.seu.smarthome.widgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.util.BitmapUtils;
import com.seu.smarthome.util.DimensionUtils;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class TabItem extends LinearLayout {
    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView;


    private Drawable mDrawableEnabled;
    private Drawable mDrawableCommon;
    private String mText;
    private int mColorEnabled;
    private int mColorCommon;

    private boolean mEnable = false;
    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabItem);
        mDrawableCommon = a.getDrawable(R.styleable.TabItem_image);
        mText = a.getString(R.styleable.TabItem_text);
        a.recycle();

        mColorEnabled = ContextCompat.getColor(context, R.color.colorPrimary);
        mColorCommon = ContextCompat.getColor(context, R.color.black);

        if(!(mDrawableCommon instanceof BitmapDrawable)){
            throw new RuntimeException("should use BitmapDrawable as TabItem drawable");
        }
        Bitmap b = ((BitmapDrawable)mDrawableCommon).getBitmap();
        Bitmap out = BitmapUtils.changeColor(b,mColorEnabled);
        mDrawableEnabled = new BitmapDrawable(mContext.getResources(), out);
        setOrientation(VERTICAL);

        mImageView = new ImageView(mContext);
        mImageView.setImageDrawable(mDrawableCommon);
        LayoutParams params = new LayoutParams(DimensionUtils.dp2px(23), DimensionUtils.dp2px(23));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, DimensionUtils.dp2px(2), 0, 0);
        addView(mImageView, params);

        mTextView = new TextView(mContext);
        mTextView.setTextSize(12);
        mTextView.setTextColor(mColorCommon);
        mTextView.setText(mText);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(mTextView,params);
    }

    public void setEnable(boolean enable){
        if(mEnable!=enable) {
            mEnable = enable;
            mImageView.setImageDrawable(enable ? mDrawableEnabled : mDrawableCommon);
            mTextView.setTextColor(enable ? mColorEnabled : mColorCommon);
            invalidate();
        }
    }


}
