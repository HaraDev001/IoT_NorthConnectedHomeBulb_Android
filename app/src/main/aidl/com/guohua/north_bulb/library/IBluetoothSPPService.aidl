// IBluetoothSPPService.aidl
package com.guohua.north_bulb.library;

// Declare any non-default types here with import statements

interface IBluetoothSPPService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    boolean connect(String deviceAddress);

    boolean send(String deviceAddress, String message);

    void disconnect(String deviceAddress);

    boolean isConnected(String deviceAddress);

    void disconnectAll();

    boolean sendAll(String message);

}
