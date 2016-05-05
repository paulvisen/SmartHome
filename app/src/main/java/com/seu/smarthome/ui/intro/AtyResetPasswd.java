package com.seu.smarthome.ui.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.LogUtils;
import com.seu.smarthome.util.StrUtils;

import com.seu.smarthome.ui.base.BaseActivity;



/**
 * Created by Administrator on 2016-04-18.
 */
public class AtyResetPasswd extends BaseActivity {

    private final static String TAG="AtyResetPasswd";

    EditText etPhone;
    EditText etVerification;
    EditText etNewPass1;
    EditText etNewPass2;

    TextView tvGetVerification;
    TextView tvCommit;

    TextWatcher mTextWatcher;
    View.OnClickListener mListener;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_reset_passwd);

        Toolbar toolbar=(Toolbar)findViewById(R.id.aty_resetpasswd_toolbar);
        toolbar.setTitle(R.string.login_forgetpasswd);
        toolbar.setTitleTextColor(Color.WHITE);

        etPhone=(EditText)findViewById(R.id.aty_resetpasswd_phone);
        etVerification=(EditText)findViewById(R.id.aty_resetpasswd_verification);
        etNewPass1=(EditText)findViewById(R.id.aty_resetpasswd_newcode);
        etNewPass2=(EditText)findViewById(R.id.aty_resetpasswd_newcode_again);

        tvGetVerification=(TextView)findViewById(R.id.aty_resetpasswd_get_verification);
        tvCommit=(TextView)findViewById(R.id.aty_resetpasswd_commit);

        mTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkText();
            }
        };

        tvGetVerification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ///////服务器通讯 获取验证码
            }

        });

        tvCommit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                changePasswd();
            }
        });

        etPhone.addTextChangedListener(mTextWatcher);
        etVerification.addTextChangedListener(mTextWatcher);
        etNewPass1.addTextChangedListener(mTextWatcher);
        etNewPass2.addTextChangedListener(mTextWatcher);
        tvCommit.setOnClickListener(mListener);


    }

    private void checkText()
    {
        if(etPhone.getText().length()==0)
        {
            tvGetVerification.setEnabled(false);
            tvCommit.setEnabled(false);
            Toast.makeText(getApplicationContext(),"请输入对应的手机号",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!etNewPass1.getText().toString().equals(etNewPass2.getText().toString())){
            tvCommit.setEnabled(false);
            Toast.makeText(getApplicationContext(),"请保持两次出入的密码一致",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(etVerification.getText().toString().length()==0)
        {
            tvCommit.setEnabled(false);
            Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
            return ;
        }

        tvCommit.setEnabled(true);

    }
    private void changePasswd()
    {
        String phone=etPhone.getText().toString();
        String verificationCode=etVerification.getText().toString();
        String passwd=etNewPass1.getText().toString();

        Toast.makeText(getApplicationContext(),"手机"+phone+"  验证码 "+verificationCode+"  密码 "+passwd,Toast.LENGTH_SHORT).show();

        ///////将修改后的密码提交至服务器，重新登录系统
        Intent i=new Intent(AtyResetPasswd.this,AtyLogin.class);
        startActivity(i);


    }


    @Override
    protected  String  tag(){return TAG;}



}
