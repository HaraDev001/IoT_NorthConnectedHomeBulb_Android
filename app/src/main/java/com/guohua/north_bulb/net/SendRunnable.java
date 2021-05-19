package com.guohua.north_bulb.net;

import android.text.TextUtils;

import com.guohua.north_bulb.AppContext;

/**
 * @author Leo
 * @detail 发送数据的线程 为了使用线程池而设立 防止主线程阻塞
 * @time 2015-11-04
 */
public class SendRunnable implements Runnable {
    private String addresse;//地址
    private String data;//要发送的数据

    public SendRunnable(String data) {
        this.data = data;//得到数据
    }

    public SendRunnable(String addresse, String data) {
        this.addresse = addresse;
        this.data = data;
    }

    @Override
    public void run() {
        //如果数据为空就结束啦啦啦
        if (data != null && data.length() > 0) {
            //如果地址为空就向所有发送啦啦啦
            if (addresse == null || TextUtils.equals("", addresse)) {
                AppContext.getInstance().sendAll(data);
            } else {
                //否则就向地址发送数据
                AppContext.getInstance().send(addresse, data);
            }
        }
    }
}
