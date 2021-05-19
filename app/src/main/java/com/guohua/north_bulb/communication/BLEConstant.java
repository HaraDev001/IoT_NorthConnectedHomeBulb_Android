package com.guohua.north_bulb.communication;

/**
 * Created by Leo on 16/6/27.
 */
public final class BLEConstant {
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";
    public static final String EXTRA_DEVICE_LIST = "extra_device_list";
    public static final String EXTRA_GROUP_LIST = "extra_group_list";
    public static final String EXTRA_GROUP_NUMBER = "extra_group_number";
    public static final String EXTRA_TIMER_LIST = "extra_timer_list";
    public static final int REQUEST_DEVICE_SCAN = 0;
    public static final int REQUEST_DEVICE_ADD = 100;
    public static final int REQUEST_DEVICE_EDIT = 102;
    public static final int REQUEST_GROUP_ADD = 105;
    public static final int REQUEST_GROUP_EDI = 109;
    public static final int REQUEST_TIMER_ADD = 107;
    public static final int REQUEST_COLOR_PALETTE = 112;

    public static final int RESULT_DEVICE_ADD = 101;
    public static final int RESULT_DEVICE_DELETE = 103;
    public static final int RESULT_DEVICE_EDIT = 104;
    public static final int RESULT_GROUP_ADD = 106;
    public static final int RESULT_GROUP_EDIT = 110;
    public static final int RESULT_GROUP_DELETE = 111;
    public static final int RESULT_TIMER_ADD = 108;
    public static final int RESULT_COLOR_PALETTE = 113;

    public static final String EXTRA_RECEIVED_DATA = "extra_received_data";

    public static final String ACTION_RECEIVED_VERSION = "action.RECEIVED_VERSION";
    public static final String ACTION_RECEIVED_TEMPERATURE = "action.RECEIVED_TEMPERATURE";
    public static final String ACTION_RECEIVED_VOLTAGE = "action.RECEIVED_VOLTAGE";

    public static final String ACTION_BLE_CONNECTED = "action.BLE_CONNECTED";
    public static final String ACTION_BLE_DISCONNECTED = "action_BLE_DISCONNECTED";
    public static final String ACTION_BLE_CONNECTING = "action_BLE_CONNECTING";
}
