package com.guohua.north_bulb.util;

import android.graphics.Color;

/**
 * Created by Leo on 16/6/27.
 */
public final class BLECodeUtils {
    public static final int CMD_MODE_SWITCH = 0;//开关
    public static final int CMD_MODE_CONTROL = 1;//控制
    public static final int CMD_MODE_PASSWORD = 2;//加密
    public static final int CMD_MODE_NAME = 3;//改名
    public static final int CMD_MODE_DELAY = 4;//延时关灯
    public static final int CMD_MODE_COLOR = 5;//颜色
    public static final int CMD_MODE_VERSION = 8;//版本

    public static final String SWITCH_OPEN = "open";
    public static final String SWITCH_CLOSE = "close";
    public static final String DEVICE_LIST = "devicelist";
    public static final String GROUP_LIST = "grouplist";
    public static final String TIMER_LIST = "timerlist";

    public static String password = Constant.DEFAULT_PASSWORD;

    public static void setPassword(String pwd) {
        password = pwd;
    }

    /**
     * 编码函数 获取协议数据
     *
     * @param mode
     * @param values
     * @return
     */
    public static String transARGB2Protocol(int mode, Object[] values) {
        String data;

        switch (mode) {
            case CMD_MODE_SWITCH:
                data = values[0].toString();
                break;
            case CMD_MODE_CONTROL:
                data = "ctl" + ":" + values[0] + ":" + values[1] + ":" + values[2] + ":" + values[3] + ":";
                break;
            case CMD_MODE_PASSWORD:
                data = "mg:" + password + ":" + values[0];
                break;
            case CMD_MODE_NAME:
                data = "sn:" + values[0];
                break;
            case CMD_MODE_DELAY:
                data = "d:" + values[0];
                break;
            case CMD_MODE_COLOR:
                data = "save";
                break;
            case CMD_MODE_VERSION:
                data = "version";
                break;
            default:
                data = "error";
                break;
        }

        return data;
    }

    public static String transARGB2Protocol(int color) {
        String data;

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        data = "ctl" + ":" + a + ":" + r + ":" + g + ":" + b + ":";
        return data;
    }
}
