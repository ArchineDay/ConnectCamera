package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.connectcamera.R;

import java.util.Arrays;
import java.util.List;


public class OperationActivity extends AppCompatActivity {
    BleDevice bleDevice;

    //CAM8Z8服务UUID(write)
    private String WIFI_SVR_UUID_2 = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    private String WIFI_CONTROL_UUID_2 = "6E400004-B5A3-F393-E0A9-E50E24DCCA9E";
    private byte[] WIFI_WAKEUP_VALUE_2 = new byte[]{
            (byte) 'A',
            (byte) 'T',
            (byte) '+',
            (byte) 'W',
            (byte) 'A',
            (byte) 'K',
            (byte) 'E',
            (byte) 'P',
            (byte) 'U',
            (byte) 'L',
            (byte) 'S',
            (byte) 'E',
            (byte) '=',
            (byte) '1',
            (byte) '0',
            (byte) '\r',
            (byte) '\n'
    };

    private String serviceUuid;
    private String characteristicUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        bleDevice = intent.getParcelableExtra("bleDevice");
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();

        getServiceAndCharacteristics();

        getIndicateFromBle();

        try {
            Thread.sleep(1000); // 休眠1秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        writeMessageToBle();

        readMessageFromBle();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                connectWifi("CAM8Z8_003C84C78420", "1234567890", this);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //断开蓝牙连接
        Log.i(TAG, "OperationActivity: " + "断开蓝牙连接");
        BleManager.getInstance().disconnect(bleDevice);
        //销毁蓝牙管理器
        BleManager.getInstance().destroy();
    }

    private void getServiceAndCharacteristics() {
        BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        //获取服务
        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
        for (BluetoothGattService service : serviceList) {
            Log.i(TAG, "-------onServicesUUIDDiscovered--------: " + service.getUuid().toString());

            serviceUuid = String.valueOf(service.getUuid());

            //获取特征
            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristicList) {
                Log.i(TAG, "onCharacteristicUUIDDiscovered: " + characteristic.getUuid().toString());
                characteristicUuid = String.valueOf(characteristic.getUuid());
            }
        }
    }

    private void getIndicateFromBle() {
        //打开indicate
        BleManager.getInstance().indicate(
                bleDevice,
                "00001801-0000-1000-8000-00805f9b34fb",
                "00002a05-0000-1000-8000-00805f9b34fb",
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        // 打开通知操作成功
                        Log.i(TAG, "onIndicateSuccess: " + "打开通知操作成功");
                    }

                    @Override
                    public void onIndicateFailure(BleException exception) {
                        // 打开通知操作失败
                        Log.i(TAG, "onIndicateFailure: " + "打开通知操作失败" + exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        Log.i(TAG, "onCharacteristicChanged: " + "设备发过来的数据：" + Arrays.toString(data));
                    }
                });

        //关闭indicate
        //BleManager.getInstance().stopIndicate(bleDevice,serviceUuid, characteristicUuid);
    }


    private void readMessageFromBle() {
        BleManager.getInstance().read(
                bleDevice,
                "00001800-0000-1000-8000-00805f9b34fb",
                "00002a00-0000-1000-8000-00805f9b34fb",
                // "00002a01-0000-1000-8000-00805f9b34fb",
//                "00002a04-0000-1000-8000-00805 f9b34fb",
//                "00002a06-0000-1000-8000-00805 f9b34fb",
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        // 读特征值数据成功
                        Log.i(TAG, "onReadSuccess: " + "读特征值数据成功:" + Arrays.toString(data));
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        // 读特征值数据失败
                        Log.i(TAG, "onReadFailure: " + "读特征值数据失败" + exception.toString());
                    }
                });
    }

    private void writeMessageToBle() {
        BleManager.getInstance().write(
                bleDevice,
                "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                //              "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
//                "6e400003-b5a3-f393-e0a9-e50e24dcca9e",
                "6e400004-b5a3-f393-e0a9-e50e24dcca9e",
                WIFI_WAKEUP_VALUE_2,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                        Log.i(TAG, "onWriteSuccess: " + "发送数据到设备成功" + "current:" + current + "total:" + total + "justWrite:" + Arrays.toString(justWrite));
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        // 发送数据到设备失败
                        Log.i(TAG, "onWriteFailure: " + "发送数据到设备失败" + exception.toString());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean connectWifi(String ssid, String pass, Context context) throws InterruptedException {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsidPattern(new PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
                .setWpa2Passphrase(pass)
                .build();
        //创建一个请求
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)//创建的是WIFI网络。
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)//网络不受限
                .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)//信任网络，增加这个连个参数让设备连接wifi之后还联网。
                .setNetworkSpecifier(specifier)
                .build();
        connectivityManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                //TODO 连接OK，做些啥
                Log.d("WIFIActivity", "onAvailable: " + "连接OK");
                connectivityManager.bindProcessToNetwork(network);
            }

            @Override
            public void onUnavailable() {
                //TODO 连接失败，或者被用户取消了，做些啥
                Log.d("WIFIActivity", "onUnavailable: " + "连接失败，或者被用户取消了");
            }
        });
        return false;
    }
}