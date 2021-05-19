package com.guohua.north_bulb.bean;

/**
 * Created by Leo on 2015/10/31.
 * 情景模式中的标题描述和图片
 */
public class StyleBean {
    public static final int STATE_ON = 0;
    public static final int STATE_OFF = 1;

    public int id;
    public String title;
    public String subtitle;
    public int drawableId;
    public int state;

    public StyleBean() {
    }

    public StyleBean(int id, String title, String subtitle, int drawableId) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.drawableId = drawableId;
        this.state = STATE_OFF;
    }

    public StyleBean(int id, String title, String subtitle, int drawableId, int state) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.drawableId = drawableId;
        this.state = state;
    }
}
