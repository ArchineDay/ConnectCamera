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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowConnectInfo extends AppCompatActivity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetooth4Adapter;

    ListView listView;

    public Context mContext;

    private boolean scanning;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_connect_info);

        listView = findViewById(R.id.list_view);

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
    public void openBlueTooth(Context context, boolean isFast) {
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
    }

    /**
     * 扫描设备
     */
    @SuppressLint("MissingPermission")
    private void searchBtDevice() {
        BluetoothLeScanner bluetoothLeScanner = bluetooth4Adapter.getBluetoothLeScanner();

        //LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();

        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                // 处理扫描到的BLE设备
                BluetoothDevice device = result.getDevice();
                // 扫描到设备后的操作
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                //扫描到的设备如果不为空，打印出来
                if (deviceName != null && deviceName.length() > 0) {
                    Log.d(TAG, "deviceScan-------------->" + "deviceName: " + deviceName + ",deviceAddress: " + deviceAddress);
                }

//                leDeviceListAdapter.addDevice(result.getDevice());
//                leDeviceListAdapter.notifyDataSetChanged();

            }
        };


        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        List<ScanFilter> scanFilters = new ArrayList<>();


        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(scanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }

    }


//
//    //盛放扫描到的设备
//    private class LeDeviceListAdapter extends BaseAdapter {
//        private final ArrayList<BluetoothDevice> mLeDevices;
//        private final LayoutInflater mInflator;
//
//        public LeDeviceListAdapter() {
//            super();
//            mLeDevices = new ArrayList<BluetoothDevice>();
//            mInflator = ShowConnectInfo.this.getLayoutInflater();
//        }
//
//        public void addDevice(BluetoothDevice device) {
//            if(!mLeDevices.contains(device)) {
//                mLeDevices.add(device);
//            }
//        }
//
//        public BluetoothDevice getDevice(int position) {
//            return mLeDevices.get(position);
//        }
//
//        public void clear() {
//            mLeDevices.clear();
//        }
//
//        @Override
//        public int getCount() {
//            return mLeDevices.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mLeDevices.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//
//            ViewHolder viewHolder;
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.activity_show_connect_info, null);
//                viewHolder = new ViewHolder();
//                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
//                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            BluetoothDevice device = mLeDevices.get(i);
//            @SuppressLint("MissingPermission") final String deviceName = device.getName();
//            if (deviceName != null && deviceName.length() > 0)
//                viewHolder.deviceName.setText(deviceName);
//            else
//                viewHolder.deviceName.setText(R.string.unknown_device);
//            viewHolder.deviceAddress.setText(device.getAddress());
//
//            return view;
//        }
//    }
//
//    static class ViewHolder {
//        TextView deviceName;
//        TextView deviceAddress;
//    }

    /**
     * 连接设备
     */
    public void connectBLE() {



    }


}