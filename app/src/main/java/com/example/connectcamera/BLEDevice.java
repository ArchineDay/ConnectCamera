package com.example.connectcamera;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BLEDevice {

    private String name;//设备名称

    private String address;//地址

    private int id;

    public BLEDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
