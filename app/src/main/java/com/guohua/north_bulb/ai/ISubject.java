package com.guohua.north_bulb.ai;

/**
 * @author Leo
 * @detail 观察模式的主题 具有注册 取消注册 通知观察者
 * @time 2015-11-17
 */
public interface ISubject {
    /**
     * 注册观察者
     *
     * @param IObserver
     */
    void registerObserver(IObserver IObserver);

    /**
     * 取消观察者
     *
     * @param IObserver
     */
    void unregisterObserver(IObserver IObserver);

    /**
     * 通知观察者更新数据
     */
    void notifyObserver(byte[] bytes);
}
