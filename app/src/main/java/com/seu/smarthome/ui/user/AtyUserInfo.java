package com.seu.smarthome.ui.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.intro.AtyEditInfo;
import com.seu.smarthome.util.DimensionUtils;

/**
 * Created by jwcui on 2016/5/1.
 */
public class AtyUserInfo extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout mWholeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_info);
        mWholeLayout = (LinearLayout) findViewById(R.id.aty_info_layout);
        findViewById(R.id.aty_info_more).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aty_info_more:
                final Dialog dialog = new Dialog(AtyUserInfo.this,R.style.DialogSlideAnim);
                View content = LayoutInflater.from(AtyUserInfo.this).inflate(R.layout.aty_info_option,mWholeLayout,false);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.aty_info_option_edit_info){
                            Intent i = new Intent(AtyUserInfo.this, AtyEditInfo.class);
                            startActivity(i);
                        }
                        dialog.dismiss();
                    }
                };
                content.findViewById(R.id.aty_info_option_cancel).setOnClickListener(listener);
                content.findViewById(R.id.aty_info_option_edit_info).setOnClickListener(listener);
                dialog.setContentView(content);

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.BOTTOM | Gravity.START;
                wmlp.x = 0;   //x position
                wmlp.y = 0;   //y position
                wmlp.width = DimensionUtils.getDisplay().widthPixels;
                dialog.show();
        }
    }
}
