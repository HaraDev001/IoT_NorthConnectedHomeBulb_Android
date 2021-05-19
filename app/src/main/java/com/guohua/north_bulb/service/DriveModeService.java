package com.guohua.north_bulb.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.MenuActivity;
import com.guohua.north_bulb.net.DriveModeSendThread;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.Constant;

/**
 * @author Leo
 * @detail 音乐律动的服务使用Visualizer实现
 * @time 2015-11-09
 */
public class DriveModeService extends Service {

    private static final String TAG = DriveModeService.class.getSimpleName();

    private ThreadPool pool = null;
    //    private Handler mHandler = null;
//    private Timer mTimer;
    private int currentShineColor = -1;
    private int threadDelay = 50;//线程池延迟
    private int shineGap = 50;//闪烁间隔


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mIntent = new Intent(this, MenuActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new Notification.Builder(this).setTicker(getString(R.string.ticker_text))
                .setWhen(System.currentTimeMillis()).setContentInfo(getString(R.string.notification_info)).setSmallIcon(R.drawable.icon)
                .setContentTitle(getString(R.string.notification_title)).setContentText(getString(R.string.notification_content_drivemode))
                .setContentIntent(mPendingIntent).setPriority(Notification.PRIORITY_MAX).build();
        startForeground(-1213, notification);
        //foregroundCompat.startForegroundCompat(-1213, notification);
        flags = START_STICKY;//杀不死
        Log.e("DriveModeService", "shineGap: " + shineGap + "; currentShineColor: " + currentShineColor);

        return super.onStartCommand(intent, flags, startId);
    }


    private static final long DELAY = 3000;

    /**
     * 情景模式 和 音乐律动 不能同时运行
     */
    private void stopVisualizerService() {
        Intent service = new Intent(this, VisualizerService.class);
        stopService(service);
        //Toast.makeText(this, R.string.notice_warning, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        stopVisualizerService();
        /**
         * 注册广播
         */
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constant.ACTION_EXIT);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);

        pool = ThreadPool.getInstance();//得到线程池
    }

    private String data;
    private DriveModeSendThread dms;

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
//        mTimer.cancel();
        if (dms != null) {
            dms.destroyThread();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
//        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("checkedPos", -1).commit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("");
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return new DriveModeServiceBinder();
    }

    private class DriveModeServiceBinder extends Binder implements IDriveModeService {

        @Override
        public void changeShineGap(int progress) {
            shineGap = threadDelay + progress;
            runBicycling(shineGap, currentShineColor);
        }

        public void setCurrentShineColor(int shineColor) {
            currentShineColor = shineColor;
            runBicycling(shineGap, currentShineColor);
        }

        public void stopBicycling() {
            if (dms != null) {
                dms.destroyThread();
            }
        }
    }

    public interface IDriveModeService {

        void changeShineGap(int progress);

        void setCurrentShineColor(int currentShineColor);

        void stopBicycling();
    }

    public void runBicycling(int shineGap, int shineColor) {
        if (dms != null) {
            dms.destroyThread();
        }
        if (shineColor != -1) {
            dms = new DriveModeSendThread(shineGap, shineColor);
            dms.start();
        }
    }

}

