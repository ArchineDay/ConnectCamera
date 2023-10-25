package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.connectcamera.R;
import com.example.connectcamera.RecyclerViewAdapter;

import java.util.HashMap;
import java.util.List;

public class FastBleActivity extends AppCompatActivity {

    private boolean isFast = false;
    private Context mContext = this;
    private RecyclerView recyclerView;

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
                    scanResultList.addAll(deviceHashMap.values());

                    //添加设备至recyclerView
                    recyclerView.setAdapter(new RecyclerViewAdapter(FastBleActivity.this, scanResultList));
                    recyclerView.setLayoutManager(new LinearLayoutManager(FastBleActivity.this));
                }
            }
        });
    }


}