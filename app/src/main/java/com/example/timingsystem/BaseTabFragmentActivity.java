package com.example.timingsystem;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.example.timingsystem.fragment.KeyDwonFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-03-10.
 */
public class BaseTabFragmentActivity extends FragmentActivity {

    protected ActionBar mActionBar;


    protected NoScrollViewPager mViewPager;
    protected ViewPagerAdapter mViewPagerAdapter;


    protected List<KeyDwonFragment> lstFrg = new ArrayList<KeyDwonFragment>();
    protected List<String> lstTitles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    protected void initViewPageData() {

    }

    protected void initViewPager() {


        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), lstFrg, lstTitles);

        mViewPager = (NoScrollViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mViewPagerAdapter);

    }


    protected void initTabs() {
        for (int i = 0; i < mViewPagerAdapter.getCount(); ++i) {
            mActionBar.addTab(mActionBar.newTab()
                    .setText(mViewPagerAdapter.getPageTitle(i))
                    .setTabListener(mTabListener));
        }
    }


    protected ActionBar.TabListener mTabListener = new ActionBar.TabListener() {

        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139) {

            if (event.getRepeatCount() == 0) {

                if (mViewPager != null) {

                    KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
                    sf.myOnKeyDwon();

                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
