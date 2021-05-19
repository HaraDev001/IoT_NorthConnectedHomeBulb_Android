package com.guohua.north_bulb.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.guohua.north_bulb.R;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.fragmentNew.AboutFragment;
import com.guohua.north_bulb.fragmentNew.DeviceFragment;
import com.guohua.north_bulb.fragmentNew.GroupListFragment;
import com.guohua.north_bulb.fragmentNew.TimerListFragment;
import com.guohua.north_bulb.interfaceListner.OnActivityResult;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.util.ToolUtils;
import com.specyci.residemenu.ResideMenu;
import com.specyci.residemenu.ResideMenuItem;


public class MenuActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = MenuActivity.class.getName();

    final static String SIDEBAR_TITLES[] = {"Devices", "Groups", "Timer", "About"};
    final static int SIDEBAR_IDS[] = {0, 1, 2, 3};
    private ResideMenu resideMenu;
    Context mContext;

    private static OnActivityResult onActivityResult;

    public static void setInitListener(OnActivityResult initListener) {
        onActivityResult = initListener;
    }

    private TextView txtTitle;

    int fragment = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MenuActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));//this.getResources().getColor(R.color.colorPrimary));
        }

        setContentView(R.layout.activity_menu);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        setUpMenu();


        ToolUtils.requestPermissions(this, Manifest.permission.BLUETOOTH, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH);
        ToolUtils.requestPermissions(this, Manifest.permission.RECORD_AUDIO, Constant.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        ToolUtils.requestPermissions(this, Manifest.permission.BLUETOOTH_ADMIN, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN);
        ToolUtils.requestPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION, Constant.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        changeFragment(new DeviceFragment());
    }


    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        // resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.6f);

        for (int i = 0; i < SIDEBAR_TITLES.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, 0, SIDEBAR_TITLES[i]);
            item.setId(SIDEBAR_IDS[i]);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
        }

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragment == 0) {
                    changeFragmentWithAnim(new GroupListFragment());
                } else if (fragment == 1) {
                    changeFragmentWithAnim(new TimerListFragment());
                } else if (fragment == 2) {
                    changeFragmentWithAnim(new DeviceFragment());
                }
            }
        });

        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case 0:
                changeFragment(new DeviceFragment());
                break;
            case 1:
                changeFragment(new GroupListFragment());
                break;
            case 2:
                changeFragment(new TimerListFragment());
                break;
            case 3:
                changeFragment(new AboutFragment());
                break;
        }
        resideMenu.closeMenu();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {

        if (targetFragment.getClass().getName().equals(DeviceFragment.class.getName())) {
            fragment = 0;
        } else if (targetFragment.getClass().getName().equals(GroupListFragment.class.getName())) {
            fragment = 1;
        } else if (targetFragment.getClass().getName().equals(TimerListFragment.class.getName())) {
            fragment = 2;
        } else if (targetFragment.getClass().getName().equals(AboutFragment.class.getName())) {
            fragment = 3;
        }

        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        setUpToolbarName();

    }

    private void setUpToolbarName() {
        findViewById(R.id.title_bar_right_menu).setVisibility(View.VISIBLE);
        if (fragment == 0) {
            txtTitle.setText(getString(R.string.label_device));
        } else if (fragment == 1) {
            txtTitle.setText(getString(R.string.label_group));
        } else if (fragment == 2) {
            txtTitle.setText(getString(R.string.label_timer));
        } else if (fragment == 3) {
            txtTitle.setText(getString(R.string.label_About));
            findViewById(R.id.title_bar_right_menu).setVisibility(View.GONE);
        }
    }

    private void changeFragmentWithAnim(Fragment targetFragment) {
//
        if (targetFragment.getClass().getName().equals(DeviceFragment.class.getName())) {
            fragment = 0;
        } else if (targetFragment.getClass().getName().equals(GroupListFragment.class.getName())) {
            fragment = 1;
        } else if (targetFragment.getClass().getName().equals(TimerListFragment.class.getName())) {
            fragment = 2;
        } else {
            return;
        }

        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
//                .setCustomAnimations(
//                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
//                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .commit();

        setUpToolbarName();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);
        if (requestCode == BLEConstant.REQUEST_DEVICE_EDIT && resultCode == BLEConstant.RESULT_DEVICE_DELETE) {
            if (onActivityResult != null) {
                onActivityResult.onactivityresult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult null");
            }
        } else if (requestCode == BLEConstant.REQUEST_DEVICE_EDIT && resultCode == BLEConstant.RESULT_DEVICE_EDIT) {
            if (onActivityResult != null) {
                onActivityResult.onactivityresult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult null");
            }
        } else if (requestCode == BLEConstant.REQUEST_COLOR_PALETTE && resultCode == BLEConstant.RESULT_COLOR_PALETTE) {
            if (onActivityResult != null) {
                onActivityResult.onactivityresult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult null");
            }
        } else if (requestCode == BLEConstant.REQUEST_GROUP_EDI && resultCode == BLEConstant.RESULT_GROUP_EDIT) {
            if (onActivityResult != null) {
                onActivityResult.onactivityresult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult null");
            }
        } else if (requestCode == BLEConstant.REQUEST_GROUP_EDI && resultCode == BLEConstant.RESULT_GROUP_DELETE) {
            if (onActivityResult != null) {
                onActivityResult.onactivityresult(requestCode, resultCode, data);
            } else {
                Log.e(TAG, "onActivityResult null");
            }
        }
    }
}