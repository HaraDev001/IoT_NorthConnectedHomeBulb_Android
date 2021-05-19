package com.guohua.north_bulb.fragmentNew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.MenuActivity;
import com.guohua.north_bulb.adapter.DeviceListAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEActivity;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.interfaceListner.OnActivityResult;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DeviceFragment extends Fragment implements View.OnClickListener, OnActivityResult {

    private static final String TAG = DeviceFragment.class.getName();
    public ListView lstDevice;
    public View mView;
    Context mContext;
    public DeviceListAdapter deviceListAdapter;
    public ArrayList<Device> deviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device, container, false);
        mContext = getActivity();
        init();

        return mView;
    }

    private void init() {
        Button btnAdd = (Button) mView.findViewById(R.id.btnAdd);
        lstDevice = (ListView) mView.findViewById(R.id.lstDevice);

        MenuActivity.setInitListener(DeviceFragment.this);

        btnAdd.setOnClickListener(this);
        deviceList = new ArrayList<>();
        deviceListAdapter = new DeviceListAdapter(mContext);
        lstDevice.setAdapter(deviceListAdapter);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {
        }.getType();

        String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");

        Log.e(TAG, "json " + Devicelist);
        if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

            deviceList = gson.fromJson(Devicelist, type);
            Log.e(TAG, "fromJson " + deviceList);
            for (int i = 0; i < deviceList.size(); i++) {
                AppContext.getInstance().addDevice(deviceList.get(i));
            }
            deviceListAdapter.addAllDevices(deviceList);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnAdd:
                Intent intent = new Intent(mContext, BLEActivity.class);
                startActivityForResult(intent, BLEConstant.REQUEST_DEVICE_SCAN);
                break;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        if (requestCode == BLEConstant.REQUEST_DEVICE_SCAN && resultCode == BLEConstant.RESULT_DEVICE_ADD) {

            Device device = new Device();
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
            }

            AppContext.getInstance().addDevice(device);
            deviceListAdapter.addDevices(device);
            deviceListAdapter.notifyDataSetChanged();
            Log.e(TAG, "Device " + deviceListAdapter.getCount());

            if (deviceList.contains(device)) {
                Log.e(TAG, "Device same");
            } else {
                deviceList.add(device);
                Log.e(TAG, "Device not same");
            }

            Gson gson = new Gson();
            Type type = new TypeToken<List<Device>>() {
            }.getType();
            String json = gson.toJson(deviceList, type);

            AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, json);

            Log.e(TAG, "json " + json);
            List<Device> fromJson = gson.fromJson(json, type);
            Log.e(TAG, "fromJson " + fromJson);
            Log.e(TAG, "Device " + device.toString());


        } else if (requestCode == BLEConstant.REQUEST_DEVICE_EDIT && resultCode == BLEConstant.RESULT_DEVICE_DELETE) {

            Device device = new Device();
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
            }
            Log.e(TAG, "delete Device " + device.toString());
            for (int i = 0; i < deviceList.size(); i++) {
                if (device != null && deviceList.get(i).getDeviceAddress().equalsIgnoreCase(device.getDeviceAddress())
                        && deviceList.get(i).getDeviceName().equalsIgnoreCase(device.getDeviceName())) {
                    deviceListAdapter.removeDevice(i);
                }
            }
        } else if (requestCode == BLEConstant.REQUEST_DEVICE_EDIT && resultCode == BLEConstant.RESULT_DEVICE_EDIT) {

            Device device = new Device();
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
            }

            for (int i = 0; i < deviceList.size(); i++) {
                if (device != null && deviceList.get(i).getDeviceAddress().equalsIgnoreCase(device.getDeviceAddress())
                        && deviceList.get(i).getDeviceName().equalsIgnoreCase(device.getDeviceName())) {
                    deviceListAdapter.updateDevices(device, i);
                }
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<Device>>() {
            }.getType();
            String json = gson.toJson(deviceList, type);

            AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, json);

        } else if (requestCode == BLEConstant.REQUEST_COLOR_PALETTE && resultCode == BLEConstant.RESULT_COLOR_PALETTE) {

            Device device = new Device();
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
            }

            for (int i = 0; i < deviceList.size(); i++) {
                if (device != null && deviceList.get(i).getDeviceAddress().equalsIgnoreCase(device.getDeviceAddress())
                        && deviceList.get(i).getDeviceName().equalsIgnoreCase(device.getDeviceName())) {
                    deviceListAdapter.updateDevices(device, i);
                }
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<Device>>() {
            }.getType();
            String json = gson.toJson(deviceList, type);

            AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, json);
        }
    }

    @Override
    public void onactivityresult(int requestCode, int resultCode, Intent data) {

        onActivityResult(requestCode, resultCode, data);

    }
}
