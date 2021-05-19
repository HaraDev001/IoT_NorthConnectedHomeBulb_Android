package com.guohua.north_bulb.util;

import android.graphics.Color;

import com.guohua.north_bulb.bean.SceneListInfo;

/**
 * Created by Leo on 2015/10/30.
 */
public final class Constant {
    /**
     * 键值对的键
     */
    public static final String RED_GRADIENT_DELAY = "red_gradient_delay";
    public static final String GREEN_GRADIENT_DELAY = "green_gradient_delay";
    public static final String BLUE_GRADIENT_DELAY = "blue_gradient_delay";
    public static final String KEY_DEVICE_NAME = "device_name";
    public static final String KEY_DEVICE_ADDRESS = "device_address";
    public static final String KEY_SHAKE_MODE = "shake_mode";
    public static final String KEY_COLOR = "key_color";
    public static final String KEY_PERSONAL_FEEL = "personal_feel";
    public static final String KEY_SPLASH_NAME = "key_splash_name";
    public static final String KEY_THRESHOLD = "key_threshold";
    public static final String KEY_BLUETOOTH_STATE = "key_bluetooth_state";
    public static final String KEY_STATUS_MESSAGE = "key_status_message";
    public static final String KEY_DEVICE_SWITCH = "key_device_switch";
    public static final String KEY_DEVICE_COLOR = "key_device_color";
    public static final String KEY_TIMER = "key_timer";
    public static final String EXIST_TIMER_OPEN = "exist_timer_open";
    public static final String KEY_TIMER_OPEN = "key_timer_open";
    public static final String KEY_TIMER_CLOSE = "key_timer_close";
    public static final String KEY_TIMER_MODE = "key_timer_mode";
    public static final String KEY_SELFIE_RUN = "key_selfie_run";
    public static final String KEY_AUTO_CONNECT = "key_auto_connect";
    public static final String KEY_BLUETOOTH_INIT_STATE = "key_bluetooth_init_state";
    public static final String CALL_REMINDER_SHINEMODE = "call_reminder_shinemode";
    public static final String CALL_REMINDER_SHINEMODE_VALUE = "call_reminder_shinemode_value";
    /**
     * 网址
     */
    public static final String OFFICIAL_WEBSITE = "http://www.guohua-oet.com";
    public static final String UPGRADE_ADDRESS = "http://www.guohua-net.com/magiclamp/mlight.xml";
//    public static final String SPLASH_ADDRESS = "http://www.guohua-net.com/magiclamp/splash.xml";

    /**
     * 密码目录改变颜色
     */
    public static final String DEFAULT_PASSWORD_HEAD = "mk:";
    public static final String DEFAULT_PASSWORD = "0000";
    public static final String CONTENT_DIR = "guohua";
    public static final int WHAT_CHANGE_COLOR = 1;

    public static final String IS_APPINTRO = "isfirsttime";

    /**
     * 用到的action
     */
    public static final String ACTION_EXIT = "com.guohua.glight.ACTION_EXIT";

    public static final String ACTION_BLUETOOTH_STATE = "action.BLUETOOTH_STATE";
    public static final String ACTION_RECEIVED_STATUS = "action.RECEIVED_STATUS";

    public static final String ACTION_INIT_STATUS = "action.INIT_STATUS";
    public static final String ACTION_TEMPERATURE_STATUS = "action.TEMPERATURE_STATUS";
    public static final String ACTION_VOLTAGE_STATUS = "action.VOLTAGE_STATUS";
    public static final String ACTION_FIRMWARE_VERSION = "action_FIRMWARE_VERSION";
    public static final String ACTION_OPENLIGHT_TIMER = "action_OPENLIGHT_TIMER";

    //情景模式监听状态值
    public static final int DRIVEMODE_WHITE_CODE = 9;
    public static final int DRIVEMODE_RED_CODE = 10;
    public static final int DRIVEMODE_GREEN_CODE = 11;
    public static final int DRIVEMODE_BLUE_CODE = 12;
    public static final int DRIVEMODE_REDGREEN_CODE = 13;
    public static final int DRIVEMODE_REDBLUE_CODE = 14;
    public static final int DRIVEMODE_BLUEGREEN_CODE = 15;
    public static final int DRIVEMODE_MIX_CODE = 16;
    public static final int DRIVEMODE_DIY_CODE = 17;

    public static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 100;
    public static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN = 101;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 102;
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 103;
    public static final int MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS = 104;

    public static final int RED = Color.argb(0, 255, 0, 0); // 红色
    public static final int GREEN = Color.argb(0, 0, 255, 0); // 绿色
    public static final int BLUE = Color.argb(0, 0, 0, 255); // 蓝色
    public static final int COLORMAXVALUE = 255;
    public static final int DEFAULTSTOPGAPVALUE = 10;
    public static final int HARDWARETIMEUNIT = 4;
    public static final int SOFTWARETIMEUNIT = 1000;
    public static final int HARDWAREINITGRADIENTCOLORGAP = 0;
    public static final int DEFAULTGRADIENTGAPVALUE = 0;
    public static int RED_GRADIENT_DELAY_VALUE = 1;
    public static int GREEN_GRADIENT_DELAY_VALUE = 1;
    public static int BLUE_GRADIENT_DELAY_VALUE = 1;

    /**
     * 开关灯的命令
     */
    public static final String CMD_OPEN_LIGHT = "open";
    public static final String CMD_CLOSE_LIGHT = "close";


    //默认日出日落情景模式  色温：日出黄昏2000K，rgb（255,141,11），#ff8d0b，中午5500k，rgb（255，236,224），#ffece0,
    public static int SunSceneInfoId = 0;
    public static String SunSceneName = "SUN";
    public static int SUN_RED_GRADIENT_DELAY = 0;
    public static int SUN_GREEN_GRADIENT_DELAY = 0;
    public static int SUN_BLUE_GRADIENT_DELAY = 0;
    public static int SunIsStartGradientRampService = 2;
    public static int SUNSCENESTOPGAPVALUE = 30;
    public static int SUNSCENEGRADIENTGAPVALUE = 5;
    public static boolean SunSceneGradientGapRedCBChecked = false;
    public static boolean SunSceneGradientGapGreenCBChecked = true;
    public static boolean SunSceneGradientGapBlueCBChecked = true;
    public static int SunSceneCurClickColorImgOnOff[] = {1, 0, 1, 1};
    public static int SunSceneDefaultColor[] = {0, 87, 5, 2, 0, 255, 215, 86};
    public static int SunSceneGradientRampStopGap[] = {SUNSCENESTOPGAPVALUE, SUNSCENESTOPGAPVALUE, SUNSCENESTOPGAPVALUE, SUNSCENESTOPGAPVALUE};
    public static int SunSceneGradientRampGradientGap[] = {SUNSCENEGRADIENTGAPVALUE, SUNSCENEGRADIENTGAPVALUE - 1, SUNSCENEGRADIENTGAPVALUE, SUNSCENEGRADIENTGAPVALUE - 3};
    public static int SUN_DOWN_DELAY = ((SunSceneDefaultColor[6] - Constant.HARDWAREINITGRADIENTCOLORGAP - SunSceneDefaultColor[2]) / SUNSCENEGRADIENTGAPVALUE) * (SUNSCENESTOPGAPVALUE * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT);

    public static int SunSceneDatasHead[] = {1, 1, 1, 1, 1, 0, 0, 0};
    public static int SunSceneDefaultGradientColorDeta[] = {1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1};
    public static int SunSceneDefaultGradientColorTime[] = {15, 1, 1, 20, 65, 1, 16, 7, 16, 1, 25, 8};
    public static int SunSceneDefaultGradientColor[] = {100, 1, 2, 165, 20, 2, 255, 200, 80, 255, 255, 255, 2, 1, 2};
    public static int SUN_DOWN_DELAY1 = ((SunSceneDefaultGradientColor[0])) * ((SunSceneDefaultGradientColorTime[0]) * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT);
    public static int SUN_DOWN_DELAY2 = ((SunSceneDefaultGradientColor[3] - SunSceneDefaultGradientColor[0])) * ((SunSceneDefaultGradientColorTime[3]) * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT);
    public static int SUN_DOWN_DELAY3 = ((SunSceneDefaultGradientColor[6] - SunSceneDefaultGradientColor[3])) * ((SunSceneDefaultGradientColorTime[6]) * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT);
    public static int SUN_DOWN_DELAY4 = ((SunSceneDefaultGradientColor[11] - SunSceneDefaultGradientColor[8])) * ((SunSceneDefaultGradientColorTime[11]) * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT);

    public static SceneListInfo.SceneInfo sunScene = new SceneListInfo.SceneInfo(SunSceneInfoId, SunSceneName, SUN_RED_GRADIENT_DELAY, SUN_GREEN_GRADIENT_DELAY,
            SUN_BLUE_GRADIENT_DELAY, SunIsStartGradientRampService, SUNSCENESTOPGAPVALUE, SUNSCENEGRADIENTGAPVALUE, SunSceneGradientGapRedCBChecked,
            SunSceneGradientGapGreenCBChecked, SunSceneGradientGapBlueCBChecked, SunSceneCurClickColorImgOnOff, SunSceneDatasHead, SunSceneDefaultColor,
            SunSceneGradientRampStopGap, SunSceneGradientRampGradientGap);

    //默认月圆月缺情景模式
    public static int MoonSceneInfoId = 1;
    public static String MoonSceneName = "MOON";
    public static int MOON_RED_GRADIENT_DELAY = 0;
    public static int MOON_GREEN_GRADIENT_DELAY = 0;
    public static int MOON_BLUE_GRADIENT_DELAY = 0;
    public static int MoonIsStartGradientRampService = 3;
    public static int MOONSCENESTOPGAPVALUE = 50;
    public static int MOONSCENEGRADIENTGAPVALUE = 10;
    public static boolean MoonSceneGradientGapRedCBChecked = true;
    public static boolean MoonSceneGradientGapGreenCBChecked = true;
    public static boolean MoonSceneGradientGapBlueCBChecked = true;
    public static int MoonSceneCurClickColorImgOnOff[] = {1, 1, 1, 1};
    public static int MoonSceneDatasHead[] = {1, 0, 0, 0, 1, 0, 0, 0};
    public static int MoonSceneDefaultColor[] = {0, 255, 141, 11, 0, 255, 236, 224};
    public static int MoonSceneGradientRampStopGap[] = {MOONSCENESTOPGAPVALUE, MOONSCENESTOPGAPVALUE * 2, MOONSCENESTOPGAPVALUE, MOONSCENESTOPGAPVALUE};
    public static int MoonSceneGradientRampGradientGap[] = {MOONSCENEGRADIENTGAPVALUE, MOONSCENEGRADIENTGAPVALUE, MOONSCENEGRADIENTGAPVALUE, MOONSCENEGRADIENTGAPVALUE};

    public static SceneListInfo.SceneInfo moonScene = new SceneListInfo.SceneInfo(MoonSceneInfoId, MoonSceneName, MOON_RED_GRADIENT_DELAY, MOON_GREEN_GRADIENT_DELAY,
            MOON_BLUE_GRADIENT_DELAY, MoonIsStartGradientRampService, MOONSCENESTOPGAPVALUE, MOONSCENEGRADIENTGAPVALUE, MoonSceneGradientGapRedCBChecked,
            MoonSceneGradientGapGreenCBChecked, MoonSceneGradientGapBlueCBChecked, MoonSceneCurClickColorImgOnOff, MoonSceneDatasHead, MoonSceneDefaultColor,
            MoonSceneGradientRampStopGap, MoonSceneGradientRampGradientGap);

    //默认红绿蓝波浪情景模式
    public static int RgbSceneInfoId = 2;
    public static String RgbSceneName = "三色水波纹";
    public static int RgbIsStartGradientRampService = 3;
    public static int RGBSCENESTOPGAPVALUE = 1;
    public static int RGBSCENEGRADIENTGAPVALUE = 1;
    public static int RGB_RED_GRADIENT_DELAY = 0;
    public static int RGB_GREEN_GRADIENT_DELAY = ((Constant.COLORMAXVALUE - Constant.HARDWAREINITGRADIENTCOLORGAP) / RGBSCENEGRADIENTGAPVALUE) * (RGBSCENESTOPGAPVALUE * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT) * 2;
    public static int RGB_BLUE_GRADIENT_DELAY = RGB_GREEN_GRADIENT_DELAY * 2;
    public static boolean RgbSceneGradientGapRedCBChecked = true;
    public static boolean RgbSceneGradientGapGreenCBChecked = true;
    public static boolean RgbSceneGradientGapBlueCBChecked = true;
    public static int RgbSceneCurClickColorImgOnOff[] = {1, 1, 1, 1};
    public static int RgbSceneDatasHead[] = {1, 0, 0, 0, 1, 0, 0, 0};
    public static int RgbSceneDefaultColor[] = {0, 0, 0, 0, 0, 255, 255, 255};
    public static int RgbSceneGradientRampStopGap[] = {RGBSCENESTOPGAPVALUE, RGBSCENESTOPGAPVALUE, RGBSCENESTOPGAPVALUE, RGBSCENESTOPGAPVALUE};
    public static int RgbSceneGradientRampGradientGap[] = {RGBSCENEGRADIENTGAPVALUE, RGBSCENEGRADIENTGAPVALUE, RGBSCENEGRADIENTGAPVALUE, RGBSCENEGRADIENTGAPVALUE};

    public static SceneListInfo.SceneInfo rgbScene = new SceneListInfo.SceneInfo(RgbSceneInfoId, RgbSceneName, RGB_RED_GRADIENT_DELAY, RGB_GREEN_GRADIENT_DELAY,
            RGB_BLUE_GRADIENT_DELAY, RgbIsStartGradientRampService, RGBSCENESTOPGAPVALUE, RGBSCENEGRADIENTGAPVALUE, RgbSceneGradientGapRedCBChecked,
            RgbSceneGradientGapGreenCBChecked, RgbSceneGradientGapBlueCBChecked, RgbSceneCurClickColorImgOnOff, RgbSceneDatasHead, RgbSceneDefaultColor,
            RgbSceneGradientRampStopGap, RgbSceneGradientRampGradientGap);

    //默认存储渐变情景模式
    public final static int SaveDiyColor[][] = {{0, 0xf8, 1, 0, 0, 1, 1, 1, 2, 0, 0, 1, 0, 0},
            {1, 0xf8, 0, 1, 0, 1, 1, 1, 0, 2, 0, 0, 1, 0},
            {2, 0xf8, 0, 0, 1, 1, 1, 1, 0, 0, 2, 0, 0, 1},
            {3, 0xf8, 1, 1, 0, 2, 2, 2, 2, 2, 0, 1, 1, 0},
            {4, 0xf8, 1, 0, 1, 2, 2, 2, 2, 0, 2, 1, 0, 1},
            {5, 0xf8, 0, 1, 1, 2, 2, 2, 0, 2, 2, 0, 1, 1},
            {6, 0xf8, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1},
            {7, 0xf8, 1, 2, 3, 1, 1, 1, 2, 2, 2, 1, 1, 1},
            {8, 0xf8, 3, 1, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1},
            {9, 0xf8, 2, 3, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1},
    };

    //小夜灯
    public static int COLORMOONMODE = Color.argb(255, 160, 60, 10);

    //handler的postdelay延迟
    public static int HANDLERDELAY = 150;

    //toast时长
    public static int TOASTLENGTH = 75;

    //来电提醒模式
    public static int REMINDERLIGHTSHINEMODE[][] = {{124, 111, 0, 0, 10, 0, 0, 121},
            {122, 0, 111, 0, 0, 10, 0, 121},
            {121, 0, 0, 111, 0, 0, 10, 121}};

    //来电提醒模式的Scene id标识值
    public static int CALLREMINDERMODEID = -2;

    //底层要求的最低单路灯珠色值，太低会出现闪烁
    public static int HARDWARELEDMINCOLORVALUE = 6;
}