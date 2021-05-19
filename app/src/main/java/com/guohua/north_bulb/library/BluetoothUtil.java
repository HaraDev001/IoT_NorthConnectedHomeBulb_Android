package com.guohua.north_bulb.library;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

/**
 * Created by Leo on 2016/5/20.
 */
public final class BluetoothUtil {
    /**
     * java反射技术取消蓝牙配对
     *
     * @param device
     */
    @SuppressWarnings("unchecked")
    public static void unpairDevice(BluetoothDevice device) {
        try {
            Class c = BluetoothDevice.class;
            Method m = c.getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void unpairDevice(String address) {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothDevice device = mAdapter.getRemoteDevice(address);
            Class c = BluetoothDevice.class;
            Method m = c.getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
