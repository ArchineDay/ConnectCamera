package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.connectcamera.BluetoothLEManager;
import com.example.connectcamera.R;
import com.example.connectcamera.RecyclerViewAdapter;
import com.example.connectcamera.comm.ObserverManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BLEActivity extends AppCompatActivity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetooth4Adapter;

    // ListView listView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private BluetoothDevice bluetoothDevice;

    private RecyclerView recyclerView;
    List<BleDevice> bleDeviceArrayList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private ImageView img_loading;

    BleDevice bleDevice;

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
        //connectBLE();

        //connect(bleDevice);

    }

    private void bindView() {

        mContext = BLEActivity.this;

        recyclerView = findViewById(R.id.recycler_view);

        img_loading = findViewById(R.id.img_loading);
        progressDialog = new ProgressDialog(this);
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
    private BluetoothDevice searchBtDevice() {

        BluetoothLeScanner bluetoothLeScanner = bluetooth4Adapter.getBluetoothLeScanner();

        recyclerViewAdapter = new RecyclerViewAdapter(BLEActivity.this, bleDeviceArrayList);

        ScanCallback scanCallback = new ScanCallback() {

            HashMap<String, BleDevice> hashMap = new HashMap<String, BleDevice>();

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                // 处理扫描到的BLE设备
                bluetoothDevice = result.getDevice();
                bleDevice= new BleDevice(bluetoothDevice, result.getRssi(), result.getScanRecord().getBytes(), System.currentTimeMillis());
                // 扫描到设备后的操作
                String deviceName = bluetoothDevice.getName();
                String deviceAddress = bluetoothDevice.getAddress();

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
        return bluetoothDevice;
    }

    /**
     * 连接设备
     */
    @SuppressLint("MissingPermission")
    public void connectBLE() {
        //连接设备
        //设置点击事件
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //tipDialog();

                int pos=position+1;
                Log.d(TAG, "你点击了第" + pos + "个设备");
                Toast.makeText(BLEActivity.this, "你点击了第" + pos + "个设备", Toast.LENGTH_SHORT).show();

                //跳转提示框开始连接
                tipDialog(bleDeviceArrayList.get(position).getName());
            }
        });
    }

    /**
     * 提示框
     */
    public void tipDialog(String deviceName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(BLEActivity.this);
        builder.setTitle("连接蓝牙：");
        builder.setMessage("是否连接至" + deviceName + "？");
        //builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框外的区域让对话框消失
        builder.setCancelable(true);

        //设置正面按钮
        builder.setPositiveButton("确定", (dialog, which) ->

        {
            Toast.makeText(BLEActivity.this, "你点击了确定", Toast.LENGTH_SHORT).show();

            //开始连接
            BluetoothLEManager bluetoothLEManager=new BluetoothLEManager(this.mContext);
            bluetoothLEManager.connectToDevice(bluetoothDevice.getAddress());


            connect(bleDevice);

            dialog.dismiss();
        });

        //设置反面按钮
        builder.setNegativeButton("取消", (dialog, which) ->

        {
            Toast.makeText(BLEActivity.this, "你点击了取消", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.show();
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                img_loading.clearAnimation();
                img_loading.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                Toast.makeText(BLEActivity.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
               // recyclerViewAdapter.addDevice(bleDevice);
                recyclerViewAdapter.notifyDataSetChanged();
                //mDeviceAdapter.addDevice(bleDevice);
                //mDeviceAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

                //recyclerViewAdapter.removeDevice(bleDevice);
                recyclerViewAdapter.notifyDataSetChanged();
                //mDeviceAdapter.removeDevice(bleDevice);
                //mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(BLEActivity.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BLEActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }

            }
        });
    }


}
