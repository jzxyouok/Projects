package cn.ccxxs.friendcalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ccxxs.friendcalendar.Adapter.ViewPagerAdapter;
import cn.ccxxs.friendcalendar.Fragment.ActivityFragment;
import cn.ccxxs.friendcalendar.Fragment.CalendarFragment;
import cn.ccxxs.friendcalendar.Fragment.ProfileFragment;
import cn.ccxxs.friendcalendar.Login.LoginActivity;
import cn.sharesdk.framework.ShareSDK;

import static cn.ccxxs.friendcalendar.Constants.getAvatar;
import static cn.ccxxs.friendcalendar.Constants.userid;

public class MainActivity extends AppCompatActivity {
    //底部导航
    @Bind(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @Bind(R.id.viewpager) ViewPager viewPager;
    static ViewPagerAdapter adapter;
    //fragment
    CalendarFragment chatFragment;
    ActivityFragment activityFragment;
    ProfileFragment personFragment;
    MenuItem prevMenuItem;
    //定义一个context
    public static Activity main;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        main = this;
        isLogined();
        //Initializing viewPager
        viewPager.setOffscreenPageLimit(2);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_activity:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_chat:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_person:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }
            @Override
            public void onPageScrollStateChanged(int sta) {

            }
        });
        setupViewPager(viewPager);
        initData();
        //隐藏Actionbar
        getSupportActionBar().hide();
        ShareSDK.initSDK(this,"1e74a45971e7c");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        activityFragment=new ActivityFragment();
        chatFragment=new CalendarFragment();
        personFragment=new ProfileFragment();
        adapter.addFragment(activityFragment);
        adapter.addFragment(chatFragment);
        adapter.addFragment(personFragment);
        viewPager.setAdapter(adapter);
    }

    //判断是否登录
    public void isLogined() {
        sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        if (sp.getBoolean("loginState",false) != true){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }
    //初始化数据
    public void initData() {
        userid = sp.getInt("userid",0);
        Constants.username = sp.getString("username","username");
        getAvatar();
    }
    public void refreshAvatar(){
        personFragment.refreshAvatar();
    }
    //刷新界面数据
    public void refreshList() {
            activityFragment.getActivityList();
    }
}
