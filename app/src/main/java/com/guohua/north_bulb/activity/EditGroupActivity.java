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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.adapter.DeviceListFroGroupAdapter;
import com.guohua.north_bulb.adapter.ImageAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.bean.Group;
import com.guohua.north_bulb.communication.BLEConstant;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class EditGroupActivity extends Activity implements View.OnClickListener {

    String TAG = "EditGroupActivity";
    EditText ed_add_Group_name;
    RelativeLayout rl_Group_save;
    ImageView iv_add_Group;
    Button btn_DeleteGroup;
    public ListView lstDevice;
    public DeviceListFroGroupAdapter deviceListAdapter;
    public ArrayList<Device> deviceList;
    int selectedImage = -1;
    int GroupPOS = -1;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        getintent();
        initView();

    }

    private void getintent() {

        group = new Group();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            group = (Group) bundle.getSerializable(BLEConstant.EXTRA_GROUP_LIST);
            GroupPOS = bundle.getInt(BLEConstant.EXTRA_GROUP_NUMBER);
        }
    }

    private void initView() {

        rl_Group_save = (RelativeLayout) findViewById(R.id.rl_Group_save);
        ed_add_Group_name = (EditText) findViewById(R.id.ed_add_Group_name);
        iv_add_Group = (ImageView) findViewById(R.id.iv_add_Group);
        btn_DeleteGroup = (Button) findViewById(R.id.btn_DeleteGroup);
        lstDevice = (ListView) findViewById(R.id.lstDevice);

        rl_Group_save.setOnClickListener(this);
        iv_add_Group.setOnClickListener(this);
        btn_DeleteGroup.setOnClickListener(this);

        deviceList = new ArrayList<>();
        deviceListAdapter = new DeviceListFroGroupAdapter(EditGroupActivity.this,TAG);
        lstDevice.setAdapter(deviceListAdapter);

        ArrayList<Device> ExisdeviceList = new ArrayList<>();
        if (group.getName() != null) {

            ed_add_Group_name.setText(group.getName());
            iv_add_Group.setImageResource(group.getGroupIcon());
            selectedImage = group.getGroupIcon();

            ExisdeviceList = group.getDevices();

        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {
        }.getType();

        String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");

        if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

            deviceList = gson.fromJson(Devicelist, type);

            for (int i = 0; i < deviceList.size(); i++) {

                Boolean isExist = false;
                for (int j = 0; j < ExisdeviceList.size(); j++) {

                    if (deviceList.get(i).getDeviceAddress().equalsIgnoreCase(ExisdeviceList.get(j).getDeviceAddress())) {
                        isExist = true;
                    }
                }
                if (isExist) {
                    deviceList.get(i).setSelected(true);
                } else {
                    deviceList.get(i).setSelected(false);
                }
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
                        Toast.makeText(EditGroupActivity.this, "Please select device !", Toast.LENGTH_SHORT).show();
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


                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Group>>() {
                    }.getType();

                    String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");

                    Log.e(TAG, "json " + Grouplist);
                    if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {

                        ArrayList<Group> groupList = gson.fromJson(Grouplist, type);
                        groupList.set(GroupPOS, group);

                        String json = gson.toJson(groupList, type);
                        AppContext.preferencePutString(BLECodeUtils.GROUP_LIST, json);

                    }

                    Intent intent = new Intent();
                    setResult(BLEConstant.RESULT_GROUP_EDIT, intent);
                    finish();

                }

                break;
            case R.id.iv_add_Group:
                final Dialog dialog = new Dialog(EditGroupActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_icon_select);
                GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
                gridView.setAdapter(new ImageAdapter(EditGroupActivity.this));
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

            case R.id.btn_DeleteGroup:

                Gson gson = new Gson();
                Type type = new TypeToken<List<Group>>() {
                }.getType();

                String Grouplist = AppContext.preferenceGetString(BLECodeUtils.GROUP_LIST, "");

                Log.e(TAG, "json " + Grouplist);
                if (!TextUtils.isEmpty(Grouplist) && !Grouplist.equals("")) {

                    ArrayList<Group> groupList = gson.fromJson(Grouplist, type);
                    groupList.remove(GroupPOS);

                    String json = gson.toJson(groupList, type);
                    AppContext.preferencePutString(BLECodeUtils.GROUP_LIST, json);

                }

                Intent intent = new Intent();
                setResult(BLEConstant.RESULT_GROUP_DELETE, intent);
                finish();

                break;
        }
    }

    public void back(View view) {
        this.finish();
    }
}
