package com.seu.smarthome.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.user.AtyAbout;
import com.seu.smarthome.ui.user.AtyUserInfo;

/**
 * Created by Administrator on 2016-04-21.
 */
public class MyFragment extends Fragment implements View.OnClickListener{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fgt_me,null);
        view.findViewById(R.id.fgt_me_me).setOnClickListener(this);
        view.findViewById(R.id.fgt_me_about).setOnClickListener(this);
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
}
