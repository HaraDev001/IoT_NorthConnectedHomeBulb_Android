package com.guohua.north_bulb.bean;

import java.io.Serializable;

/**
 * 设备类
 */
public class Device implements Serializable{
    private String deviceName;
    private String deviceAddress;
    private boolean connected;
    private boolean selected;
    private int deviceIcon;
    private String Name;

    public Device() {
        this.connected = false;
        this.selected = true;
    }

    public Device(String deviceName, String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.connected = false;
        this.selected = false;
    }

    public Device(String deviceName, String deviceAddress, boolean selected) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.connected = false;
        this.selected = selected;
    }

    public Device(String deviceName, String deviceAddress, boolean connected, boolean selected) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.connected = connected;
        this.selected = selected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getDeviceIcon() {
        return deviceIcon;
    }

    public void setDeviceIcon(int deviceIcon) {
        this.deviceIcon = deviceIcon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", connected=" + connected +
                ", selected=" + selected +
                ", deviceIcon=" + deviceIcon +
                ", Name='" + Name + '\'' +
                '}';
    }
}
