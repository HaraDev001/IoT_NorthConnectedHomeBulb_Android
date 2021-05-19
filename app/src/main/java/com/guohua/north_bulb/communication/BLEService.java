package com.guohua.north_bulb.communication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BLEService extends Service {
    public static final String TAG = BLEService.class.getSimpleName();
    private BLEService mContext;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final boolean AUTO_CONNECT = true;
    private static final boolean NOTIFICATION_ENABLED = true;
    private HashMap<String, BLEDevice> mBLEDevices;

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        suiside();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return Service.START_STICKY;
    }


    private void init() {
        if (!initBluetooth()) {
            stopSelf();
        }

        mContext = this;
        mBLEDevices = new HashMap<>();
    }


    private void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }


    private boolean initBluetooth() {
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }

        return true;
    }

    private void suiside() {
        disconnectAll();
        try {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter = null;
            }
        } catch (Exception e) {
            toast("Turn off Bluetooth failure, please shut down manually");
        }
    }


    public void close(BluetoothGatt mBluetoothGatt) {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
    }

    public boolean isConnected(String deviceAddress) {
        if (deviceAddress == null || TextUtils.equals("", deviceAddress)) {
            return false;
        }

        BLEDevice mDevice = mBLEDevices.get(deviceAddress);
        if (mDevice != null) {
            int state = mDevice.state;
            if (state == BLEDevice.STATE_CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null || TextUtils.equals(address, "")) {
            return false;
        }

        boolean isHave = mBLEDevices.containsKey(address);
        if (isHave) {
            BLEDevice mDevice = mBLEDevices.get(address);
            if (mDevice != null) {
                int state = mDevice.state;
                BluetoothGatt mGatt = mDevice.gatt;
                if (mGatt != null && state == BLEDevice.STATE_DISCONNECTED) {
                    if (mGatt.connect()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }

        BluetoothGatt mGatt = device.connectGatt(this, AUTO_CONNECT, mGattCallback);
        mBLEDevices.put(address, new BLEDevice(address, mGatt));

        return true;
    }


    public void disconnect(String address, boolean isRemove) {
        if (mBluetoothAdapter == null || address == null || TextUtils.equals(address, "")) {
            return;
        }
        if (!mBLEDevices.containsKey(address)) {
            return;
        }
        BLEDevice mDevice = mBLEDevices.get(address);
        if (mDevice != null) {
            BluetoothGatt mGatt = mDevice.gatt;
            int state = mDevice.state;
            if (mGatt != null && state == BLEDevice.STATE_CONNECTED) {
                mGatt.disconnect();
            }
        }
        if (isRemove) {
            close(mDevice.gatt);
            mBLEDevices.remove(address);
        }
    }


    public void disconnectAll() {
        Set<Map.Entry<String, BLEDevice>> mEnterySet = mBLEDevices.entrySet();
        for (Map.Entry<String, BLEDevice> entry : mEnterySet) {
            BLEDevice mDevice = entry.getValue();
            disconnect(mDevice.address, false);
            close(mDevice.gatt);
        }

        mBLEDevices.clear();
    }

    /**
     * 向外发送收到的数据广播
     *
     * @param rcv
     */
    private void sendDataBroadcast(String rcv, String address) {
        if (rcv == null || TextUtils.equals(rcv, ""))
            return;
        Intent intent = new Intent();
        intent.putExtra(BLEConstant.EXTRA_RECEIVED_DATA, rcv);
        intent.putExtra(BLEConstant.EXTRA_DEVICE_ADDRESS, address);
        if (rcv.startsWith("T:") || rcv.startsWith("t:")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_TEMPERATURE);
        } else if (rcv.startsWith("V:") || rcv.startsWith("v:")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_VOLTAGE);
        } else if (rcv.startsWith("VER:") || rcv.startsWith("ver:")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_VERSION);
        }
        sendBroadcast(intent);
    }

    /**
     * 向外发送状态广播
     *
     * @param action
     * @param address
     */
    private void sendStateBroadcast(String action, String address) {
        if (action == null || TextUtils.equals(action, ""))
            return;
        Intent intent = new Intent();
        intent.putExtra(BLEConstant.EXTRA_DEVICE_ADDRESS, address);
        intent.setAction(action);
        sendBroadcast(intent);
    }

    /**
     * GATT通信回調函數
     */
    @SuppressLint("NewApi")
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            // super.onCharacteristicChanged(gatt, characteristic);
            //硬件传来的数据从这里读取
            String deviceAddress = gatt.getDevice().getAddress();
            String rcv = new String(characteristic.getValue());
            sendDataBroadcast(rcv, deviceAddress);
            System.out.println("我收到了" + deviceAddress + "的数据:" + rcv);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicRead(gatt, characteristic, status);
            System.out.println("onCharacteristicRead()");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            //super.onCharacteristicWrite(gatt, characteristic, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                String deviceAddress = gatt.getDevice().getAddress();
                gatt.disconnect();
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            String deviceAddress = gatt.getDevice().getAddress();
            if (!mBLEDevices.containsKey(deviceAddress)) {
                return;
            }
            BLEDevice mDevice = mBLEDevices.get(deviceAddress);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                BluetoothGatt mGatt = mDevice.gatt;
                if (mGatt != null)
                    mGatt.discoverServices();
                mDevice.state = BLEDevice.STATE_CONNECTED;
                System.out.println("state connected");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mDevice.state = BLEDevice.STATE_DISCONNECTED;
                System.out.println("state disconnected");
                sendStateBroadcast(BLEConstant.ACTION_BLE_DISCONNECTED, deviceAddress);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorRead(gatt, descriptor, status);
            System.out.println("onDescriptorRead()");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorWrite(gatt, descriptor, status);
            System.out.println("onDescriptorWrite()");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // TODO Auto-generated method stub
            super.onReadRemoteRssi(gatt, rssi, status);
            System.out.println("onReadRemoteRssi()");

        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onReliableWriteCompleted(gatt, status);
            System.out.println("onReliableWriteCompleted()");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            String deviceAddress = gatt.getDevice().getAddress();
            if (!mBLEDevices.containsKey(deviceAddress)) {
                gatt.disconnect();
                return;
            }
            BLEDevice mDevice = mBLEDevices.get(deviceAddress);
            BluetoothGatt mGatt = mDevice.gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattCharacteristic mCharacteristic = getCharacteristic(gatt, SampleGattAttributes.UUID_SERVICE, SampleGattAttributes.UUID_CHARACTERISTIC);
                if (mCharacteristic == null) {
                    return;
                }
                mDevice.characteristic = mCharacteristic;
                System.out.println("find services");
                System.out.println("state connected");
                String passport = (Constant.DEFAULT_PASSWORD_HEAD + sp.getString(deviceAddress, CodeUtils.password));
                System.out.println(" BLEService onServicesDiscovered deviceAddress: " + deviceAddress + "; passport:  " + passport);
                writeToBLE(deviceAddress, passport.getBytes());
                sendStateBroadcast(BLEConstant.ACTION_BLE_CONNECTED, deviceAddress);
                setCharacteristicNotification(gatt, mCharacteristic, NOTIFICATION_ENABLED);
            } else {
                gatt.disconnect();
            }
        }

    };

    private BluetoothGattService getService(BluetoothGatt nGatt, UUID nUuid) {
        return nGatt.getService(nUuid);
    }

    private BluetoothGattCharacteristic getCharacteristic(BluetoothGatt nGatt, UUID nServiceUuid, UUID nCharacteristicUuid) {
        BluetoothGattService nService = nGatt.getService(nServiceUuid);
        return nService.getCharacteristic(nCharacteristicUuid);
    }


    private void setCharacteristicNotification(BluetoothGatt mBluetoothGatt,
                                               BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothGatt == null || characteristic == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic
                .getDescriptor(SampleGattAttributes.UUID_DESCRIPTOR);
        if (descriptor != null) {
            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }


    private synchronized boolean writeToBLE(String address, byte[] value) {

        if (mBluetoothAdapter == null || address == null || TextUtils.equals(address, "")) {
            return false;
        }
        if (!mBLEDevices.containsKey(address)) {
            return false;
        }

        BLEDevice mDevice = mBLEDevices.get(address);
        if (mDevice.state != BLEDevice.STATE_CONNECTED) {
            return false;
        }

        BluetoothGatt mGatt = mDevice.gatt;
        BluetoothGattCharacteristic mCharacteristic = mDevice.characteristic;

        if (mGatt == null || mCharacteristic == null) {
            return false;
        }

        mCharacteristic.setValue(value);

        boolean isSucc = false;
        isSucc = mGatt.writeCharacteristic(mCharacteristic);

        return isSucc;
    }

    public boolean send(String deviceAddress, byte[] data) {
        return writeToBLE(deviceAddress, data);
    }


}
