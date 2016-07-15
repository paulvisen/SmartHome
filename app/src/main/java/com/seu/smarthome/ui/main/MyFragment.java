package com.seu.smarthome.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.base.BaseFragment;
import com.seu.smarthome.ui.user.AtyAbout;
import com.seu.smarthome.ui.user.AtyMyDevice;
import com.seu.smarthome.ui.user.AtyUserInfo;
import com.seu.smarthome.util.StrUtils;

/**
 * Created by Administrator on 2016-04-21.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener{
    private final static String TAG = "MyFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fgt_me,null);
        view.findViewById(R.id.fgt_me_me).setOnClickListener(this);
        view.findViewById(R.id.fgt_me_about).setOnClickListener(this);
        view.findViewById(R.id.fgt_me_device).setOnClickListener(this);
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
            case R.id.fgt_me_device:
                i = new Intent(getActivity(), AtyMyDevice.class);
                getActivity().startActivity(i);
                break;
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
