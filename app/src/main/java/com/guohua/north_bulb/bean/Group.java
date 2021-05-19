package com.guohua.north_bulb.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {

    private boolean connected;
    private boolean selected;
    private int groupIcon;
    private String Name;
    private ArrayList<Device> devices;

    public Group() {
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

    public int getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(int groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "Group{" +
                "connected=" + connected +
                ", selected=" + selected +
                ", groupIcon=" + groupIcon +
                ", Name='" + Name + '\'' +
                ", devices=" + devices +
                '}';
    }
}
