package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.connectcamera.R;

import java.util.List;
import java.util.UUID;


public class OperationActivity extends AppCompatActivity {
    BleDevice bleDevice;

    //蓝牙服务UUID
    private String WIFI_SVR_UUID_1 = "0000ffb0-0000-1000-8000-00805f9b34fb";
    //蓝牙控制UUID
    private String WIFI_CONTROL_UUID_1 = "0000ffb1-0000-1000-8000-00805f9b34fb";
    //激活蓝牙唤醒
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

        getMessageFromBle();

        ReadAndWriteMessageToBle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //断开蓝牙连接
        Log.i(TAG, "OperationActivity: " + "断开蓝牙连接");
        BleManager.getInstance().disconnect(bleDevice);
    }

    private void getServiceAndCharacteristics() {
        BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        //获取服务
        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
        for (BluetoothGattService service : serviceList) {
            Log.i(TAG, "onServicesUUIDDiscovered: " + service.getUuid().toString());

            serviceUuid = String.valueOf(service.getUuid());

            //获取特征
            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristicList) {
                Log.i(TAG, "onServicesUUIDDiscovered: " + characteristic.getUuid().toString());
                characteristicUuid = String.valueOf(characteristic.getUuid());
            }
        }
    }

    private void getMessageFromBle() {
        //打开indicate
        BleManager.getInstance().indicate(
                bleDevice,
                serviceUuid,
                characteristicUuid,
               // uuid_characteristic_indicate,
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        // 打开通知操作成功
                        Log.i(TAG, "onIndicateSuccess: " + "打开通知操作成功");
                    }

                    @Override
                    public void onIndicateFailure(BleException exception) {
                        // 打开通知操作失败
                        Log.i(TAG, "onIndicateFailure: " + "打开通知操作失败"+exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        Log.i(TAG, "onCharacteristicChanged: " + "打开通知后，设备发过来的数据将在这里出现"+data.toString());

                    }
                });

        //关闭indicate
        //BleManager.getInstance().stopIndicate(bleDevice,serviceUuid, characteristicUuid);
    }


    private void ReadAndWriteMessageToBle(){
        BleManager.getInstance().read(
                bleDevice,
                serviceUuid,
                characteristicUuid,
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        // 读特征值数据成功
                        Log.i(TAG, "onReadSuccess: " + "读特征值数据成功"+data.toString());
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        // 读特征值数据失败
                        Log.i(TAG, "onReadFailure: " + "读特征值数据失败"+exception.toString());
                    }
                });

        BleManager.getInstance().write(
                bleDevice,
                serviceUuid,
                characteristicUuid,
                //data,
                WIFI_WAKEUP_VALUE_1,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                        Log.i(TAG, "onWriteSuccess: " + "发送数据到设备成功"+"current:"+current+"total:"+total+"justWrite:"+justWrite.toString());

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        // 发送数据到设备失败
                        Log.i(TAG, "onWriteFailure: " + "发送数据到设备失败"+exception.toString());
                    }
                });
    }
}