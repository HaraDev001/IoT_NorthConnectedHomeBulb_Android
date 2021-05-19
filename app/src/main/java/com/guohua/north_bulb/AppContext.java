/**
 * Copyright(C)2016-2020
 * 公司：深圳市国华光电科技有限责任公司
 * 作者：李伟（Leo）
 * QQ:532449175
 */

package com.guohua.north_bulb;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEService;

import java.util.ArrayList;


public class AppContext extends Application {
    public static int currentColor;


    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    private volatile static AppContext mAppContext = null;

    private BLEService mBLEService;
    public boolean isBind;


    public static AppContext getInstance() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Appcontext start");
        mAppContext = this;
        openBLEService();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    private void openBLEService() {
        Intent service = new Intent(this, BLEService.class);
        startService(service);
        isBind = bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
    }



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBLEService = ((BLEService.LocalBinder) service).getService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBLEService = null;
            isBind = false;
        }
    };


    public void closeBLEService() {
        Intent service = new Intent(this, BLEService.class);
        System.out.println(isBind);
        if (isBind) {
            unbindService(mServiceConnection);
            isBind = false;
        }
        stopService(service);
    }


    public boolean connect(String deviceAddress) {

        return mBLEService != null && mBLEService.connect(deviceAddress);
    }

    public boolean disonnect(String deviceAddress, boolean remove) {
        if (mBLEService == null) {
            return false;
        }
        mBLEService.disconnect(deviceAddress, remove);
        return true;
    }

    public boolean isConnect(String deviceAddress) {
        return mBLEService.isConnected(deviceAddress);
    }

    public Boolean on_off(String deviceAddress, String message) {
        return mBLEService.send(deviceAddress, message.getBytes());
    }

    public boolean send(String deviceAddress, String message) {

        if (mBLEService == null) {
            return false;
        }
        boolean isSucc = mBLEService.send(deviceAddress, message.getBytes());

        Log.e("APPCONTEXT", "deviceAddress " + deviceAddress);
        Log.e("APPCONTEXT", "message " + message);
        Log.e("APPCONTEXT", "isSucc " + isSucc);

        return isSucc;
    }

    public boolean send(String deviceAddress, byte[] message) {
        return mBLEService != null && mBLEService.send(deviceAddress, message);
    }


    public void sendAll(String message) {
        for (Device device : devices) {
            if (device.isSelected()) {
                send(device.getDeviceAddress(), message);
                System.out.println("sendAll(String message) message is: " + message);
            }
        }
    }

    public ArrayList<Device> devices = new ArrayList<>();

    public void addDevice(Device device) {
        for (Device temp : devices) {
            if (TextUtils.equals(temp.getDeviceAddress(), device.getDeviceAddress())) {
                return;
            }
        }
        this.devices.add(device);
        connect(device.getDeviceAddress());
    }


    public static void preferencePutInteger(String key, int value) {
        sharedPreferencesEditor.putInt(key, value);
        sharedPreferencesEditor.commit();
    }

    public static int preferenceGetInteger(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void preferencePutBoolean(String key, boolean value) {
        sharedPreferencesEditor.putBoolean(key, value);
        sharedPreferencesEditor.commit();
    }

    public static boolean preferenceGetBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void preferencePutString(String key, String value) {
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.commit();
    }

    public static String preferenceGetString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void preferencePutLong(String key, long value) {
        sharedPreferencesEditor.putLong(key, value);
        sharedPreferencesEditor.commit();
    }

    public static long preferenceGetLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void preferenceRemoveKey(String key) {
        sharedPreferencesEditor.remove(key);
        sharedPreferencesEditor.commit();
    }

    public static void clearPreference() {

        sharedPreferencesEditor.clear();
        sharedPreferencesEditor.commit();
    }

}
