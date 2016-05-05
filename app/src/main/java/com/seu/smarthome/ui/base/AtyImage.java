package com.seu.smarthome.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Liujilong on 2016/1/31.
 * liujilong.me@gmail.com
 */
public abstract class AtyImage extends BaseActivity {

    protected static final int MAX_PICTURE = 9;
    protected static final int REQUEST_IMAGE = 0xad;

    protected ArrayList<String> mChosenPicturePathList;
    protected View.OnClickListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = new ImageListener();
    }


    public class ImageListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AtyImage.this, MultiImageSelectorActivity.class);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            // 最大可选择图片数量
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, MAX_PICTURE);
            // 选择模式
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            // 默认选择
            if(mChosenPicturePathList != null && mChosenPicturePathList.size()>0){
                intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mChosenPicturePathList);
            }
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }
}
