package com.guohua.north_bulb.bean;

/**
 * @author Leo
 *         #time 2016-08-25
 *         #detail 情景
 */
public class SceneBean {
    public Object tag;
    public String title;
    public String detail;
    public int picture;
    public boolean isRunning;

    public SceneBean() {
    }

    public SceneBean(Object tag, String title, String detail, int picture) {
        this.tag = tag;
        this.title = title;
        this.detail = detail;
        this.picture = picture;
        isRunning = false;
    }
}
