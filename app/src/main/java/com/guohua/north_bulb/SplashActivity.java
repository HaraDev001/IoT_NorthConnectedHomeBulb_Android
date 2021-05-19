package com.guohua.north_bulb;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.guohua.north_bulb.activity.AppIntroActivity;
import com.guohua.north_bulb.activity.MenuActivity;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.util.ToolUtils;

public class SplashActivity extends AppCompatActivity {
    public static final long DELAY = 2000;
    public static final long DELAY_FOR_LOG = 1000;
    private Handler mHandler = new Handler();
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.imageView);

        ToolUtils.requestPermissions(this, Manifest.permission.BLUETOOTH, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH);

        ToolUtils.requestPermissions(this, Manifest.permission.BLUETOOTH_ADMIN, Constant.MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN);

        ToolUtils.requestPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION, Constant.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.default_text, Toast.LENGTH_LONG).show();
            AppContext.getInstance().closeBLEService();
            finish();
        } else {
            mHandler.postDelayed(mRunnable, DELAY);
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                saveBluetoothState(false);
            } else {
                saveBluetoothState(true);
            }
        }

        showTitleAnimation();
    }

    private void showTitleAnimation() {
        Animation animation = new AlphaAnimation(0f, 1.0f);
        animation.setDuration(DELAY_FOR_LOG);
        imageView.startAnimation(animation);
    }

    private void saveBluetoothState(boolean isStart) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constant.KEY_BLUETOOTH_INIT_STATE, isStart).apply();
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startTheActivity();
        }
    };


    private void startTheActivity() {

        Intent intent;
        if (AppContext.preferenceGetBoolean(Constant.IS_APPINTRO, false)) {

            intent = new Intent(this, MenuActivity.class);

        } else {

            intent = new Intent(SplashActivity.this, AppIntroActivity.class);
        }

        startActivity(intent);
        finish();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            mHandler.removeCallbacks(mRunnable);
            startTheActivity();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
