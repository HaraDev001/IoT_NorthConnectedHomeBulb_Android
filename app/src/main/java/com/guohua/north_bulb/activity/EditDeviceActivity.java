package com.guohua.north_bulb.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.adapter.ImageAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class EditDeviceActivity extends Activity implements View.OnClickListener {

    private String TAG = "EditDeviceActivity";
    EditText ed_add_device_name;
    RelativeLayout rl_device_save;
    ImageView iv_add_device;
    Button btn_DeleteDevice;
    int selectedImage = -1;
    Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
        getintent();
        initView();

    }

    private void getintent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            device = new Device();
            device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }

    }

    private void initView() {

        rl_device_save = (RelativeLayout) findViewById(R.id.rl_device_save);
        ed_add_device_name = (EditText) findViewById(R.id.ed_add_device_name);
        iv_add_device = (ImageView) findViewById(R.id.iv_add_device);
        btn_DeleteDevice = (Button) findViewById(R.id.btn_DeleteDevice);

        rl_device_save.setOnClickListener(this);
        iv_add_device.setOnClickListener(this);
        btn_DeleteDevice.setOnClickListener(this);

        if (device.getName() != null) {
            ed_add_device_name.setText(device.getName());
            iv_add_device.setImageResource(device.getDeviceIcon());
            selectedImage = device.getDeviceIcon();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_device_save:
                if (TextUtils.isEmpty(ed_add_device_name.getText().toString().trim()) && ed_add_device_name.getText().toString().trim().equals("")) {
                    ed_add_device_name.setError("Device name is required field !");
                } else {
                    if (selectedImage == -1) {
                        device.setDeviceIcon(R.drawable.ic_add_device);
                    } else {
                        device.setDeviceIcon(selectedImage);
                    }

                    device.setName(ed_add_device_name.getText().toString().trim());

                    Gson gson = new Gson();
                    Type typeGroup = new TypeToken<List<Group>>() {
                    }.getType();

                    String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");
                    if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {
                        ArrayList<Group> newgroupList = new ArrayList<>();
                        ArrayList<Group> groupList = gson.fromJson(Grouplist, typeGroup);
                        for (int i = 0; i < groupList.size(); i++) {
                            ArrayList<Device> devices = groupList.get(i).getDevices();
                            ArrayList<Device> newdevices = new ArrayList<>();
                            for (int j = 0; j < devices.size(); j++) {
                                if (devices.get(j).getDeviceAddress().equalsIgnoreCase(device.getDeviceAddress()) && devices.get(j).getDeviceName().equalsIgnoreCase(device.getDeviceName())) {
                                    newdevices.add(device);
                                } else {
                                    newdevices.add(devices.get(j));
                                }
                            }
                            groupList.get(i).setDevices(newdevices);
                            newgroupList.add(groupList.get(i));
                        }
                        String jsonGROUP = gson.toJson(newgroupList, typeGroup);
                        AppContext.preferencePutString(BLECodeUtils.GROUP_LIST, jsonGROUP);
                    }

                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
                    intent.putExtras(b);
                    setResult(BLEConstant.RESULT_DEVICE_EDIT, intent);
                    finish();
                }

                break;
            case R.id.iv_add_device:
                final Dialog dialog = new Dialog(EditDeviceActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_icon_select);
                GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
                gridView.setAdapter(new ImageAdapter(EditDeviceActivity.this));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TypedArray imageIDs = getResources().obtainTypedArray(R.array.appliancesIcon);
                        iv_add_device.setImageResource(imageIDs.getResourceId(position, -1));
                        selectedImage = imageIDs.getResourceId(position, -1);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

            case R.id.btn_DeleteDevice:
                Gson gson = new Gson();
                Type type = new TypeToken<List<Device>>() {
                }.getType();
                ArrayList<Device> deviceList = new ArrayList<>();
                ArrayList<Device> newDeviceList = new ArrayList<>();
                String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");

                if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {
                    deviceList = gson.fromJson(Devicelist, type);
                    for (int i = 0; i < deviceList.size(); i++) {
                        if (deviceList.get(i).getDeviceAddress().equalsIgnoreCase(device.getDeviceAddress())
                                && deviceList.get(i).getDeviceName().equalsIgnoreCase(device.getDeviceName())) {
                            AppContext.getInstance().disonnect(device.getDeviceAddress(), true);
                        } else {
//                            newDeviceList.add(deviceList.get(i));
                        }
                    }
                    String json = gson.toJson(newDeviceList, type);
                    AppContext.preferencePutString(BLECodeUtils.DEVICE_LIST, json);
                }

                Type typeGroup = new TypeToken<List<Group>>() {}.getType();
                String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");
                if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {
                    ArrayList<Group> newgroupList = new ArrayList<>();
                    ArrayList<Group> groupList = gson.fromJson(Grouplist, typeGroup);
                    for (int i = 0; i < groupList.size(); i++) {
                        ArrayList<Device> devices = groupList.get(i).getDevices();
                        ArrayList<Device> newdevices = new ArrayList<>();
                        for (int j = 0; j < devices.size(); j++) {
                            if (devices.get(j).getDeviceAddress().equalsIgnoreCase(device.getDeviceAddress())
                                    && devices.get(j).getDeviceName().equalsIgnoreCase(device.getDeviceName())) {
                            } else {
//                                newdevices.add(devices.get(j));
                            }
                        }
                        if (newdevices.size() > 0) {
                            groupList.get(i).setDevices(newdevices);
//                            newgroupList.add(groupList.get(i));
                        }
                    }
                    String jsonGROUP = gson.toJson(newgroupList, typeGroup);
                    AppContext.preferencePutString(BLECodeUtils.GROUP_LIST, jsonGROUP);
                }

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
                intent.putExtras(b);
                setResult(BLEConstant.RESULT_DEVICE_DELETE, intent);
                finish();

                break;
        }
    }

    public void back(View view) {
        this.finish();
    }
}
