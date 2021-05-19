package com.guohua.north_bulb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.EditDeviceActivity;
import com.guohua.north_bulb.activity.PalletActivity;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.TimerModel;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.net.SendRunnable;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.BLECodeUtils;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeviceListAdapter extends BaseAdapter {
    String TAG = "DeviceListAdapter ";
    private ArrayList<Device> datas;
    private LayoutInflater mInflater;
    private Context mContext;
    private HashMap<LinearLayout, CountDownTimer> counters;

    public DeviceListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        datas = new ArrayList<Device>();
        this.counters = new HashMap<LinearLayout, CountDownTimer>();
    }

    public void addDevices(Device data) {

        datas.add(data);
        notifyDataSetChanged();
    }

    public void updateDevices(Device data, int pos) {

        datas.set(pos, data);
        notifyDataSetChanged();
    }

    public void removeDevice(int pos) {

        datas.remove(pos);
        notifyDataSetChanged();
    }

    public void addAllDevices(ArrayList<Device> data) {
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

            convertView = mInflater.inflate(R.layout.row_item_device_list, null);

            viewHolder.iv_device_icon = (ImageView) convertView.findViewById(R.id.iv_device_icon);
            viewHolder.iv_on_off = (ImageView) convertView.findViewById(R.id.iv_on_off);
            viewHolder.txt_device_name = (TextView) convertView.findViewById(R.id.txt_device_name);
            viewHolder.txt_edit_device = (TextView) convertView.findViewById(R.id.txt_edit_device);
            viewHolder.ll_notConnected = (LinearLayout) convertView.findViewById(R.id.ll_notConnected);
            viewHolder.ll_Connected = (LinearLayout) convertView.findViewById(R.id.ll_Connected);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (datas.get(position).isSelected()) {
            viewHolder.iv_on_off.setImageResource(R.drawable.ic_on);
        } else {
            viewHolder.iv_on_off.setImageResource(R.drawable.ic_off);
        }

        viewHolder.txt_device_name.setText(datas.get(position).getName());
        viewHolder.iv_device_icon.setImageResource(datas.get(position).getDeviceIcon());

        final LinearLayout tv = viewHolder.ll_notConnected;
        CountDownTimer cdt = counters.get(viewHolder.ll_notConnected);
        if (cdt != null) {
            cdt.cancel();
            cdt = null;
        }
        long difference = 10000;
        cdt = new CountDownTimer(difference, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int days = 0;
                int hours = 0;
                int minutes = 0;
                int seconds = 0;
                String sDate = "";

                if (millisUntilFinished > DateUtils.DAY_IN_MILLIS) {
                    days = (int) (millisUntilFinished / DateUtils.DAY_IN_MILLIS);
                    //sDate += days + "d";
                }

                millisUntilFinished -= (days * DateUtils.DAY_IN_MILLIS);

                if (millisUntilFinished > DateUtils.HOUR_IN_MILLIS) {
                    hours = (int) (millisUntilFinished / DateUtils.HOUR_IN_MILLIS);
                }

                millisUntilFinished -= (hours * DateUtils.HOUR_IN_MILLIS);

                if (millisUntilFinished > DateUtils.MINUTE_IN_MILLIS) {
                    minutes = (int) (millisUntilFinished / DateUtils.MINUTE_IN_MILLIS);
                }

                millisUntilFinished -= (minutes * DateUtils.MINUTE_IN_MILLIS);

                if (millisUntilFinished > DateUtils.SECOND_IN_MILLIS) {
                    seconds = (int) (millisUntilFinished / DateUtils.SECOND_IN_MILLIS);
                }

                sDate = String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + " min Remaining";

            }

            @Override
            public void onFinish() {
                tv.setVisibility(View.GONE);
            }
        };

        counters.put(tv, cdt);
        Boolean iscon = AppContext.getInstance().isConnect(datas.get(position).getDeviceAddress());
        Log.e(TAG, "iscon " + iscon);
        if (!iscon) {
            cdt.start();
        } else {
            tv.setVisibility(View.GONE);
        }

        viewHolder.iv_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveThePassword(datas.get(position), "0000");
                CodeUtils.setPassword("0000");

                if (!datas.get(position).isSelected()) {

                    Boolean asd = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "open");
                    Log.e(TAG, "open " + asd);
                    if (asd)
                        datas.get(position).setSelected(true);
                    else {
                        Boolean iscon = AppContext.getInstance().isConnect(datas.get(position).getDeviceAddress());
                        Log.e(TAG, "iscon " + iscon);
                        if (!iscon) {
                            Boolean reIscon = AppContext.getInstance().connect(datas.get(position).getDeviceAddress());
                            Log.e(TAG, "reIscon " + reIscon);
                            if (reIscon) {
                                asd = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "open");
                                Log.e(TAG, "open " + asd);
                                if (asd)
                                    datas.get(position).setSelected(true);
                            }
                        } else {
                            asd = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "open");
                            Log.e(TAG, "open1 " + asd);
                            if (asd)
                                datas.get(position).setSelected(true);
                            else {
                                iscon = AppContext.getInstance().isConnect(datas.get(position).getDeviceAddress());
                                Log.e(TAG, "iscon1 " + iscon);
                                if (!iscon) {
                                    Boolean reIscon = AppContext.getInstance().connect(datas.get(position).getDeviceAddress());
                                    Log.e(TAG, "reIscon1 " + reIscon);
                                    if (reIscon) {
                                        asd = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "open");
                                        Log.e(TAG, "open1 " + asd);
                                        if (asd)
                                            datas.get(position).setSelected(true);
                                    }
                                } else {
                                    asd = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "open");
                                    Log.e(TAG, "open1 " + asd);
                                    if (asd)
                                        datas.get(position).setSelected(true);
                                }
                            }
                        }
                    }
                    updateDevice();
                } else {
                    Boolean das = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "close");
                    Log.e(TAG, "Close " + das);

                    if (das)
                        datas.get(position).setSelected(false);
                    else {

                        Boolean iscon = AppContext.getInstance().isConnect(datas.get(position).getDeviceAddress());
                        Log.e(TAG, "iscon " + iscon);
                        if (!iscon) {
                            Boolean reIscon = AppContext.getInstance().connect(datas.get(position).getDeviceAddress());
                            Log.e(TAG, "reIscon " + reIscon);
                            if (reIscon) {
                                das = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "close");
                                Log.e(TAG, "Close " + das);
                                if (das)
                                    datas.get(position).setSelected(false);
                            }
                        } else {
                            das = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "close");
                            Log.e(TAG, "das " + das);
                            if (das)
                                datas.get(position).setSelected(false);
                            else {

                                iscon = AppContext.getInstance().isConnect(datas.get(position).getDeviceAddress());
                                Log.e(TAG, "iscon1 " + iscon);
                                if (!iscon) {
                                    Boolean reIscon = AppContext.getInstance().connect(datas.get(position).getDeviceAddress());
                                    Log.e(TAG, "reIscon1 " + reIscon);
                                    if (reIscon) {
                                        das = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "close");
                                        Log.e(TAG, "Close1 " + das);
                                        if (das)
                                            datas.get(position).setSelected(false);
                                    }
                                } else {
                                    das = AppContext.getInstance().on_off(datas.get(position).getDeviceAddress(), "close");
                                    Log.e(TAG, "Close1 " + das);
                                    if (das)
                                        datas.get(position).setSelected(false);
                                }
                            }
                        }
                    }
                    updateDevice();
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.txt_edit_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditDeviceActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, datas.get(position));
                intent.putExtras(b);
                ((Activity) mContext).startActivityForResult(intent, BLEConstant.REQUEST_DEVICE_EDIT);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, PalletActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, datas.get(position));
                intent.putExtras(b);
                ((Activity) mContext).startActivityForResult(intent, BLEConstant.REQUEST_COLOR_PALETTE);
            }
        });

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
                //for (int i = 0; i < 5; i++) {
                ThreadPool.getInstance().addTask(new SendRunnable(finalDeviceAddress, data));
                try {
                    Thread.sleep(Constant.HANDLERDELAY / 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //  }
            }
        }).start();

        System.out.println(" centerfragment changePassword deviceAddress: " + deviceAddress + "; data:  " + data);
        sp.edit().putString(deviceAddress, password).apply();
    }


    private void updateDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {}.getType();
        String json = gson.toJson(datas, type);
        AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, json);
    }

    private class ViewHolder {
        ImageView iv_device_icon, iv_on_off;
        TextView txt_edit_device, txt_device_name;
        LinearLayout ll_Connected, ll_notConnected;
    }
}
