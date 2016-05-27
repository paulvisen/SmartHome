package com.seu.smarthome.ui.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.model.User;
import com.seu.smarthome.ui.intro.AtyEditInfo;
import com.seu.smarthome.ui.intro.AtyLogin;
import com.seu.smarthome.ui.main.ActivityMain;
import com.seu.smarthome.util.DimensionUtils;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jwcui on 2016/5/1.
 */
public class AtyUserInfo extends AppCompatActivity implements View.OnClickListener{

    private User user;
    private LinearLayout mWholeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_info);
        mWholeLayout = (LinearLayout) findViewById(R.id.aty_info_layout);
        findViewById(R.id.aty_info_more).setOnClickListener(this);
    }

    private void getUserInfo(){
        if(!APP.networkConnected){
            Toast.makeText(this, "请连接网络", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_PROFILE_URL, map, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtyUserInfo.this, s);
                if (j == null)
                    return;
                user = User.fromJSON(j);
                ((TextView) findViewById(R.id.aty_info_name)).setText(user.username);
                ((TextView) findViewById(R.id.aty_info_phone)).setText(user.phone);
                ((TextView) findViewById(R.id.aty_info_qq)).setText(user.qq);
                ((TextView) findViewById(R.id.aty_info_we_chat)).setText(user.wechat);
            }
        });
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
                            i.putExtra("user_info", user);
                            startActivity(i);
                        }
                        else if(v.getId() == R.id.aty_info_option_logout){
                            SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, Context.MODE_PRIVATE);
                            sp.edit().remove(StrUtils.SP_USER_TOKEN).apply();
                            Intent i = new Intent(AtyUserInfo.this, ActivityMain.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("logout", true);
                            startActivity(i);
                        }
                        dialog.dismiss();
                    }
                };
                content.findViewById(R.id.aty_info_option_cancel).setOnClickListener(listener);
                content.findViewById(R.id.aty_info_option_edit_info).setOnClickListener(listener);
                content.findViewById(R.id.aty_info_option_logout).setOnClickListener(listener);
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
