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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.adapter.DeviceListAdapter;
import com.guohua.north_bulb.adapter.DeviceListFroGroupAdapter;
import com.guohua.north_bulb.adapter.ImageAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.interfaceListner.GroupResult;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class AddGroupActivity extends Activity implements View.OnClickListener {

    String TAG = "AddGroupActivity";
    EditText ed_add_Group_name;
    RelativeLayout rl_Group_save;
    ImageView iv_add_Group;
    public ListView lstDevice;
    public DeviceListFroGroupAdapter deviceListAdapter;
    public ArrayList<Device> deviceList;
    int selectedImage = -1;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getintent();
        initView();

    }

    private void getintent() {
        group = new Group();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            group = (Group) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }
    }

    private void initView() {

        rl_Group_save = (RelativeLayout) findViewById(R.id.rl_Group_save);
        ed_add_Group_name = (EditText) findViewById(R.id.ed_add_Group_name);
        iv_add_Group = (ImageView) findViewById(R.id.iv_add_Group);
        lstDevice = (ListView) findViewById(R.id.lstDevice);

        rl_Group_save.setOnClickListener(this);
        iv_add_Group.setOnClickListener(this);

        deviceList = new ArrayList<>();
        deviceListAdapter = new DeviceListFroGroupAdapter(AddGroupActivity.this,TAG);
        lstDevice.setAdapter(deviceListAdapter);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {
        }.getType();

        String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");

        if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

            deviceList = gson.fromJson(Devicelist, type);
            for (int i = 0; i < deviceList.size(); i++) {
                deviceList.get(i).setSelected(false);
            }
            deviceListAdapter.addAllDevices(deviceList);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rl_Group_save:

                if (TextUtils.isEmpty(ed_add_Group_name.getText().toString().trim()) && ed_add_Group_name.getText().toString().trim().equals("")) {
                    ed_add_Group_name.setError("Group name is required field !");

                } else {


                    deviceList = deviceListAdapter.getAllDevices();
                    ArrayList<Device> devices = new ArrayList<>();
                    boolean anySelectDevice = false;
                    for (int i = 0; i < deviceList.size(); i++) {

                        if (deviceList.get(i).isSelected()) {
                            anySelectDevice = true;
                            devices.add(deviceList.get(i));
                        }
                    }

                    if (!anySelectDevice) {
                        Toast.makeText(AddGroupActivity.this, "Please select device !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedImage == -1) {
                        group.setGroupIcon(R.drawable.ic_add_device);
                    } else {
                        group.setGroupIcon(selectedImage);
                    }

                    group.setName(ed_add_Group_name.getText().toString().trim());

                    group.setSelected(true);
                    group.setDevices(devices);

                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    b.putSerializable(BLEConstant.EXTRA_GROUP_LIST, group);
                    intent.putExtras(b);
                    setResult(BLEConstant.RESULT_GROUP_ADD, intent);
                    finish();

                }

                break;
            case R.id.iv_add_Group:
                final Dialog dialog = new Dialog(AddGroupActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_icon_select);
                GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
                gridView.setAdapter(new ImageAdapter(AddGroupActivity.this));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TypedArray imageIDs = getResources().obtainTypedArray(R.array.appliancesIcon);
                        iv_add_Group.setImageResource(imageIDs.getResourceId(position, -1));
                        selectedImage = imageIDs.getResourceId(position, -1);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    public void back(View view) {
        this.finish();
    }
}
