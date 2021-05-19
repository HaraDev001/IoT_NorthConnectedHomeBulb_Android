package com.guohua.north_bulb.communication;

import java.util.UUID;

/**
 * @describe ble通信所需要的各个UUID
 * @time 2016-06-27
 * @author Leo
 * 一个BLE设备具有一项或多项服务,每个服务包括一个或多个特征,每个特征具有一个或多个描述
 */
public class SampleGattAttributes {
	//服务所用的UUID
	public static final UUID UUID_SERVICE = UUID
			.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
	//特征所用的UUID
	public static final UUID UUID_CHARACTERISTIC = UUID
			.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	//描述所用的UUID
	public static final UUID UUID_DESCRIPTOR = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
