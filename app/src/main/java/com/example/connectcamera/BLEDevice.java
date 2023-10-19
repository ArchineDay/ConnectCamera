package com.example.connectcamera;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BLEDevice {

    private BluetoothDevice bluetoothDevice;//设备

    private String  address;//蓝牙信号

    public BLEDevice(BluetoothDevice bluetoothDevice, String  address) {
        this.bluetoothDevice = bluetoothDevice;
        this.address = address;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
