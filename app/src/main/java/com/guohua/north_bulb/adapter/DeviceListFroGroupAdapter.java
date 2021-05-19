package com.guohua.north_bulb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceListFroGroupAdapter extends BaseAdapter {
    String TAG = "DeviceListAdapter ";
    private ArrayList<Device> datas;
    private LayoutInflater mInflater;
    private Context mContext;
    String Tag = "";
    private HashMap<LinearLayout, CountDownTimer> counters;

    public DeviceListFroGroupAdapter(Context context, String Tag) {
        this.mContext = context;
        this.Tag = Tag;
        mInflater = LayoutInflater.from(context);
        datas = new ArrayList<Device>();
        this.counters = new HashMap<LinearLayout, CountDownTimer>();
    }

    public void addAllDevices(ArrayList<Device> data) {
        datas = data;
        notifyDataSetChanged();
    }

    public ArrayList<Device> getAllDevices() {
        return datas;
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

            convertView = mInflater.inflate(R.layout.row_item_device_list_for_group, null);

            viewHolder.iv_device_icon = (ImageView) convertView.findViewById(R.id.iv_device_icon);
            viewHolder.iv_on_off = (ImageView) convertView.findViewById(R.id.iv_on_off);
            viewHolder.txt_device_name = (TextView) convertView.findViewById(R.id.txt_device_name);
            viewHolder.ll_notConnected = (LinearLayout) convertView.findViewById(R.id.ll_notConnected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (datas.get(position).isSelected()) {
            viewHolder.iv_on_off.setImageResource(R.drawable.ic_check);
        } else {
            viewHolder.iv_on_off.setImageResource(0);
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

                if (Tag.equalsIgnoreCase("AddTimerActivity")) {

                    for (int i = 0; i < datas.size(); i++) {

                        if (i == position) {
                            datas.get(position).setSelected(true);
                        } else {
                            datas.get(i).setSelected(false);
                        }
                    }

                } else {
                    if (datas.get(position).isSelected()) {
                        datas.get(position).setSelected(false);
                    } else {
                        datas.get(position).setSelected(true);
                    }
                }
                notifyDataSetChanged();
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.iv_on_off.callOnClick();
            }
        });
        return convertView;
    }


    private class ViewHolder {
        ImageView iv_device_icon, iv_on_off;
        TextView txt_device_name;
        LinearLayout ll_notConnected;
    }
}
