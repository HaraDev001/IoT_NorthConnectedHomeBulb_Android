package com.guohua.north_bulb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.EditDeviceActivity;
import com.guohua.north_bulb.activity.EditGroupActivity;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.net.SendRunnable;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.BLECodeUtils;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends BaseAdapter {
    String TAG = "GroupListAdapter ";
    private ArrayList<Group> datas;
    private LayoutInflater mInflater;
    private Context mContext;

    public GroupListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        datas = new ArrayList<Group>();
    }

    public void addGroup(Group data) {

        datas.add(data);
        Log.e(TAG, "data " + data.toString());
        Log.e(TAG, "data1 " + datas.size());
        Log.e(TAG, "data2 " + datas);

        notifyDataSetChanged();
    }

    public void updateGroup(Group data, int pos) {

        datas.set(pos, data);
        Log.e(TAG, "update pos " + pos);
        Log.e(TAG, "updatye data " + data);

        notifyDataSetChanged();
    }

    public void removeGroup(int pos) {

        datas.remove(pos);
        Log.e(TAG, "Remove devovice pos " + pos);
        notifyDataSetChanged();
    }

    public void addAllGroups(ArrayList<Group> data) {
        datas = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (null == convertView) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.row_item_group_list, null);

            viewHolder.iv_Group_icon = (ImageView) convertView.findViewById(R.id.iv_Group_icon);
            viewHolder.iv_on_off = (ImageView) convertView.findViewById(R.id.iv_on_off);
            viewHolder.txt_device_name = (TextView) convertView.findViewById(R.id.txt_device_name);
            viewHolder.txt_group_name = (TextView) convertView.findViewById(R.id.txt_group_name);
            viewHolder.txt_on_off = (TextView) convertView.findViewById(R.id.txt_on_off);
            viewHolder.txt_edit_Group = (TextView) convertView.findViewById(R.id.txt_edit_Group);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Log.e(TAG, "devicestringdata " + datas.get(position));

        if (datas.get(position).getDevices() != null) {

            Boolean allBulbOn = false;
            for (int i = 0; i < datas.get(position).getDevices().size(); i++) {

                if (datas.get(position).getDevices().get(i).isSelected()) {
                    allBulbOn = true;
                }

            }
            if (allBulbOn) {
                viewHolder.iv_on_off.setImageResource(R.drawable.ic_on);
                viewHolder.txt_on_off.setText("On");
            } else {
                viewHolder.iv_on_off.setImageResource(R.drawable.ic_off);
                viewHolder.txt_on_off.setText("Off");
            }

            viewHolder.txt_group_name.setText(datas.get(position).getName());
            viewHolder.iv_Group_icon.setImageResource(datas.get(position).getGroupIcon());
            String deviceName = "";
            for (int i = 0; i < datas.get(position).getDevices().size(); i++) {
                deviceName = deviceName + datas.get(position).getDevices().get(i).getName() + "\n";
            }
            viewHolder.txt_device_name.setText(deviceName);

            viewHolder.iv_on_off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean allBulbOn = false;
                    for (int i = 0; i < datas.get(position).getDevices().size(); i++) {

                        if (datas.get(position).getDevices().get(i).isSelected()) {
                            allBulbOn = true;
                        }
                    }
                    Log.e(TAG, "allBulbOn " + allBulbOn);
                    if (allBulbOn) {

                        Off_devices(datas.get(position).getDevices(), position);

                    } else {

                        On_devices(datas.get(position).getDevices(), position);

                    }
                }
            });
            viewHolder.txt_edit_Group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, EditGroupActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(BLEConstant.EXTRA_GROUP_LIST, datas.get(position));
                    b.putInt(BLEConstant.EXTRA_GROUP_NUMBER, position);
                    intent.putExtras(b);
                    ((Activity) mContext).startActivityForResult(intent, BLEConstant.REQUEST_GROUP_EDI);

                }
            });
        }
        return convertView;
    }

    private void saveThePassword(Device device, String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String deviceAddress;
        deviceAddress = device.getDeviceAddress();
        final String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_PASSWORD, new String[]{sp.getString(deviceAddress, CodeUtils.password), password});
        final String finalDeviceAddress = deviceAddress;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    ThreadPool.getInstance().addTask(new SendRunnable(finalDeviceAddress, data));
                    try {
                        Thread.sleep(Constant.HANDLERDELAY / 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        System.out.println(" centerfragment changePassword deviceAddress: " + deviceAddress + "; data:  " + data);
        sp.edit().putString(deviceAddress, password).apply();
    }

    private void Off_devices(ArrayList<Device> deviceArrayList, int pos) {

        for (int i = 0; i < deviceArrayList.size(); i++) {

            saveThePassword(deviceArrayList.get(i), "0000");
            CodeUtils.setPassword("0000");

            Boolean close = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "close");
            Log.e(TAG, "close " + close);

            if (close)
                datas.get(pos).getDevices().get(i).setSelected(false);
            else {

                Boolean iscon = AppContext.getInstance().isConnect(deviceArrayList.get(i).getDeviceAddress());
                Log.e(TAG, "iscon " + iscon);
                if (!iscon) {
                    Boolean reIscon = AppContext.getInstance().connect(deviceArrayList.get(i).getDeviceAddress());
                    Log.e(TAG, "reIscon " + reIscon);
                    if (reIscon) {
                        close = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "close");
                        Log.e(TAG, "close " + close);
                        if (close)
                            datas.get(pos).getDevices().get(i).setSelected(false);
                    }
                } else {
                    close = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "close");
                    Log.e(TAG, "close " + close);
                    if (close)
                        datas.get(pos).getDevices().get(i).setSelected(false);
                    else {

                        iscon = AppContext.getInstance().isConnect(deviceArrayList.get(i).getDeviceAddress());
                        Log.e(TAG, "iscon1 " + iscon);
                        if (!iscon) {
                            Boolean reIscon = AppContext.getInstance().connect(deviceArrayList.get(i).getDeviceAddress());
                            Log.e(TAG, "reIscon " + reIscon);
                            if (reIscon) {
                                close = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "close");
                                Log.e(TAG, "close1 " + close);
                                if (close)
                                    datas.get(pos).getDevices().get(i).setSelected(false);
                            }
                        } else {
                            close = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "close");
                            Log.e(TAG, "close1 " + close);
                            if (close)
                                datas.get(pos).getDevices().get(i).setSelected(false);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
        updateDevice(datas.get(pos).getDevices());
    }

    private void On_devices(ArrayList<Device> deviceArrayList, int pos) {

        for (int i = 0; i < deviceArrayList.size(); i++) {

            saveThePassword(deviceArrayList.get(i), "0000");
            CodeUtils.setPassword("0000");

            Boolean open = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "open");
            Log.e(TAG, "open " + open);
            if (open)
                datas.get(pos).getDevices().get(i).setSelected(true);
            else {
                Boolean iscon = AppContext.getInstance().isConnect(deviceArrayList.get(i).getDeviceAddress());
                Log.e(TAG, "iscon " + iscon);
                if (!iscon) {
                    Boolean reIscon = AppContext.getInstance().connect(deviceArrayList.get(i).getDeviceAddress());
                    Log.e(TAG, "reIscon " + reIscon);
                    if (reIscon) {
                        open = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "open");
                        Log.e(TAG, "open " + open);
                        if (open)
                            datas.get(pos).getDevices().get(i).setSelected(true);
                    }
                } else {
                    open = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "open");
                    Log.e(TAG, "open1 " + open);
                    if (open)
                        datas.get(pos).getDevices().get(i).setSelected(true);
                    else {
                        iscon = AppContext.getInstance().isConnect(deviceArrayList.get(i).getDeviceAddress());
                        Log.e(TAG, "iscon " + iscon);
                        if (!iscon) {
                            Boolean reIscon = AppContext.getInstance().connect(deviceArrayList.get(i).getDeviceAddress());
                            Log.e(TAG, "reIscon " + reIscon);
                            if (reIscon) {
                                open = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "open");
                                Log.e(TAG, "open1 " + open);
                                if (open)
                                    datas.get(pos).getDevices().get(i).setSelected(true);
                            }
                        } else {
                            open = AppContext.getInstance().on_off(deviceArrayList.get(i).getDeviceAddress(), "open");
                            Log.e(TAG, "open1 " + open);
                            if (open)
                                datas.get(pos).getDevices().get(i).setSelected(true);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
        updateDevice(datas.get(pos).getDevices());
    }

    private void updateDevice(ArrayList<Device> deviceArrayList) {

        Gson gson = new Gson();
        Type typeGroup = new TypeToken<List<Group>>() {
        }.getType();
        String json = gson.toJson(datas, typeGroup);

        AppContext.preferencePutString(BLECodeUtils.GROUP_LIST, json);


        Type typeDevice = new TypeToken<List<Device>>() {
        }.getType();

        String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");

        Log.e(TAG, "Devicelist " + Devicelist);
        if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

            ArrayList<Device> deviceList = gson.fromJson(Devicelist, typeDevice);
            Log.e(TAG, "fromJson " + deviceList);
            for (int i = 0; i < deviceList.size(); i++) {

                for (int j = 0; j < deviceArrayList.size(); j++) {

                    if (deviceList.get(i).getDeviceAddress().equals(deviceArrayList.get(j).getDeviceAddress())
                            && deviceList.get(i).getDeviceName().equals(deviceArrayList.get(j).getDeviceName())) {
                        deviceList.set(i, deviceArrayList.get(j));
                        Log.e(TAG, "j " + j);
                        Log.e(TAG, "i " + i);
                        break;
                    }
                }
            }

            String devicedataSTR = gson.toJson(deviceList, typeDevice);
            AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, devicedataSTR);

        }
    }

    private class ViewHolder {
        ImageView iv_Group_icon, iv_on_off;
        TextView txt_edit_Group, txt_on_off, txt_device_name, txt_group_name;
    }
}
