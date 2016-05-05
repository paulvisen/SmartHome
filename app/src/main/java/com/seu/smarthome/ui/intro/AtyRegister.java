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

import org.json.JSONObject;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.LogUtils;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class AtyRegister extends BaseActivity {
    private static final String TAG = "AtyRegister";

    EditText etName;
    EditText etPass;
    EditText etPass2;
    TextView tvContract;
    TextView tvRegister;
    TextView tvError;

    TextWatcher mTextWatcher;
    View.OnClickListener mListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aty_register_toolbar);
        toolbar.setTitle(R.string.register_weme_user);
        toolbar.setTitleTextColor(Color.WHITE);

        etName = (EditText) findViewById(R.id.aty_register_name);
        etPass = (EditText) findViewById(R.id.aty_register_pass);
        etPass2 = (EditText) findViewById(R.id.aty_register_pass2);
        tvContract = (TextView) findViewById(R.id.aty_register_contract);
        tvRegister = (TextView) findViewById(R.id.aty_register_register);
        tvError = (TextView) findViewById(R.id.aty_register_error);


        tvContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AtyRegister.this, AtyContract.class);
                startActivity(i);
            }
        });

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkText();
            }
        };

        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        };

        etName.addTextChangedListener(mTextWatcher);
        etPass.addTextChangedListener(mTextWatcher);
        etPass2.addTextChangedListener(mTextWatcher);
        tvRegister.setOnClickListener(mListener);

    }

    private void checkText(){
        if(etName.getText().length()==0){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.name_not_empty);
            return;
        }
        if(etPass.getText().length()<6){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.password_long_6);
            return;
        }
        if(!etPass.getText().toString().equals(etPass2.getText().toString())){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.password_not_equal);
            return;
        }
        tvRegister.setEnabled(true);
        tvError.setText("");
    }

    private void register(){
        String name = etName.getText().toString();
        String passMD5 = StrUtils.md5(etPass.getText().toString());
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("username",name);
        param.put("password", passMD5);
        OkHttpUtils.post(StrUtils.REGISTER_URL,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyRegister.this, s);
                if(j == null){
                    return;
                }
                String id = j.optString("id");
                String token = j.optString("token");
                SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER,MODE_PRIVATE);
                sp.edit().putString(StrUtils.SP_USER_ID,id)
                        .putString(StrUtils.SP_USER_TOKEN,token).apply();
                Intent i = new Intent(AtyRegister.this,AtyEditInfo.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
