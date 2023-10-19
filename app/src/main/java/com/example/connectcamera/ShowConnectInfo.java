package com.example.connectcamera;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowConnectInfo extends AppCompatActivity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetooth4Adapter;

    public Context mContext;

//    private static final int REQUEST_ENABLE_BT = 1;
//
//    private boolean mScanning;
//    private Handler handler;
//
//    // Stops scanning after 10 seconds.
//    private static final long SCAN_PERIOD = 10000;
//
//    private static final int DISCOVERY_DEVICE = 0x0A;
//    private static final int DISCOVERY_OUT_TIME = 0x0B;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_connect_info);

        mContext = ShowConnectInfo.this;
        //初始化
        initBle(mContext);

        //打开蓝牙
        openBlueTooth(mContext, false);

        //搜索
        searchBtDevice();

    }


    public boolean initBle(Context context) {
        mContext = context;
        return checkBle(context);
    }

    /**
     * 检测手机是否支持4.0蓝牙
     *
     * @param context 上下文
     * @return true--支持4.0  false--不支持4.0
     */
    @SuppressLint("ServiceCast")
    public boolean checkBle(Context context) {

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            return false;
        }
        bluetooth4Adapter = bluetoothManager.getAdapter();  //BLUETOOTH权限
        if (bluetooth4Adapter == null) {
            return false;
        } else {
            Log.d(TAG, "该设备支持蓝牙4.0");
            Toast.makeText(this, "该设备支持蓝牙4.0", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * 获取蓝牙状态
     */
    public boolean isEnable() {
        if (bluetooth4Adapter == null) {
            return false;
        }
        return bluetooth4Adapter.isEnabled();
    }

    /**
     * 打开蓝牙
     *
     * @param isFast true表示直接打开，false提示用户打开
     */
    @SuppressLint("MissingPermission")
    public BluetoothAdapter openBlueTooth(Context context, boolean isFast) {
        if (!isEnable()) {
            if (isFast) {
                Log.d(TAG, "直接打开手机蓝牙");

                bluetooth4Adapter.enable();
            } else {
                Log.d(TAG, "提示用户去打开手机蓝牙");

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBtIntent);

                Toast.makeText(this, "手机蓝牙状态已开", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "手机蓝牙状态已开");
            Toast.makeText(this, "手机蓝牙状态已开", Toast.LENGTH_SHORT).show();
        }
        return bluetooth4Adapter;

    }

    /**
     * 扫描设备
     */
    @SuppressLint("MissingPermission")
    private void searchBtDevice() {

        BluetoothLeScanner bluetoothLeScanner = bluetooth4Adapter.getBluetoothLeScanner();

        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                // 处理扫描到的BLE设备
                BluetoothDevice device = result.getDevice();
                // 扫描到设备后的操作
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                Log.d(TAG, "deviceScan-------------->"+"deviceName"+deviceName+",deviceAddress"+deviceAddress);
            }
        };

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        List<ScanFilter> scanFilters = new ArrayList<>();
        bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);

    }

}