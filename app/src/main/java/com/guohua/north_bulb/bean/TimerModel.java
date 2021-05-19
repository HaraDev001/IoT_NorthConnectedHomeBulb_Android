package com.guohua.north_bulb.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TimerModel implements Serializable {

    private boolean connected;
    private boolean selected;
    private Date date;
    // On/Off
    private Boolean Status;

    private Device device;

    public TimerModel() {
        this.connected = false;
        this.selected = true;
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

    public Device getDevices() {
        return device;
    }

    public void setDevices(Device devices) {
        this.device = devices;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "TimerModel{" +
                "connected=" + connected +
                ", selected=" + selected +
                ", Status='" + Status + '\'' +
                ", devices=" + device +
                ", date=" + date +
                '}';
    }
}
