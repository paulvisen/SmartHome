package com.seu.smarthome.ui.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.ui.main.ActivityMain;
import com.seu.smarthome.util.BitmapUtils;
import com.seu.smarthome.util.DimensionUtils;
import com.seu.smarthome.util.StrUtils;

/**
 * Created by Liujilong on 16/1/20.
 * liujilong.me@gmail.com
 */
public class AtyWelcome extends BaseActivity {
    private static final String TAG = "AtyWelcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_welcome);

        Handler handler = new Handler();
        final ImageView iv = (ImageView) findViewById(R.id.background);
        handler.post(new Runnable() {
            @Override
            public void run() {
                int w = DimensionUtils.getDisplay().widthPixels;
                int h = DimensionUtils.getDisplay().heightPixels;
                final Bitmap bitmap = BitmapUtils.fromResourceAndSize(R.mipmap.splash_background,w,h);
                iv.post(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bitmap);
                    }
                });
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                String token = sp.getString(StrUtils.SP_USER_TOKEN, "");
                if (token.equals("")) {
                    loginIn();
                    overridePendingTransition(0,0);
                }else{
                    main();
                    overridePendingTransition(0,0);
                }
                finish();
            }
        },1000);
    }

    private void loginIn(){
        startActivity(new Intent(AtyWelcome.this, AtyLogin.class));
    }

    private void main(){
        startActivity(new Intent(AtyWelcome.this, ActivityMain.class));
    }



    @Override
    protected String tag() {
        return TAG;
    }
}
