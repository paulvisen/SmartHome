package com.seu.smarthome.ui.intro;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;

import com.seu.smarthome.R;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class AtyContract extends Activity {
    private TextView tvContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_contract);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aty_contract_toolbar);
        toolbar.setTitle(R.string.user_contract);
        toolbar.setTitleTextColor(Color.WHITE);
        tvContract = (TextView) findViewById(R.id.aty_contract_text);
        TextView tvReturn = (TextView) findViewById(R.id.aty_contract_return);
        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new Thread(){
            @Override
            public void run() {
                final String res;
                try{
                    InputStream in = getResources().openRawResource(R.raw.contract);
                    int length = in.available();

                    byte [] buffer = new byte[length];
                    in.read(buffer);
                    res = new String(buffer,"utf-8");
                    in.close();
                    tvContract.post(new Runnable() {
                        @Override
                        public void run() {
                            tvContract.setText(toDBC(res));
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }.run();
    }

    //将半角转换为全角
    public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] < '\177' && c[i] != '\r' && c[i] != '\n' && c[i] != '\t') {
                c[i] = (char) (c[i] + 65248);
            }
            if(c[i] == '\t')
                c[i] = '\u3000';
        }
        return new String(c);
    }

}
