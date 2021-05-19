package com.guohua.north_bulb.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.R;
import com.guohua.north_bulb.activity.AddScanDeviceActivity;
import com.guohua.north_bulb.bean.Device;
import com.guohua.north_bulb.util.BLECodeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class BLEActivity extends Activity {

    String TAG = "BLEActivity";
    private static Context mContext;

    private BluetoothAdapter mBluetoothAdapter = null;

    private Handler mHandler = null;
    private boolean mScanning = false;

    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView mListView = null;
    private BLEAdapter mAdapter = null;

    private Button button_connect;
    TextView txt_Search_status;
    Boolean isFirst = true;
    private ArrayList<String> selectedScanDeviceList = new ArrayList<String>();
    private ArrayList<Device> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        if (!checkBluetooth()) {
            finish();
        }
        init();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e(TAG, "onResume ");
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "mBluetoothAdapter ");
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        mAdapter = new BLEAdapter(mContext);
        mListView.setAdapter(mAdapter);
        txt_Search_status.setText(getResources().getString(R.string.status_Searching));

        doDiscovery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        //mAdapter.clear();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        suiside();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else if (requestCode == BLEConstant.REQUEST_DEVICE_ADD && resultCode == BLEConstant.RESULT_DEVICE_ADD) {
            Intent intent = new Intent();
            Device device = new Device();
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                device = (Device) bundle.getSerializable(BLEConstant.EXTRA_DEVICE_LIST);
            }
            Log.e(TAG, "device  = " + device);
            Bundle b = new Bundle();
            b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device);
            intent.putExtras(b);
            setResult(BLEConstant.RESULT_DEVICE_ADD, intent);
            finish();
        }
    }


    private boolean checkBluetooth() {
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast("ble not supported");
            return false;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            toast("bluetooth not supported");
            return false;
        }

        return true;
    }


    private void init() {
        mContext = this;
        mHandler = new Handler();
        findViewsByIds();
    }

    private void findViewsByIds() {
        button_connect = (Button) findViewById(R.id.button_connect);
        txt_Search_status = (TextView) findViewById(R.id.txt_Search_status);

        mListView = (ListView) findViewById(R.id.lv_device_ble);
        mListView.setOnItemClickListener(mItemClickListener);

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearSelectedScanDeviceList();
                addSelectedScanDeviceList();
                Intent intent = new Intent();
                intent.putExtra(BLEConstant.EXTRA_DEVICE_LIST, selectedScanDeviceList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {
        }.getType();

        String Devicelist = AppContext.preferenceGetString(BLECodeUtils.DEVICE_LIST, "");
        deviceList = new ArrayList<>();
        Log.e(TAG, "json " + Devicelist);
        if (!TextUtils.isEmpty(Devicelist) && !Devicelist.equals("")) {

            deviceList = gson.fromJson(Devicelist, type);
            Log.e(TAG, "fromJson " + deviceList);

        }

    }

    private void addSelectedScanDeviceList() {
        BluetoothDevice device;
        String name;
        String address;
        System.out.println("selectedScanDeviceList.get(i):  mAdapter.getCount()  " + mAdapter.getCount());
        for (int i = 0; i < mAdapter.getCount(); i++) {
            // if (mAdapter.getMLeDevicesIsselected().get(i)) {
            device = mAdapter.getDevice(i);
            name = device.getName().trim();
            address = device.getAddress().trim();
            selectedScanDeviceList.add(address + ";" + name);
            System.out.println("selectedScanDeviceList.get(i): " + address + ";" + name);
            //}
        }
        System.out.println("selectedScanDeviceList.get(i):  selectedScanDeviceList.size()  " + selectedScanDeviceList.size());
    }

    private void clearSelectedScanDeviceList() {
        selectedScanDeviceList.clear();
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            try {
                //  setResultToConnect(position);
                selectedScanDeviceList = new ArrayList<>();
                BluetoothDevice device;
                String name = "";
                String address;
                device = mAdapter.getDevice(position);
                try {
                    name = device.getName().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                address = device.getAddress().trim();

                Device device1 = new Device();
                device1.setDeviceAddress(address);
                device1.setDeviceName(name);

                Log.e(TAG, "selectedScanDeviceList: " + address + ";" + name);

                Intent intent = new Intent(mContext, AddScanDeviceActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(BLEConstant.EXTRA_DEVICE_LIST, device1);
                intent.putExtras(b);
                startActivityForResult(intent, BLEConstant.REQUEST_DEVICE_ADD);
                isFirst = false;
            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }
        }

    };

    private void setResultToConnect(int position) {
        scanLeDevice(false);

        BluetoothDevice device = mAdapter.getDevice(position);
        String name = device.getName().trim();
        String address = device.getAddress().trim();

        selectedScanDeviceList.add(address + ";" + name);

        System.out.println("selectedScanDeviceList.get(i) ========selectedScanDeviceList.======== " + selectedScanDeviceList.size());
        for (int i = 0; i < selectedScanDeviceList.size(); i++) {
            System.out.println(selectedScanDeviceList.get(i));
        }
        System.out.println("selectedScanDeviceList.get(i) ================");
        // mAdapter.setDeviceCheckState(position, !mAdapter.getDeviceCheckState(position));
        mAdapter.notifyDataSetChanged();

    }


    private void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }


    private void scanLeDevice(final boolean enable) {
        Log.e(TAG, "scanLeDevice enable :: " + enable);
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }


    private void doDiscovery() {

        Log.e(TAG, "mScanning :: " + mScanning);
        if (mScanning) {
            scanLeDevice(false);
        } else {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            scanLeDevice(true);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null && device.getName().contains("M_light")) {
                        Boolean isContain = false;
                        for (int i = 0; i < deviceList.size(); i++) {
                            if (device.getAddress().equalsIgnoreCase(deviceList.get(i).getDeviceAddress())) {
                                isContain = true;
                            }
                        }

                        if (!isContain) {
                            mAdapter.addDevice(device);
                            mAdapter.notifyDataSetChanged();
                            txt_Search_status.setText(getResources().getString(R.string.status_Please_select_the_device_to_add));
                        }
                    }
                }
            });
        }
    };

    private void suiside() {
        scanLeDevice(false);
    }

    public void back(View view) {
        this.finish();
    }
}
