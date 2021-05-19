package com.guohua.north_bulb.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class VibrateUtil {  
    public static void vibrate(final Activity activity, long milliseconds) { 
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE); 
        vib.vibrate(milliseconds); 
    } 
    public static void vibrate(final Activity activity, long[] pattern,boolean isRepeat) { 
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE); 
        vib.vibrate(pattern, isRepeat ? 1 : -1); 
    } 
} 