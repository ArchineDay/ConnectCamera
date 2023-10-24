package com.example.connectcamera;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

public class BluetoothLEManager {
    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic characteristic;
    private Handler handler;

    private String  WIFI_SVR_UUID_1  = "0000ffb0-0000-1000-8000-00805f9b34fb";
    private String WIFI_CONTROL_UUID_1 = "0000ffb1-0000-1000-8000-00805f9b34fb";

    private byte[] WIFI_WAKEUP_VALUE_1 = new byte[]{
            (byte) 'T',
            (byte) 'C',
            (byte) 'W',
            (byte) 'A',
            (byte) 'K',
            (byte) 'E',
            (byte) 'U',
            (byte) 'P'
    };

    private String serviceUUID,readUUID,writeUUID;

    public BluetoothLEManager(Context context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler();
    }

    // 连接到指定的蓝牙设备
    @SuppressLint("MissingPermission")
    public void connectToDevice(String deviceAddress) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        // 连接回调
        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    // 连接成功，开始发现服务
                    Log.d("BLE", "连接成功，开始发现服务");
                    gatt.discoverServices();
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    // 连接断开，释放资源
                    Log.d("BLE", "连接断开，释放资源");
                    closeGatt();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                        // 服务发现成功，获取特征值
                    BluetoothGattService service = gatt.getService(UUID.fromString(WIFI_SVR_UUID_1));
                    if (service != null) {
                        characteristic = service.getCharacteristic(UUID.fromString(WIFI_CONTROL_UUID_1));
                    } else {
                        // 未找到指定的服务

                    }
                } else {
                    // 服务发现失败
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // 特征值写入成功

                } else {
                    // 特征值写入失败
                }
            }
        };

        // 连接到设备
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    // 写入数据到特征值
    @SuppressLint("MissingPermission")
    public void writeData(byte[] data) {
        if (bluetoothGatt != null && characteristic != null) {
            characteristic.setValue(data);
            bluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    // 关闭 GATT 连接
    @SuppressLint("MissingPermission")
    private void closeGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    // 停止蓝牙操作（释放资源）
    public void stopBluetoothOperation() {
        closeGatt();
        handler.removeCallbacksAndMessages(null);
    }
}