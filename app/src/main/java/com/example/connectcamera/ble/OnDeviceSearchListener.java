package com.example.connectcamera.ble;

import com.example.connectcamera.BLEDevice;

public interface OnDeviceSearchListener {

    //搜索到设备
    void onDeviceFound(BLEDevice bleDevice);
    //搜索超时
    void onDiscoveryOutTime();
}
