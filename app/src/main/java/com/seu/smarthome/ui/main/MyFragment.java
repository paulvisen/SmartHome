package com.seu.smarthome.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.smarthome.APP;
import com.seu.smarthome.R;
import com.seu.smarthome.model.User;
import com.seu.smarthome.ui.base.BaseFragment;
import com.seu.smarthome.ui.user.AtyAbout;
import com.seu.smarthome.ui.user.AtyUserInfo;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-21.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener{
    private User user;
    private final static String TAG = "MyFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fgt_me,null);
        view.findViewById(R.id.fgt_me_me).setOnClickListener(this);
        view.findViewById(R.id.fgt_me_about).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.fgt_me_name)).setText(StrUtils.username());

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.fgt_me_me:
                i = new Intent(getActivity(), AtyUserInfo.class);
                getActivity().startActivity(i);
                break;
            case R.id.fgt_me_about:
                i = new Intent(getActivity(), AtyAbout.class);
                getActivity().startActivity(i);
                break;
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
