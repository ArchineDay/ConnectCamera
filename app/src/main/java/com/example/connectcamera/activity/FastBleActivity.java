package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.connectcamera.BluetoothLEManager;
import com.example.connectcamera.R;
import com.example.connectcamera.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FastBleActivity extends AppCompatActivity {

    private boolean isFast = false;
    private Context mContext = this;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    List<BleDevice> bleDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastble);

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);

        initView();

//        list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add("这是第" + i + "个测试");
//        }
//        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity2.this, list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        openBluTooth(mContext, isFast);
        scanDevice();

        selectDevice();

    }


    public void initView() {
        //初始化控件
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
    }

    @SuppressLint("MissingPermission")
    public void openBluTooth(Context context, boolean isFast) {
        if (BleManager.getInstance().isBlueEnable()) {
            //蓝牙已打开
            Toast.makeText(this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
        } else if (isFast) {
            //快速打开蓝牙
            BleManager.getInstance().enableBluetooth();
        } else {
            //提示蓝牙未打开
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }
    }

    public void scanDevice() {
        //配置扫描规则
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        //开始扫描
        BleManager.getInstance().scan(new BleScanCallback() {

            @Override
            public void onScanStarted(boolean success) {
                Log.d("MainActivity2", "onScanStarted: " + success);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                //Log.d("MainActivity2", "onLeScan: " + bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (bleDevice.getName() != null) {
                    Log.d(TAG, "onScanning------" + "deviceName: " + bleDevice.getName() + ",deviceAddress: " + bleDevice.getMac());
                }
                Toast.makeText(mContext, "正在扫描中", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.d("MainActivity2", "onScanFinished: " + scanResultList.size());

                if (scanResultList.size() == 0) {
                    Toast.makeText(FastBleActivity.this, "未扫描到设备", Toast.LENGTH_SHORT).show();
                }
                else if (scanResultList.size() > 0) {
                    Toast.makeText(FastBleActivity.this, "扫描到" + scanResultList.size() + "个设备", Toast.LENGTH_SHORT).show();
                    //处理scanResultList
                    HashMap<String, BleDevice> deviceHashMap = new HashMap<>();
                    for (BleDevice bleDevice : scanResultList) {
                        //过滤掉设备名为空的设备
                        if (bleDevice.getName() != null&&bleDevice.getName().length()>0){
                            deviceHashMap.put(bleDevice.getMac(), bleDevice);
                        }
                    }
                    scanResultList.clear();
                    bleDeviceList =scanResultList;
                    bleDeviceList.addAll(deviceHashMap.values());

                    //添加设备至recyclerView
                    recyclerViewAdapter = new RecyclerViewAdapter(FastBleActivity.this, bleDeviceList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(FastBleActivity.this));
                }
            }
        });
    }

    public void selectDevice(){
        recyclerViewAdapter = new RecyclerViewAdapter(FastBleActivity.this, bleDeviceList);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BleDevice bleDevice = bleDeviceList.get(position);
                int pos=position+1;
                Log.d(TAG, "你点击了第" + pos + "个设备");
                Toast.makeText(FastBleActivity.this, "你点击了第" + pos + "个设备", Toast.LENGTH_SHORT).show();

                //跳转提示框开始连接
                tipDialog(bleDevice);
            }
        });
    }

    public void tipDialog(BleDevice bleDevice) {

        AlertDialog.Builder builder = new AlertDialog.Builder(FastBleActivity.this);
        builder.setTitle("连接蓝牙：");
        builder.setMessage("是否连接至" + bleDevice.getName() + "？");
        //builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框外的区域让对话框消失
        builder.setCancelable(true);

        //设置正面按钮
        builder.setPositiveButton("确定", (dialog, which) ->
        {
            Toast.makeText(FastBleActivity.this, "你点击了确定", Toast.LENGTH_SHORT).show();

            //开始连接
            connectDevice(bleDevice);

            dialog.dismiss();
        });

        //设置反面按钮
        builder.setNegativeButton("取消", (dialog, which) ->

        {
            Toast.makeText(FastBleActivity.this, "你点击了取消", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.show();
    }


    public void connectDevice(BleDevice bleDevice){
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.i(TAG, "onStartConnect: ");

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException e) {
                Log.i(TAG, "onConnectFail: " + e.toString());

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG, "onConnectSuccess: "+bleDevice.getName()+"连接成功"+"mac地址："+bleDevice.getMac()+"status："+status);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG, "onDisConnected: "+bleDevice.getName()+"连接断开"+"mac地址："+bleDevice.getMac()+"status："+status);
            }
        });
    }
}