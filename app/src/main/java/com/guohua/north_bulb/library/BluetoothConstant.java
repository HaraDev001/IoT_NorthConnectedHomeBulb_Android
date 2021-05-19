package com.guohua.north_bulb.library;

/**
 * Created by Leo on 2016/3/16.
 */
public final class BluetoothConstant {
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";

    public static final int WHAT_RECEIVED_DATA = 0;
    public static final int WHAT_CONNECT_ERROR = 1;
    public static final int WHAT_CONNECT_SUCCESS = 2;

    public static final int REQUEST_DEVICE_SCAN = 0;
    public static final int REQUEST_GROUP_SCAN = 1;

    public static final String KEY_ERROR_MESSAGE = "key_error_message";

    public static final String ACTION_CONNECT_ERROR = "action.CONNECT_ERROR";
    public static final String ACTION_CONNECT_SUCCESS = "action.CONNECT_SUCCESS";
}
