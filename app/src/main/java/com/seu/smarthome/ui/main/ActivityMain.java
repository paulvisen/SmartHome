package com.seu.smarthome.ui.main;

import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.seu.smarthome.R;
import com.seu.smarthome.ui.device.DeviceAddActivity;
import com.seu.smarthome.ui.intro.AtyLogin;
import com.seu.smarthome.ui.scene.SceneAddActivity;
import com.seu.smarthome.widgt.TabItem;

/**
 * Created by Administrator on 2016-04-21.
 */
public class ActivityMain extends AppCompatActivity {

    private static final int PAGE_COUNT = 3;
    private TabItem[] tabItems;
    private ViewPager viewPager;
    private TextView title;
    private final int[] titles = new int[]{
            R.string.equipment,
            R.string.scene,
            R.string.me
    };

    private int tabSelected;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("logout", false))
        {
            Intent i = new Intent(this,AtyLogin.class);
            startActivity(i);
            finish();
            return;
        }
        setContentView(R.layout.aty_main);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView)findViewById(R.id.main_title);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.aty_main_button_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabSelected == 0) {
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain.this, DeviceAddActivity.class);
                    startActivity(intent);
                }
                else if(tabSelected == 1){
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain.this, SceneAddActivity.class);
                    startActivity(intent);
                }
            }
        });

        bindViews();
    }

    private void bindViews(){
        tabItems = new TabItem[3];
        tabItems[0] = (TabItem) findViewById(R.id.tab_device);
        tabItems[1] = (TabItem) findViewById(R.id.tab_scene);
        tabItems[2] = (TabItem) findViewById(R.id.tab_my);
        tabItems[0].setEnable(true);
        tabSelected = 0;

        viewPager = (ViewPager)findViewById(R.id.main_content);
        Adapter adapter = new Adapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                title.setText(titles[position]);
                for (int i = 0; i < PAGE_COUNT; i++) {
                    tabItems[i].setEnable(i == position);
                }
                tabSelected = position;
                //invalidateOptionsMenu();

                if(tabSelected == 2)
                    floatingActionButton.hide();
                else
                    floatingActionButton.show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        View.OnClickListener tabItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int)v.getTag();
                viewPager.setCurrentItem(p);
            }
        };
        for(int i = 0; i<PAGE_COUNT; i++){
            tabItems[i].setTag(i);
            tabItems[i].setOnClickListener(tabItemClickListener);
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(tabSelected == 0 || tabSelected == 1)
            getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(tabSelected == 0) {
            Intent intent = new Intent();
            intent.setClass(this, DeviceAddActivity.class);
            startActivity(intent);
        }
        else if(tabSelected == 1){
            Intent intent = new Intent();
            intent.setClass(this, SceneAddActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/

    public class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new DeviceFragment();
                case 1:
                    return new SceneFragment();
                case 2:
                    return new MyFragment();
                default:
                    throw new RuntimeException("position can not be larger than 2");
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

}