package com.guohua.north_bulb.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.MenuActivity;
import com.guohua.north_bulb.activity.VisualizerActivity;
import com.guohua.north_bulb.ai.IObserver;
import com.guohua.north_bulb.ai.ISubject;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.net.SendRunnable;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Leo
 * @detail 音乐律动的服务使用Visualizer实现
 * @time 2015-11-09
 */
public class VisualizerService extends Service implements ISubject, RecordingSampler.updatevisualizer {

    String TAG = "VisualizerService ";
    // 音乐分析
    private Visualizer mVisualizer = null;

    RecordingSampler mRecordingSampler;
    Boolean isPlayinside = false;
    // 通信Handle理
    private ThreadPool pool = null;
    // 常量
    //private static final int RATE = 20000;// 多长时间输出一次
    //private IColorStrategy colorStrategy;
    private int size = 212;
    private int color;
    private Handler mHandler = null;
    private Timer mTimer;
    private static final int LEVEL = 0;
    private Device device;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case Constant.WHAT_CHANGE_COLOR: {
                        Random random = new Random();
                        int r = random.nextInt(7);
                        color = getAutoModeColor(r, LEVEL);
                    }
                    break;
                    default:
                        break;
                }
            }
        };
        initData();
    }

    private int getAutoModeColor(int which, int level) {
        int tcolor;
        switch (which) {
            case 0:
                tcolor = Color.argb(level, 0, 0, 255);
                break;
            case 1:
                tcolor = Color.argb(level, 255, 0, 0);
                break;
            case 2:
                tcolor = Color.argb(level, 0, 255, 0);
                break;
            case 3:
                tcolor = Color.argb(level, 255, 0, 255);
                break;
            case 4:
                tcolor = Color.argb(level, 0, 255, 255);
                break;
            case 5:
                tcolor = Color.argb(level, 255, 255, 0);
                break;
            case 6:
                tcolor = Color.argb(level, 255, 255, 255);
                break;
            default:
                tcolor = Color.argb(level, 255, 255, 255);
                break;
        }

        return tcolor;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent mIntent = new Intent(this, MenuActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new Notification.Builder(this).setTicker(getString(R.string.ticker_text))
                .setWhen(System.currentTimeMillis()).setContentInfo(getString(R.string.notification_info)).setSmallIcon(R.drawable.icon)
                .setContentTitle(getString(R.string.notification_title)).setContentText(getString(R.string.notification_content_visualizer))
                .setContentIntent(mPendingIntent).setPriority(Notification.PRIORITY_MAX).build();
        startForeground(-1213, notification);
        //foregroundCompat.startForegroundCompat(-1213, notification);
        flags = START_STICKY;//杀不死
        return super.onStartCommand(intent, flags, startId);
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(Constant.WHAT_CHANGE_COLOR);
        }
    };

    private static final long DELAY = 5000;

    private void stopShakeService() {
        Intent service = new Intent(this, ShakeService.class);
        stopService(service);
    }

    private void initData() {
        stopShakeService();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constant.ACTION_EXIT);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);

        initValues();
        //colorStrategy = new ColourStrategy();
        IObservers = new ArrayList<>();
        pool = ThreadPool.getInstance();//得到线程池
        initVisualizer();//初始化Visualizer
        mTimer = new Timer();
        mTimer.schedule(timerTask, DELAY, DELAY);
    }

    private void initValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        color = sp.getInt(Constant.KEY_COLOR, Color.argb(LEVEL, 255, 255, 255));
    }

    /**
     * 配置Visualizer对象
     */
    private void initVisualizer() {

        if (mVisualizer != null) {
            if (mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(false);
            }
            mVisualizer.release();
        }

        mVisualizer = new Visualizer(0);
        if (mVisualizer.getEnabled()) {
            mVisualizer.setEnabled(false);
        }

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[Visualizer.getCaptureSizeRange().length - 1]);
        mVisualizer.setDataCaptureListener(mOnDataCaptureListener, Visualizer.getMaxCaptureRate(), true, true);

        if (!mVisualizer.getEnabled()) {
            mVisualizer.setEnabled(true);
        }

        // create AudioRecord
        mRecordingSampler = new RecordingSampler(VisualizerService.this);
        mRecordingSampler.setUpdatevisualizerListener(this);
        mRecordingSampler.setSamplingInterval(200);
        mRecordingSampler.startRecording();

    }


    private OnDataCaptureListener mOnDataCaptureListener = new OnDataCaptureListener() {

        @Override
        public void onWaveFormDataCapture(Visualizer arg0, byte[] fft, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFftDataCapture(Visualizer arg0, byte[] fft, int arg2) {

            // TODO Auto-generated method stub
            if (fft == null || fft.length <= 0) {
                return;
            }
            notifyObserver(fft);
            isPlayinside = false;
            int colorValue = getColorByFft(fft, size, color);
            // Log.e(TAG, "Fft colorValue  " + colorValue);
            if (colorValue == 0) {
                return;
            }
            isPlayinside = true;
            String data = CodeUtils.transARGB2Protocol(colorValue);
            Log.e(TAG, "Fft data  " + data);
            pool.addTask(new SendRunnable(device.getDeviceAddress(), data));

        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constant.ACTION_EXIT)) {
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        stopForeground(true);
        super.onDestroy();
        mTimer.cancel();
        if (mVisualizer != null) {
            if (mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(false);
            }
            mVisualizer.release();
        }
        if (IObservers != null) {
            IObservers.clear();
            IObservers = null;
        }

        /*if (colorStrategy != null) {
            colorStrategy = null;
        }*/
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        mRecordingSampler.release();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub

        Bundle bundle = arg0.getExtras();
        if (bundle != null) {
            device = new Device();
            device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }
        return new VisualizerServiceBinder();
    }


    private class VisualizerServiceBinder extends Binder implements IVisualizerService {
        @Override
        public void registerTheObserver(IObserver IObserver) {
            registerObserver(IObserver);
        }

        @Override
        public void unregisterTheObserver(IObserver IObserver) {
            unregisterObserver(IObserver);
        }

        @Override
        public void changeTheFeel(int progress) {
            size = progress;
        }
    }

    public interface IVisualizerService {
        void registerTheObserver(IObserver IObserver);

        void unregisterTheObserver(IObserver IObserver);

        void changeTheFeel(int progress);
    }

    private ArrayList<IObserver> IObservers = null;//存储观察者

    @Override
    public void registerObserver(IObserver IObserver) {
        IObservers.add(IObserver);
    }

    @Override
    public void unregisterObserver(IObserver IObserver) {
        IObservers.remove(IObserver);
    }

    @Override
    public void notifyObserver(byte[] bytes) {
        if (IObservers == null || IObservers.size() <= 0) {
            return;
        }
        for (IObserver IObserver : IObservers) {
            IObserver.update(bytes);
        }
    }

    private int[] model;
    private double brightStandard, sb, brightness;
    int alpha, red, green, blue;

    public int getColorByFft(byte[] fft, int size, int color) {

        model = fft2Model(fft);
        brightStandard = size * Math.sqrt(2) * 128;//181.019335984

        sb = 0;
        for (int i = 1; i < size + 1; i++) {
            sb += model[i];
        }

        brightness = (sb / brightStandard) * 255;
        alpha = 0;//关闭白灯
        red = (int) (Color.red(color) * brightness / 255);
        green = (int) (Color.green(color) * brightness / 255);
        blue = (int) (Color.blue(color) * brightness / 255);

        color = Color.argb(alpha, red, green, blue);

        return color;
    }


    private byte[] modelByte;
    private int[] unsignedModel;

    private int[] fft2Model(byte[] fft) {

        modelByte = new byte[fft.length / 2 + 1];

        unsignedModel = new int[modelByte.length];

        modelByte[0] = (byte) Math.abs(fft[0]);
        int i, j;
        for (i = 2, j = 1; i < fft.length - 1; i += 2, j++) {
            modelByte[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            unsignedModel[j] = getUnsignedByte(modelByte[j]);
        }
        return unsignedModel;
    }


    private int getUnsignedByte(byte b) {
        return b & 0xff;
    }


    @Override
    public void updateVisualizer(byte[] fftrecord) {

        if (fftrecord == null || fftrecord.length <= 0) {
            return;
        }
        if (!isPlayinside) {
            Log.e(TAG, "fftrecord lenth  " + fftrecord.length);
            int colorValue = getColorByFft(fftrecord, size, color);
//            if (colorValue < 25000 || 6600000 < colorValue) {
//                Log.e(TAG, "fftrecord colorValue  return  " + colorValue);
//                return;
//            }
            if (colorValue <= 0) {
                return;
            }
            notifyObserver(fftrecord);
            Log.e(TAG, "fftrecord colorValue  " + colorValue);
            String data = CodeUtils.transARGB2Protocol(colorValue);
            Log.e(TAG, "fftrecord data  " + data);
            pool.addTask(new SendRunnable(device.getDeviceAddress(), data));
        } else {
            Log.e(TAG, "isPlayinside  " + isPlayinside);
        }
    }
}

