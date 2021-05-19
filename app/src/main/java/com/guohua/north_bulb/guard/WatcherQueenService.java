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

import com.guohua.north_bulb.util.Constant;

import java.util.List;

/**
 * @author Leo
 * @detail 双进程守护的Queen进程Queen服务 监听King进程
 * @time 2015-12-22
 */
public class WatcherQueenService extends Service {
    private String TAG = getClass().getName();
    //用于判断服务是否运行
    private String serviceName = "com.guohua.glight.guard.WatcherKingService";
    //用于判断进程是否运行
    private String processName = "com.guohua.glight.guard.WatcherKingService:KingService";

    private boolean isRunning = true;

    private IWatcherQueen queen = new IWatcherQueen.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override
        public void startService() throws RemoteException {
            Intent service = new Intent(WatcherQueenService.this, WatcherKingService.class);
            WatcherQueenService.this.startService(service);
        }

        @Override
        public void stopService() throws RemoteException {
            Intent service = new Intent(WatcherQueenService.this, WatcherKingService.class);
            WatcherQueenService.this.stopService(service);
        }
    };

    public WatcherQueenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return (IBinder) queen;
    }

    public void onCreate() {
        registerExitReceiver();//注册退出广播接收器 收到退出广播就退出程序

        isRunning = true;
        new Thread() {
            public void run() {
                while (isRunning) {
                    //boolean isRun = isServiceRunning(Service1.this, ServiceName);
                    boolean isRun = isProessRunning(WatcherQueenService.this, processName);
                    if (isRun == false) {
                        try {
                            queen.startService();
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
    public boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> lists = am.getRunningServices(30);

        for (ActivityManager.RunningServiceInfo info : lists) {// 获取运行服务再启动
            if (info.service.getClassName().equals(serviceName)) {
                isRunning = true;
            }
        }
        return isRunning;

    }

    // 进程是否运行
    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }

        return isRunning;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isRunning = false;
            try {
                queen.stopService();
                stopSelf();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
}
