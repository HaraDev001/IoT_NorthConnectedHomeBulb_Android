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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.EditGroupActivity;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.bean.TimerModel;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.net.SendRunnable;
import com.guohua.north_bulb.net.ThreadPool;
import com.guohua.north_bulb.util.BLECodeUtils;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimerListAdapter extends BaseAdapter {
    String TAG = "GroupListAdapter ";
    private ArrayList<TimerModel> datas;
    private LayoutInflater mInflater;
    private Context mContext;
    private HashMap<TextView, CountDownTimer> counters;


    public TimerListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        datas = new ArrayList<TimerModel>();
        this.counters = new HashMap<TextView, CountDownTimer>();
    }

    public void addTimer(TimerModel data) {

        datas.add(data);
        Log.e(TAG, "data " + data.toString());
        Log.e(TAG, "data1 " + datas.size());
        Log.e(TAG, "data2 " + datas);

        notifyDataSetChanged();
    }

    public void removeTimer(int pos) {

        datas.remove(pos);
        Log.e(TAG, "Remove devovice pos " + pos);
        notifyDataSetChanged();
    }

    public void addAllTimer(ArrayList<TimerModel> data) {
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

            convertView = mInflater.inflate(R.layout.row_item_timer_list, null);

            viewHolder.iv_Device_icon = (ImageView) convertView.findViewById(R.id.iv_Device_icon);
            viewHolder.iv_on_off = (ImageView) convertView.findViewById(R.id.iv_on_off);
            viewHolder.txt_DeviceName = (TextView) convertView.findViewById(R.id.txt_DeviceName);
            viewHolder.txt_on_off_Time = (TextView) convertView.findViewById(R.id.txt_on_off_Time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt_DeviceName.setText(datas.get(position).getDevices().getName());
        viewHolder.iv_Device_icon.setImageResource(datas.get(position).getDevices().getDeviceIcon());
        if (datas.get(position).getStatus()) {
            viewHolder.iv_on_off.setImageResource(R.drawable.ic_on);
        } else {
            viewHolder.iv_on_off.setImageResource(R.drawable.ic_off);
        }
        final TextView tv = viewHolder.txt_on_off_Time;

        CountDownTimer cdt = counters.get(viewHolder.txt_on_off_Time);
        if (cdt != null) {
            cdt.cancel();
            cdt = null;
        }

        Date date = datas.get(position).getDate();
        long currentDate = Calendar.getInstance().getTime().getTime();
        long limitDate = date.getTime();
        long difference = limitDate - currentDate;
        Log.e("test", "currentDate " + currentDate);
        Log.e("test", "limitDate " + limitDate);
        Log.e("test", "difference " + difference);

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
                tv.setText(sDate.trim());
            }

            @Override
            public void onFinish() {

                try {
                    tv.setText((datas.get(position).getStatus() ? "On" : "Off"));
                    Log.e("test", "pos " + position);

                    Gson gson = new Gson();
                    Type typeDevice = new TypeToken<List<Device>>() {
                    }.getType();
                    String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");
                    if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

                        ArrayList<Device> deviceList = gson.fromJson(Devicelist, typeDevice);
                        for (int i = 0; i < deviceList.size(); i++) {
                            if (datas.get(position).getDevices() != null && deviceList.get(i).getDeviceAddress().equalsIgnoreCase(datas.get(position).getDevices().getDeviceAddress())
                                    && deviceList.get(i).getDeviceName().equalsIgnoreCase(datas.get(position).getDevices().getDeviceName())) {
                                deviceList.get(i).setSelected(datas.get(position).getStatus());
                            }
                        }

                        String json = gson.toJson(deviceList, typeDevice);
                        AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, json);
                    }

                    Type typeTimerModel = new TypeToken<List<TimerModel>>() {
                    }.getType();

                    String timerString = AppContext.preferenceGetString(BLECodeUtils.TIMER_LIST, "");
                    if (!TextUtils.isEmpty(timerString) && !timerString.equals("")) {

                        ArrayList<TimerModel> timerModels = gson.fromJson(timerString, typeTimerModel);
                        timerModels.remove(position);
                        datas.remove(position);

                        String json = gson.toJson(datas, typeTimerModel);
                        AppContext.preferencePutString(BLECodeUtils.TIMER_LIST, json);

                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        counters.put(tv, cdt);
        cdt.start();

        return convertView;
    }

    public void cancelAllTimers() {
//        Set<Map.Entry<TextView, CountDownTimer>> s = counters.entrySet();
//        Iterator it = s.iterator();
//        while (it.hasNext()) {
//            try {
//                Map.Entry pairs = (Map.Entry) it.next();
//                CountDownTimer cdt = (CountDownTimer) pairs.getValue();
//
//                cdt.cancel();
//                cdt = null;
//            } catch (Exception e) {
//            }
//        }
//
//        it = null;
//        s = null;
//        counters.clear();
    }

    private class ViewHolder {
        ImageView iv_Device_icon, iv_on_off;
        TextView txt_DeviceName, txt_on_off_Time;
    }
}
