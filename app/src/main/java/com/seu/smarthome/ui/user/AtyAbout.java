package com.seu.smarthome.ui.user;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.seu.smarthome.R;

/**
 * Created by jwcui on 2016/6/15.
 */
public class AtyAbout extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_about);
        findViewById(R.id.check_for_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(AtyAbout.this);
                dialog.setMessage("已是最新版本");
                dialog.setPositiveButton("确定",null);
                dialog.setNegativeButton("取消",null);
                dialog.show();
            }
        });
    }
}
