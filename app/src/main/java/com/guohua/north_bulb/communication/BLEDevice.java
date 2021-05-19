package com.guohua.north_bulb.communication;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;


public class BLEDevice {
    public static final int STATE_DISCONNECTED = 0;// 断开连接
    public static final int STATE_CONNECTING = 1;// 连接中
    public static final int STATE_CONNECTED = 2;// 已连接

    public String name;//设备名
    public String address;//设备地址
    public int rssi;//设备信号
    public BluetoothGatt gatt;//设备gatt 用于连接 断开 读写操作
    public BluetoothGattCharacteristic characteristic;//可读可写可通知的特征
    public int state;//连接状态

    public BLEDevice() {
        state = STATE_DISCONNECTED;
    }

    public BLEDevice(String address, BluetoothGatt gatt) {
        this.address = address;
        this.gatt = gatt;
    }

    public BLEDevice(String name, String address, int rssi, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int state) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.gatt = gatt;
        this.characteristic = characteristic;
        this.state = state;
    }

    @Override
    public String toString() {
        return "BLEDevice{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rssi=" + rssi +
                ", gatt=" + gatt +
                ", characteristic=" + characteristic +
                ", state=" + state +
                '}';
    }
}
