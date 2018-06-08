package com.example.ibulgakov.testapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibulgakov.testapp.fragments.DashboardFragment;
import com.example.ibulgakov.testapp.fragments.HomeFragment;
import com.example.ibulgakov.testapp.fragments.NotificationFragment;

import java.util.HashMap;
import java.util.Map;

public class BottomNavActivity extends AppCompatActivity {

    private String homeTag = "home_nav_tag";
    private String dashboardTag = "home_dashboard_tag";
    private String notificationTag = "home_notify_tag";

    private TextView tvHomeCount;
    private TextView tvDashboardCount;
    private TextView tvNotifyCount;

    HashMap<String,Fragment> fragments = new HashMap<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(fragments.containsKey(homeTag)){
                        fragment = fragments.get(homeTag);
                    } else {
                        fragment = new HomeFragment();
                        fragments.put(homeTag,fragment);
                    }
                    break;

                case R.id.navigation_dashboard:
                    if(fragments.containsKey(dashboardTag)){
                        fragment = fragments.get(dashboardTag);
                    } else {
                        fragment = new DashboardFragment();
                        fragments.put(dashboardTag,fragment);
                    }
                    break;

                case R.id.navigation_notifications:
                    if(fragments.containsKey(notificationTag)){
                        fragment = fragments.get(notificationTag);
                    } else {
                        fragment = new NotificationFragment();
                        fragments.put(notificationTag,fragment);
                    }
                    break;

            }

            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        //loading the default fragment
        Fragment homeFragment = new HomeFragment();
        fragments.put(homeTag,homeFragment);
        loadFragment(homeFragment);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        addCustomViewToNavigation(navigation,R.id.navigation_home);
        addCustomViewToNavigation(navigation,R.id.navigation_dashboard);
        addCustomViewToNavigation(navigation,R.id.navigation_notifications);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_main, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void addCustomViewToNavigation(BottomNavigationView navigation, int navItemViewId){
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);

        BottomNavigationItemView itemView = bottomNavigationMenuView.findViewById(navItemViewId);

        View customView = LayoutInflater.from(this).inflate(R.layout.custom_nav_item, bottomNavigationMenuView, false);
        switch (navItemViewId){
            case R.id.navigation_home:
                tvHomeCount = customView.findViewById(R.id.tv_tab_count);
                tvHomeCount.setText("99+");
                break;
            case R.id.navigation_dashboard:
                tvDashboardCount = customView.findViewById(R.id.tv_tab_count);
                tvDashboardCount.setText("99+");
                break;
            case R.id.navigation_notifications:
                tvNotifyCount = customView.findViewById(R.id.tv_tab_count);
                tvNotifyCount.setText("99+");
                break;
        }
        itemView.addView(customView);
    }

}
