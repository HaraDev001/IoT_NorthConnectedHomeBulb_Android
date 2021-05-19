package com.guohua.north_bulb.library;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Leo
 * @time 2016-04-20
 * @describe 负责与蓝牙设备进行连接 然后抛出通信的线程
 */
public class ConnectThread extends Thread {
    private BluetoothSocket mBluetoothSocket;//通信的Socket
    private CommunicateThread mCommunicateThread;//通信数据交互线程
    private Handler mHandler;//向上传递数据

    public ConnectThread(Handler mHandler, BluetoothDevice mBluetoothDevice) {
        //super();
        this.mHandler = mHandler;
        BluetoothSocket tmp = null;//临时变量

        try {
            Method m = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(mBluetoothDevice, 1);
        } catch (Exception e) {
            System.out.print(e.getMessage());
            System.out.println("Error-ConnectThread()");
        }

        mBluetoothSocket = tmp;//引用
    }

    @Override
    public void run() {
        //super.run();
        try {
            if (mBluetoothSocket != null) {
                //1、去连接
                mBluetoothSocket.connect();
                if (mBluetoothSocket.isConnected()) {
                    mHandler.sendEmptyMessage(BluetoothConstant.WHAT_CONNECT_SUCCESS);
                    //2、去通信
                    mCommunicateThread = new CommunicateThread(mHandler, mBluetoothSocket);
                    mCommunicateThread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mBluetoothSocket != null) {
                try {
                    mBluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            mHandler.sendEmptyMessage(BluetoothConstant.WHAT_CONNECT_ERROR);
            System.out.println("Error-ConnectThread:run()");
        }
    }

    /**
     * 向设备写数据 其实就是一个傀儡 要去调用CommunicateThread的write()方法
     *
     * @param message
     * @return
     */
    public synchronized boolean write(String message) {
        if (mCommunicateThread != null) {
            return mCommunicateThread.write(message);
        }
        return false;
    }

    public void close() {
        if (mCommunicateThread != null) {
            mCommunicateThread.close();
        }
        try {
            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (mBluetoothSocket != null) {
            if (mBluetoothSocket.isConnected()) {
                return true;
            }
        }
        return false;
    }
}
