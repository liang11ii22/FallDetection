package com.falldetection.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.falldetection.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener
{


    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private int currentIndex;

    /**
     * top button
     */
    private LinearLayout mTabHome;
    private LinearLayout mTabProfile;
    private LinearLayout mTabContact;
    private LinearLayout mTabSetting;

    private ImageButton imgButtonHome;
    private ImageButton imgButtonProfile;
    private ImageButton imgButtonContact;
    private ImageButton imgButtonSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        initView();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {

            @Override
            public int getCount()
            {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return mFragments.get(arg0);
            }
        };

        mViewPager.setAdapter(mAdapter);
    }


    protected void resetTabBtn()
    {
        imgButtonHome.setImageResource(R.drawable.tab_weixin_pressed);
        imgButtonProfile.setImageResource(R.drawable.tab_find_frd_pressed);
        imgButtonContact.setImageResource(R.drawable.tab_address_pressed);
        imgButtonSetting.setImageResource(R.drawable.tab_settings_pressed);
    }

    private void initView()
    {

        mTabHome = (LinearLayout) findViewById(R.id.id_tab_top_home);
        mTabProfile = (LinearLayout) findViewById(R.id.id_tab_top_profile);
        mTabContact = (LinearLayout) findViewById(R.id.id_tab_top_contact);
        mTabSetting = (LinearLayout) findViewById(R.id.id_tab_top_setting);

        mTabHome.setOnClickListener(this);
        mTabProfile.setOnClickListener(this);
        mTabContact.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);

        imgButtonHome = ((ImageButton) mTabHome.findViewById(R.id.btn_tab_top_home));
        imgButtonProfile = ((ImageButton) mTabProfile.findViewById(R.id.btn_tab_top_profile));
        imgButtonContact = ((ImageButton) mTabContact.findViewById(R.id.btn_tab_top_contact));
        imgButtonSetting = ((ImageButton) mTabSetting.findViewById(R.id.btn_tab_top_setting));

        ProfileTab tab01 = new ProfileTab();
        HomeTab tab02 = new HomeTab();
        MapTab tab03 = new MapTab();
        SettingTab tab04 = new SettingTab();
        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(tab03);
        mFragments.add(tab04);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetTabBtn();
        switch (position)
        {
            case 0:
                imgButtonHome.setImageResource(R.drawable.tab_weixin_pressed);
                break;
            case 1:
                imgButtonProfile.setImageResource(R.drawable.tab_find_frd_pressed);
                break;
            case 2:
                imgButtonContact.setImageResource(R.drawable.tab_address_pressed);
                break;
            case 3:
                imgButtonSetting.setImageResource(R.drawable.tab_settings_pressed);
                break;
        }

        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_tab_top_home){
            mViewPager.setCurrentItem(0);
        }else if(v.getId() == R.id.id_tab_top_profile){
            mViewPager.setCurrentItem(1);
        }else if(v.getId() == R.id.id_tab_top_contact){
            mViewPager.setCurrentItem(2);
        }else if(v.getId() == R.id.id_tab_top_setting){
                mViewPager.setCurrentItem(3);
        }
    }

}
