package com.guohua.north_bulb.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.north_bulb.R;
import com.guohua.north_bulb.ai.IObserver;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.service.VisualizerService;
import com.guohua.north_bulb.util.Constant;
import com.guohua.north_bulb.view.VisualizerView;

public class VisualizerActivity extends AppCompatActivity implements IObserver {

    String TAG = "VisualizerActivity ";
    public VisualizerView mVisualizerView;
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        getintent();
        init();
    }

    private void getintent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            device = new Device();
            device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
            Log.e(TAG, "device + " + device.toString());
        }
    }

    private void init() {

        findViewsByIds();

        Intent service = new Intent(this, VisualizerService.class);
        Bundle b = new Bundle();
        b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
        service.putExtras(b);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void findViewsByIds() {

        mVisualizerView = (VisualizerView) findViewById(R.id.vv_show_visualizer);

    }

    private VisualizerService.IVisualizerService mService;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (VisualizerService.IVisualizerService) service;
            if (mService != null) {
                mService.registerTheObserver(VisualizerActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mService != null) {
                mService.unregisterTheObserver(VisualizerActivity.this);
                mService = null;
            }
        }
    };

    @Override
    public void update(byte[] bytes) {
        mVisualizerView.updateVisualizer(bytes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    public void back(View v) {
        this.finish();
    }
}
