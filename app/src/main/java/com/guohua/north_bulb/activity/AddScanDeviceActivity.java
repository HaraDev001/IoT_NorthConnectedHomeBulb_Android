package com.guohua.north_bulb.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.north_bulb.R;
import com.guohua.north_bulb.adapter.ImageAdapter;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.communication.BLEConstant;

import java.util.ArrayList;


public class AddScanDeviceActivity extends Activity implements View.OnClickListener {

    EditText ed_add_device_name;
    RelativeLayout rl_device_save;
    ImageView iv_add_device;
    int selectedImage = -1;
    Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scan_device);
        getintent();
        initView();

    }

    private void getintent() {
        device = new Device();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
        }

    }

    private void initView() {

        rl_device_save = (RelativeLayout) findViewById(R.id.rl_device_save);
        ed_add_device_name = (EditText) findViewById(R.id.ed_add_device_name);
        iv_add_device = (ImageView) findViewById(R.id.iv_add_device);

        rl_device_save.setOnClickListener(this);
        iv_add_device.setOnClickListener(this);
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

                    device.setSelected(true);

                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
                    intent.putExtras(b);
                    setResult(BLEConstant.RESULT_DEVICE_ADD, intent);
                    finish();

                }

                break;
            case R.id.iv_add_device:
                final Dialog dialog = new Dialog(AddScanDeviceActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_icon_select);
                GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
                gridView.setAdapter(new ImageAdapter(AddScanDeviceActivity.this));
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
        }
    }

    public void back(View view) {
        this.finish();
    }
}
