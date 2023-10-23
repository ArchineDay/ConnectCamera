package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectcamera.BLEDevice;
import com.example.connectcamera.R;
import com.example.connectcamera.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BLEActivity extends AppCompatActivity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetooth4Adapter;

    // ListView listView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private RecyclerView recyclerView;
    ArrayList<BLEDevice> bleDeviceArrayList = new ArrayList<>();

    public Context mContext;

    private boolean scanning;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 10000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleinfo);

        bindView();

        //初始化
        initBle(mContext);

        //打开蓝牙
        openBlueTooth(mContext, false);

        //搜索
        searchBtDevice();

        //连接
        connectBLE();

    }

    private void bindView() {

        mContext = BLEActivity.this;

        recyclerView = findViewById(R.id.recycler_view);

        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<BLEDevice>());
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

        recyclerViewAdapter = new RecyclerViewAdapter(BLEActivity.this, bleDeviceArrayList);

        ScanCallback scanCallback = new ScanCallback() {

            HashMap<String, BLEDevice> hashMap = new HashMap<String, BLEDevice>();

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                // 处理扫描到的BLE设备
                BluetoothDevice device = result.getDevice();
                // 扫描到设备后的操作
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                BLEDevice bleDevice = new BLEDevice(deviceName, deviceAddress);

                //扫描到的设备如果不为空
                if (deviceName != null && deviceName.length() > 0) {

                    hashMap.put(deviceAddress, bleDevice);//通过地址来判断是否重复

                    bleDeviceArrayList.clear();
                    bleDeviceArrayList.addAll(hashMap.values());


                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(BLEActivity.this));

                    Log.d(TAG, "deviceScan-------------->" + "deviceName: " + deviceName + ",deviceAddress: " + deviceAddress);
                }
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

    /**
     * 连接设备
     */
    public void connectBLE() {
        //连接设备
        //设置点击事件
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               //tipDialog();
                Log.d(TAG, "你点击了第" + position + "个设备");
                Toast.makeText(BLEActivity.this, "你点击了第" + position + "个设备", Toast.LENGTH_SHORT).show();
                tipDialog(bleDeviceArrayList.get(position).getName());
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    /**
     * 提示框
     */
    public void tipDialog(String deviceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BLEActivity.this);
        builder.setTitle("连接蓝牙：");
        builder.setMessage("是否连接至"+deviceName+"？");
        //builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框外的区域让对话框消失
        builder.setCancelable(true);

        //设置正面按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            Toast.makeText(BLEActivity.this, "你点击了确定", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        //设置反面按钮
        builder.setNegativeButton("取消", (dialog, which) -> {
            Toast.makeText(BLEActivity.this, "你点击了取消", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.show();
    }


}