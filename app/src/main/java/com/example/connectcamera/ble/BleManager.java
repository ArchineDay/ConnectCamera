package com.example.connectcamera.ble;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.connectcamera.BLEDevice;
import com.example.connectcamera.ShowConnectInfo;

public class BleManager {

    private BluetoothAdapter bluetooth4Adapter;

    private OnDeviceSearchListener onDeviceSearchListener;

    private Handler mHandler;

    //扫描设备回调
    private BluetoothAdapter.LeScanCallback leScanCallback =new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            if (bluetoothDevice==null){
                return ;
            }
            if (bluetoothDevice.getName() != null) {
                Log.d(TAG, bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            } else {
                Log.d(TAG, "null" + "-->" + bluetoothDevice.getAddress());
            }
            BLEDevice bleDevice = new BLEDevice(bluetoothDevice, rssi);
            if (onDeviceSearchListener != null) {
                onDeviceSearchListener.onDeviceFound(bleDevice);  //扫描到设备回调
            }
        }
    };
    @SuppressLint({"MissingPermission", "ObsoleteSdkInt"})
    public void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener, long scanTime) {
        if (bluetooth4Adapter == null) {
            Log.e(TAG, "startDiscoveryDevice-->bluetooth4Adapter == null");
            Toast.makeText(new ShowConnectInfo().mContext,"startDiscoveryDevice-->bluetooth4Adapter == null",Toast.LENGTH_SHORT).show();
            return;
        }

        this.onDeviceSearchListener = onDeviceSearchListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "开始扫描设备");
            Toast.makeText(new ShowConnectInfo().mContext,"开始扫描设备",Toast.LENGTH_SHORT).show();
            bluetooth4Adapter.startDiscovery();

        } else {
            return;
        }

        //设定最长扫描时间
        mHandler.postDelayed(stopScanRunnable, scanTime);
    }

    private Runnable stopScanRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            if (onDeviceSearchListener != null) {
                onDeviceSearchListener.onDiscoveryOutTime();  //扫描超时回调
            }
            //scanTime之后还没有扫描到设备，就停止扫描。
            stopDiscoveryDevice();
        }
    };

    /**
     * 停止扫描
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopDiscoveryDevice() {
        mHandler.removeCallbacks(stopScanRunnable);

        if (bluetooth4Adapter == null) {
            Log.e(TAG, "stopDiscoveryDevice-->bluetooth4Adapter == null");
            return;
        }

        if (leScanCallback == null) {
            Log.e(TAG, "stopDiscoveryDevice-->leScanCallback == null");
            return;
        }

        Log.d(TAG, "停止扫描设备");
        Toast.makeText(new ShowConnectInfo().mContext,"停止扫描设备",Toast.LENGTH_SHORT).show();
        bluetooth4Adapter.stopLeScan(leScanCallback);
    }

    /**
     * 本地蓝牙是否处于正在扫描状态
     * @return true false
     */
    @SuppressLint("MissingPermission")
    public boolean isDiscovery() {
        if (bluetooth4Adapter == null) {
            return false;
        }

        return bluetooth4Adapter.isDiscovering();
    }
}
