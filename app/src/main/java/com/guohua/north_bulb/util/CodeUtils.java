package com.guohua.north_bulb.util;

import android.graphics.Color;

/**
 * @author Leo
 * @time 2016-02-18
 * @detail 对数据进行编码也既是数据协议
 */
public final class CodeUtils {
    public static final int CMD_MODE_SWITCH = 0;//开关
    public static final int CMD_MODE_CONTROL = 1;//控制
    public static final int CMD_MODE_PASSWORD = 2;//加密
    public static final int CMD_MODE_NAME = 3;//改名
    public static final int CMD_MODE_DELAY_CLOSE = 41;//延时关灯
    public static final int CMD_MODE_DELAY_OPEN = 42;//延时开灯
    public static final int CMD_MODE_COLOR = 5;//颜色
    public static final int CMD_MODE_VERSION = 8;//版本
    public static final int CMD_MODE_MUSIC_ON = 9;//触发底层律动
    public static final int CMD_MODE_MUSIC_OFF = 10;//关闭底层律动
    public static final int CMD_MODE_SAVE_DIY_NUM = 11;
    public static final int CMD_MODE_SAVE_DIY_START = 12;

    public static final String SWITCH_OPEN = "open";
    public static final String SWITCH_CLOSE = "close";

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
                data = "mg:" + values[0] + ":" + values[1];
                break;
            case CMD_MODE_NAME:
                data = "sn:" + values[0];
                break;
            case CMD_MODE_DELAY_CLOSE:
                if (values == null) {
                    data = "de off";
                } else {
                    data = "de:" + values[0];
                }
                break;
            case CMD_MODE_DELAY_OPEN:
                if (values == null) {
                    data = "dl off";
                } else {
                    data = "dl:" + values[0];
                }
                break;
            case CMD_MODE_COLOR:
                data = "save";
                break;
            case CMD_MODE_VERSION:
                data = "version";
                break;
            case CMD_MODE_MUSIC_ON:
                data = "music on";
                break;
            case CMD_MODE_MUSIC_OFF:
                data = "music off";
                break;
            case CMD_MODE_SAVE_DIY_NUM:
                data = "rec:" + values[0];
                break;
            case CMD_MODE_SAVE_DIY_START:
                data = "device start" + values[0];
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

    public byte checkCtrMusicSum(byte deta_red, byte deta_green, byte deta_blue, byte deta_red_time, byte deta_green_time, byte deta_blue_time) {
        int sum = deta_red + deta_green + deta_blue + deta_red_time + deta_green_time + deta_blue_time;
        if (sum > 255) {
            sum = (sum >> 4) + ((sum << 4) >> 4);
        }
        return (byte) sum;
    }
}
