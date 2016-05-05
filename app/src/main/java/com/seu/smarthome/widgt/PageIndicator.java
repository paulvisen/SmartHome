package com.seu.smarthome.widgt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.seu.smarthome.util.DimensionUtils;

/**
 * Created by Liujilong on 16/1/27.
 * liujilong.me@gmail.com
 */
public class PageIndicator extends View implements ViewPager.OnPageChangeListener{
    @SuppressWarnings("unused")
    private final static String TAG = "PageIndicator";
    private ViewPager mViewPager;
    private int mRadius;
    private int mStrokeWidth;
    private int mCurrentPage;

    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);


    public PageIndicator(Context context) {
        this(context,null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        mRadius = DimensionUtils.dp2px(5);
        mStrokeWidth = DimensionUtils.dp2px(1);

        mPaintFill.setColor(0xffffffff);
        mPaintFill.setStyle(Paint.Style.FILL);

        mPaintStroke.setColor(0xffffffff);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(DimensionUtils.dp2px(1));
    }

    public void setViewPager(ViewPager viewPager){
        requestLayout();
        setCurrentItem(0);
        if(mViewPager == viewPager) {
            requestLayout();
            return;
        }
        if(mViewPager!=null){
            mViewPager.removeOnPageChangeListener(this);
        }
        if(viewPager.getAdapter()==null){
            throw new IllegalStateException("ViewPagers does not have adapter instance");
        }
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    public void setCurrentItem(int item) {
        mCurrentPage = item;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mViewPager==null){
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if(count == 0){
            return;
        }
        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        for(int i = 0; i<count; i++){
            float dx = leftPadding + i*3*mRadius + mRadius;
            float dy = topPadding + mRadius;
            if(i == mCurrentPage){
                canvas.drawCircle(dx, dy, mRadius, mPaintFill);
            }else {
                canvas.drawCircle(dx, dy, mRadius-mStrokeWidth/2, mPaintStroke);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if((specMode==MeasureSpec.EXACTLY)||mViewPager==null){
            result = specSize;
        }else{
            final int count = mViewPager.getAdapter().getCount();
            result = getPaddingLeft()+getPaddingRight()+(count*2*mRadius)+(count-1)*mRadius+mStrokeWidth;
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }
    private int measureHeight(int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if((specMode==MeasureSpec.EXACTLY)||mViewPager==null){
            result = specSize;
        }else{
            result = getPaddingLeft()+getPaddingRight()+2*mRadius+mStrokeWidth;
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        invalidate();
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }


}
