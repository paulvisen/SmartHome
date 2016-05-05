package com.seu.smarthome.widgt;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Liujilong on 2015/12/11.
 * liujilong.me@gmail.com
 */
public class GridLayout extends ViewGroup {
    private int numInRow = 4;
    private int CELL_SIZE_DIVIDE_GAP_SIZE = 10;
    private Context mContext;


    public void setNumInRow(int num){
        numInRow = num;
        requestLayout();
    }


    public GridLayout(Context context) {
        this(context, null);
        mContext = context;
    }

    public GridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public GridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setImageLists(List<String> lists, View.OnClickListener listener){
        removeAllViews();
        if(lists==null || lists.size()==0){
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        for(String url : lists){
            SimpleDraweeView image = new SimpleDraweeView(mContext);
            addView(image);
            image.setImageURI(Uri.parse(url));
            image.setTag(url);
            image.setOnClickListener(listener);
        }
    }

    public void setImageLists(List<String> lists){
        setImageLists(lists,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = Integer.MAX_VALUE;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }
        int cellSize = getCellSize(width);
        int desiredHeight = calculateDesiredHeight(cellSize);

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
        int measureSpec = MeasureSpec.makeMeasureSpec(cellSize,MeasureSpec.EXACTLY);
        measureChildren(measureSpec, measureSpec);
    }

    private int getCellSize(int width){

        //int rows = (getChildCount()-1)/numInRow +1;
        //  set gap size to 1/CELL_SIZE_DIVIDE_GAP_SIZE of cell size
        //  so width equals to numInRow * cellSize + (numInRow+1)*cellSize/CELL_SIZE_DIVIDE_GAP_SIZE
        return CELL_SIZE_DIVIDE_GAP_SIZE*width / ((CELL_SIZE_DIVIDE_GAP_SIZE+1)*numInRow-1);
    }

    private int calculateDesiredHeight(int cellSize){
        int rows = (getChildCount()-1)/numInRow +1;
        if(getChildCount()==0){
            return 0;
        }else {
            return cellSize * rows + (cellSize / CELL_SIZE_DIVIDE_GAP_SIZE) * (rows - 1);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int cellSizeDivided = width / ((CELL_SIZE_DIVIDE_GAP_SIZE+1)*numInRow-1);
        int count = getChildCount();
        for(int i = 0; i<count; i++){
            View child = getChildAt(i);
            int x = i%numInRow;
            int y = i/numInRow;
            int left = x*cellSizeDivided*(CELL_SIZE_DIVIDE_GAP_SIZE+1);
            int right = left + cellSizeDivided*CELL_SIZE_DIVIDE_GAP_SIZE;
            int top = y*cellSizeDivided*(CELL_SIZE_DIVIDE_GAP_SIZE+1);
            int bottom = top + cellSizeDivided*CELL_SIZE_DIVIDE_GAP_SIZE;
            child.layout(left, top, right, bottom);
        }
    }







}
