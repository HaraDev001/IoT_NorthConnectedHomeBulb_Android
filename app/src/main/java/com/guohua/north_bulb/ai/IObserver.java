package com.guohua.north_bulb.ai;

/**
 * @author Leo
 * @detail 观察者模式中的观察者 主要为更新数据
 * @time 2015-11-17
 */
public interface IObserver {
    void update(byte[] bytes);
}
