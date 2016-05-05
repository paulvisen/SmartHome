package com.seu.smarthome.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.seu.smarthome.R;

/**
 * Created by Administrator on 2016-04-20.
 */
public class DeviceIdentifyActivity extends AppCompatActivity
    implements  Button.OnClickListener,DialogInterface.OnClickListener{

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_device_identify);
        setTitle("");

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton=(Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("设备更新成功");
        dialog.setMessage("请输入设备名");
        dialog.setView(new EditText(this));
        dialog.setPositiveButton("确定", this);
        dialog.setNegativeButton("取消", null);
        dialog.show();

    }

    @Override
    public void onClick(DialogInterface dialog,int which){
        Intent intent=new Intent();
        intent.setClass(this, ActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

}
