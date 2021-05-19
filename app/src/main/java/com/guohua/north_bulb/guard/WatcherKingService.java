package com.guohua.north_bulb.guard;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.guohua.north_bulb.util.Constant;

import java.util.List;

/**
 * @author Leo
 * @detail 双进程守护的King进程King服务 监听Queen进程
 * @time 2015-12-22
 */
public class WatcherKingService extends Service {
    private String TAG = getClass().getName();
    private String serviceName = "com.guohua.glight.guard.WatcherQueenService";
    private String processName = "com.guohua.glight.guard.WatcherQueenService:QueenService";

    private boolean isRunning = true;
    private IWatcherKing king = new IWatcherKing.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void startService() throws RemoteException {
            Intent service = new Intent(WatcherKingService.this, WatcherQueenService.class);
            WatcherKingService.this.startService(service);
        }

        @Override
        public void stopService() throws RemoteException {
            Intent service = new Intent(WatcherKingService.this, WatcherQueenService.class);
            WatcherKingService.this.stopService(service);
        }
    };


    public void onCreate() {
        registerExitReceiver();

        isRunning = true;
        new Thread() {
            public void run() {
                while (isRunning) {
                    //boolean isRun = isServiceRunning(Service2.this,"com.service.demo.Service1");
                    boolean isRun = isProessRunning(WatcherKingService.this, processName);
                    if (isRun == false) {
                        try {
                            king.startService();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    private void registerExitReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constant.ACTION_EXIT);
        mIntentFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    //服务是否运行
    public static boolean isServiceRunning(Context context, String serviceName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> lists = am.getRunningServices(30);

        for (ActivityManager.RunningServiceInfo info : lists) {//判断服务
            if (info.service.getClassName().equals(serviceName)) {
                Log.i("Service1进程", "" + info.service.getClassName());
                isRunning = true;
            }
        }


        return isRunning;
    }

    //进程是否运行
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }

        return isRunning;
    }

    public WatcherKingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return (IBinder) king;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isRunning = false;
            try {
                king.stopService();
                stopSelf();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
}
