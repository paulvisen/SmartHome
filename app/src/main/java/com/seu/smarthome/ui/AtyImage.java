package com.seu.smarthome.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;

/**
 * Created by Liujilong on 2016/1/29.
 * liujilong.me@gmail.com
 */
public class AtyImage extends BaseActivity {
    private static final String TAG = "AtyImage";
    public static final String URL_INTENT = "url";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_image);
        String url = getIntent().getStringExtra(URL_INTENT);
        SimpleDraweeView image = (SimpleDraweeView) findViewById(R.id.aty_image_image);
        image.setImageURI(Uri.parse(url));
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
