package com.guohua.north_bulb.library;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.guohua.north_bulb.util.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Leo
 * @time 2016-02-02
 * @detail 蓝牙通信服务
 */
public class BluetoothSPPService extends Service {
    private MHandler mHandler;//用于与线程进行数据通信
    private BluetoothAdapter mAdapter;//蓝牙设置的管理
    private Map<String, ConnectThread> mConnectThreadMap;//所有的连接信息，以device address为key

    private BluetoothDevice mBluetoothDevice;//要连接的设备
    private ConnectThread mConnectThread;//连接线程临时变量

    public BluetoothSPPService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();//初始化
    }

    /**
     * 实现远程服务接口
     */
    private IBluetoothSPPService.Stub mService = new IBluetoothSPPService.Stub() {
        @Override
        public IBinder asBinder() {
            return super.asBinder();
        }

        @Override
        public boolean connect(String deviceAddress) throws RemoteException {
            return BluetoothSPPService.this.connect(deviceAddress);
        }

        @Override
        public boolean send(String deviceAddress, String message) throws RemoteException {
            return BluetoothSPPService.this.write(deviceAddress, message);
        }

        @Override
        public void disconnect(String deviceAddress) throws RemoteException {
            BluetoothSPPService.this.disconnect(deviceAddress);
        }

        @Override
        public boolean isConnected(String deviceAddress) throws RemoteException {
            return BluetoothSPPService.this.isConnected(deviceAddress);
        }

        @Override
        public void disconnectAll() throws RemoteException {
            BluetoothSPPService.this.disconnectAll();
        }

        @Override
        public boolean sendAll(String message) throws RemoteException {
            return BluetoothSPPService.this.sendAll(message);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };


    /**
     * 根据地址连接设备
     *
     * @param deviceAddress
     */
    private boolean connect(String deviceAddress) {
        //地址为空就退出 别烦我
        if (deviceAddress == null || TextUtils.equals("", deviceAddress)) {
            return false;
        }
        //不支持蓝牙或者蓝牙关闭就退出 别烦我
        if (mAdapter == null || !mAdapter.isEnabled()) {
            return false;
        }

        if (isConnected(deviceAddress)) {
            return true;//如果连接就不再连接
        }

        if (mAdapter.isDiscovering())
            mAdapter.cancelDiscovery();//连接前一家要取消扫描

        mConnectThread = mConnectThreadMap.get(deviceAddress);//查看设备是否存在
        //存在但是没有连接我就关了你
        if (mConnectThread != null) {
            mConnectThread.close();
            mConnectThread = null;
            mConnectThreadMap.remove(deviceAddress);
        }
        mBluetoothDevice = mAdapter.getRemoteDevice(deviceAddress);
        mConnectThread = new ConnectThread(mHandler, mBluetoothDevice);
        mConnectThreadMap.put(deviceAddress, mConnectThread);
        mConnectThread.start();
        return true;
    }

    /**
     * 向蓝牙设备写数据
     *
     * @param deviceAddress
     * @param message
     */
    private boolean write(String deviceAddress, String message) {
        //如果你没给我消息 就别来烦我
        if (message == null || TextUtils.equals("", message)) {
            return false;
        }
        if (deviceAddress == null || TextUtils.equals(deviceAddress, "")) {
            return false;
        }
        /*//你给我一个空的地址 我猜你是想让我向所有的设备写数据 那我就满足你
        if (deviceAddress == null || TextUtils.equals("", deviceAddress)) {
            //遍历所有的连接线程去写数据
            Set<Map.Entry<String, ConnectThread>> mEnterySet = mConnectThreadMap.entrySet();
            for (Map.Entry<String, ConnectThread> entry : mEnterySet) {
                mConnectThread = entry.getValue();
                if (mConnectThread != null) {
                    return mConnectThread.write(message);
                }
            }
            //写完就走 别再烦我
        }*/
        //哈哈 你给了我地址 最喜欢这样的了 我给查一下有没有 有的话就帮你捎个信 没有别怪我
        mConnectThread = mConnectThreadMap.get(deviceAddress);
        if (mConnectThread == null || !isConnected(deviceAddress)) {
            return false;
        }
        return mConnectThread.write(message);
    }

    /**
     * 根据设备地址进行断开连接
     *
     * @param deviceAddress
     */
    private void disconnect(String deviceAddress) {
        //你没给我地址 这是要作哪样 我就忍气吞声断开所有的设备 吼吼吼吼
        /*if (deviceAddress == null || TextUtils.equals("", deviceAddress)) {
            Set<Map.Entry<String, ConnectThread>> mEnterySet = mConnectThreadMap.entrySet();
            for (Map.Entry<String, ConnectThread> entry : mEnterySet) {
                mConnectThread = entry.getValue();
                if (mConnectThread != null) {
                    mConnectThread.close();
                }
            }
            //断开完我再清理
            mConnectThreadMap.clear();
            return;
        }*/
        //我就是不用else
        if (deviceAddress == null && TextUtils.equals("", deviceAddress)) {
            return;
        }
        mConnectThread = mConnectThreadMap.get(deviceAddress);
        if (mConnectThread != null) {
            mConnectThread.close();
            mConnectThreadMap.remove(deviceAddress);
        }
    }

    private void disconnectAll() {
        Set<Map.Entry<String, ConnectThread>> mEnterySet = mConnectThreadMap.entrySet();
        for (Map.Entry<String, ConnectThread> entry : mEnterySet) {
            mConnectThread = entry.getValue();
            if (mConnectThread != null) {
                mConnectThread.close();
            }
        }
        //断开完我再清理
        mConnectThreadMap.clear();
    }

    private boolean sendAll(String message) {
        //遍历所有的连接线程去写数据
        boolean flag = true;//标志位
        Set<Map.Entry<String, ConnectThread>> mEnterySet = mConnectThreadMap.entrySet();
        for (Map.Entry<String, ConnectThread> entry : mEnterySet) {
            mConnectThread = entry.getValue();
            if (mConnectThread != null) {
                if (!mConnectThread.write(message)) {
                    flag = false;
                }
            }
        }
        //写完就走 别再烦我
        return flag;
    }

    /**
     * 判断设备是否连接
     *
     * @param deviceAddress
     * @return
     */
    private boolean isConnected(String deviceAddress) {
        //你是敢给我空的地址我就敢给你false
        if (deviceAddress == null || TextUtils.equals("", deviceAddress)) {
            return false;
        }
        mConnectThread = mConnectThreadMap.get(deviceAddress);
        if (mConnectThread != null) {
            return mConnectThread.isConnected();
        }
        return false;
    }

    /**
     * 初始化数据
     */
    private void init() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new MHandler();
        mConnectThreadMap = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;//粘性打开死而复生
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mService;//传出接口
    }

    /**
     * 自杀函数
     * 我是隐藏的你敢要我死我就死给你看
     */
    private void sucide() {
        disconnectAll();
        mConnectThreadMap = null;
        mConnectThread = null;
        mBluetoothDevice = null;
        if (mAdapter != null) {
            mAdapter = null;
        }
        mHandler = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sucide();
    }

    private class MHandler extends Handler {
        public MHandler() {
            //super();
        }

        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case BluetoothConstant.WHAT_RECEIVED_DATA: {
                    System.out.println(msg.obj.toString());
                    handleReceivedMessage(msg.obj.toString());
                }
                break;
                case BluetoothConstant.WHAT_CONNECT_ERROR: {
                    sendConnectInfoBroadcast(what, null);
                }
                break;
                case BluetoothConstant.WHAT_CONNECT_SUCCESS: {
                    sendConnectInfoBroadcast(what, null);
                }
                break;
                default:
                    break;
            }
        }
    }

    private void handleReceivedMessage(String rcvMsg) {
        sendDataBroadcast(rcvMsg);
    }
//////////////////////////////////////////////还需修改///////////////////////////////////////////////

    /**
     * 发送接收到的数据的广播
     *
     * @param rcvMsg
     */
    private void sendDataBroadcast(String rcvMsg) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_STATUS_MESSAGE, rcvMsg);

        if (rcvMsg.contains("status:G")) {
            intent.setAction(Constant.ACTION_RECEIVED_STATUS);
            saveStatusData(rcvMsg);
        } else if (rcvMsg.contains("status:T")) {
            intent.setAction(Constant.ACTION_TEMPERATURE_STATUS);
        } else if (rcvMsg.contains("status:V")) {
            intent.setAction(Constant.ACTION_VOLTAGE_STATUS);
        } else if (rcvMsg.contains("status:")) {
            saveStatusData(rcvMsg);//这里有问题
            intent.setAction(Constant.ACTION_INIT_STATUS);
        } else if (rcvMsg.contains("blight_ver")) {
            intent.setAction(Constant.ACTION_FIRMWARE_VERSION);
        }
        sendBroadcast(intent);
    }

    private void sendConnectInfoBroadcast(int what, String errorMsg) {
        Intent intent = new Intent();
        if (errorMsg != null && !TextUtils.equals("", errorMsg)) {
            intent.putExtra(BluetoothConstant.KEY_ERROR_MESSAGE, errorMsg);
        }
        switch (what) {
            case BluetoothConstant.WHAT_CONNECT_ERROR:
                intent.setAction(BluetoothConstant.ACTION_CONNECT_ERROR);
                break;
            case BluetoothConstant.WHAT_CONNECT_SUCCESS:
                intent.setAction(BluetoothConstant.ACTION_CONNECT_SUCCESS);
                break;
        }
        sendBroadcast(intent);
    }

    /**
     * 连接成功后 硬件会发来灯的当前状态 把当前状态保存起来
     *
     * @param msg
     */
    private void saveStatusData(String msg) {
        String[] datas = msg.split(":");
        if (datas.length < 6) {
            return;
        }
        boolean isLightOn = false;//默认状态为关灯
        int alpha = 255;//默认颜色值为白
        int red = 255;
        int green = 255;
        int blue = 255;

        if (datas[1] != null && datas[1].contains("open")) {
            isLightOn = true;
        }
        if (datas[2] != null && datas[3] != null && datas[4] != null && datas[5] != null) {
            alpha = Integer.parseInt(datas[2].trim());
            red = Integer.parseInt(datas[3].trim());
            green = Integer.parseInt(datas[4].trim());
            blue = Integer.parseInt(datas[5].trim());
        }

        int color = Color.argb(alpha, red, green, blue);

        saveValues(isLightOn, color);
    }

    /**
     * 把当前数据保存到SharedPreference中
     */
    private void saveValues(boolean isLightOn, int color) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constant.KEY_DEVICE_SWITCH, isLightOn).apply();
        editor.putInt(Constant.KEY_DEVICE_COLOR, color).apply();
    }
//////////////////////////////////////////////还需修改///////////////////////////////////////////////
}