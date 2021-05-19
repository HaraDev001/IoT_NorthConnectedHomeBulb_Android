// IBLEService.aidl
package com.guohua.north_bulb.communication;

// Declare any non-default types here with import statements

interface IBLEService {

    boolean connect(String deviceAddress);

    boolean send(String deviceAddress, String message);

    boolean sendByte(String deviceAddress, inout byte[] message);

    void disconnect(String deviceAddress);

    boolean isConnected(String deviceAddress);

    void disconnectAll();

    boolean sendAll(String message);

    boolean sendAllByte(inout byte[] message);

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

}
