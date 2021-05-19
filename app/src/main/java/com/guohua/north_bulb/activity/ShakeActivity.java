package com.guohua.north_bulb.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.north_bulb.R;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.service.ShakeService;
import com.guohua.north_bulb.util.Constant;


public class ShakeActivity extends AppCompatActivity {
    private ImageView shake;
    private TextView  switcher, color;
    private boolean isSwitch = true;
    private int currentValue = 17;
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        getintent();

        Intent service = new Intent(this, ShakeService.class);
        Bundle b = new Bundle();
        b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
        service.putExtras(b);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);

        init();
    }

    public static final String ACTION_SHAKE_A_SHAKE = "shake_a_shake";

    private void getintent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            device = new Device();
            device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_SHAKE_A_SHAKE);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);
    }

    private void init() {
        initValue();
        findViewsByIds();
    }

    private void initValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        isSwitch = sp.getBoolean(Constant.KEY_SHAKE_MODE, true);
    }

    private void saveValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constant.KEY_SHAKE_MODE, isSwitch).apply();
    }

    private void findViewsByIds() {

        shake = (ImageView) findViewById(R.id.iv_shake_shake);
        switcher = (TextView) findViewById(R.id.tv_switch_shake);
        color = (TextView) findViewById(R.id.tv_color_shake);
        color.setOnClickListener(mOnClickListener);
        switcher.setOnClickListener(mOnClickListener);
        changeMode();
    }



    private void changeMode() {
        if (isSwitch) {
            switcher.setBackgroundColor(getResources().getColor(R.color.greya));
            switcher.setTextColor(getResources().getColor(R.color.main));

            color.setBackgroundColor(Color.WHITE);
            color.setTextColor(Color.BLACK);
        } else {
            color.setBackgroundColor(getResources().getColor(R.color.greya));
            color.setTextColor(getResources().getColor(R.color.main));

            switcher.setBackgroundColor(Color.WHITE);
            switcher.setTextColor(Color.BLACK);
        }
        saveValue();
        if (methods != null) {
            methods.changeMode(isSwitch);
        }
    }

    public void shakeAShake() {
        AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        animationSet.addAnimation(animation);
        animationSet.setDuration(100);
        shake.startAnimation(animationSet);
    }

    /**
     * 监听事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_switch_shake:
                    isSwitch = true;
                    break;
                case R.id.tv_color_shake:
                    isSwitch = false;
                    break;
                default:
                    Toast.makeText(ShakeActivity.this, R.string.default_text, Toast.LENGTH_SHORT).show();
                    break;
            }
            changeMode();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_SHAKE_A_SHAKE)) {
                shakeAShake();
            }
        }
    };

    private ShakeService.IShakeService methods = null;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            methods = (ShakeService.IShakeService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (methods != null) {
            methods = null;
        }
        unbindService(mServiceConnection);
    }

    public void back(View v) {
        this.finish();
    }
}
