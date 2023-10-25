package com.example.connectcamera.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.example.connectcamera.R;


public class OperationActivity extends AppCompatActivity {
    BleDevice bleDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        bleDevice = intent.getParcelableExtra("bleDevice");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //断开蓝牙连接
        Log.i(TAG, "OperationActivity: " + "断开蓝牙连接");

        BleManager.getInstance().disconnect(bleDevice);
    }
}