package com.guohua.north_bulb.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.guohua.north_bulb.bean.SceneListInfo;

import java.io.DataOutputStream;
import java.util.List;

/**
 * Created by Leo on 2015/11/16.
 */
public final class ToolUtils {
    /**
     * 读取蓝牙的最初状态
     * @param context
     * @return
     */
    public static boolean readBluetoothState(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constant.KEY_BLUETOOTH_INIT_STATE, false);
    }
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        serviceList.clear();
        return isRunning;
    }

    /**
     * 判断当前是否为wifi网络
     *
     * @param mContext
     * @return
     */
    public static boolean isWifiActive(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error in isNetworkAvailable()");
        }
        return false;
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static void requestPermissions(Activity context, String permission, int myPermissionCode) {
        //android6.0 运行时申请蓝牙权限
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(context, new String[]{permission}, myPermissionCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static int[] getSceneGradientRampByteArray(SceneListInfo.SceneInfo ss){

        System.out.println("getSceneGradientRampByteArray SceneListInfo.SceneInfo ss: " + ss.toString());

        byte ctrMode = (byte) (0x78 + (((ss.SceneCurClickColorImgOnOff[1]<<2) + (ss.SceneCurClickColorImgOnOff[2]<<1) + (ss.SceneCurClickColorImgOnOff[3])) & 0x0ff));

        int deta_red = (ss.SceneGradientRampGradientGap[1] & 0x0ff);
        int deta_green = (ss.SceneGradientRampGradientGap[2] & 0x0ff);
        int deta_blue = (ss.SceneGradientRampGradientGap[3] & 0x0ff);
        int deta_red_time = (ss.SceneGradientRampStopGap[1] & 0x0ff);
        int deta_green_time = (ss.SceneGradientRampStopGap[2] & 0x0ff);
        int deta_blue_time =  (ss.SceneGradientRampStopGap[3] & 0x0ff);

        int sum = deta_red + deta_green + deta_blue + deta_red_time + deta_green_time + deta_blue_time;

        int highBit = 0, lowBit = 0;
        while(sum > 255){
            highBit = (sum & 0xff00) >> 8;
            lowBit = sum & 0x00ff;
            sum =  highBit + lowBit;
        }

        final int[] datas = new int[8];
        datas[0] = ctrMode;
        datas[1] = deta_red;
        datas[2] = deta_green;
        datas[3] = deta_blue;
        datas[4] = deta_red_time;
        datas[5] = deta_green_time;
        datas[6] = deta_blue_time;
        datas[7] = sum;
        System.out.println("getSceneGradientRampByteArray datas: ");
        for (int i = 0; i < datas.length; i++) {
            System.out.print(datas[i] + ";");
        }
        System.out.println();

        return datas;
    }
}
