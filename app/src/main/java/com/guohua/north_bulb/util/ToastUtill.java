package com.guohua.north_bulb.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtill {

    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static Toast showToast(Context context, String s, int length) {
        if (toast == null) {
            toast = Toast.makeText(context, s, length);
            //toast.show();  
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > length) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
        return toast;
    }
}
