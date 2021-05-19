package com.guohua.north_bulb.communication;

/**
 * @author Leo
 *         #time 2016-08-17
 *         #detail 解析扫描时的scanRecord数据为BLERecord对象 进行匹配是否为制定设备
 */
public class BLERecord {
    private static final byte[] filter = new byte[]{2, 1, 6, 3, 2, -16, -1, 12, 9};

    public static boolean isOurDevice(byte[] scanRecord) {
        if (scanRecord == null) {
            return false;
        }

        int length = filter.length;
        for (int i = 0; i < length; i++) {
            if (filter[i] != scanRecord[i]) {
                return false;
            }
        }

        return true;
    }
}
