package com.example.connectcamera.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;

import com.example.connectcamera.R;

import java.util.List;

public class WIFIActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);

        //扫描wifi设备
        scanWifiDevice(mContext);
        //连接wifi设备
        //发送wifi设备指令
    }


    @SuppressLint("MissingPermission")
    public void scanWifiDevice(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //检查wifi是否开启
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            //wifi已开启
            Log.i("WIFIActivity", "wifi已开启");
        }else {
            //wifi未开启
            Log.i("WIFIActivity", "wifi未开启");
            //开启wifi

        }
        //扫描wifi设备
        wifiManager.startScan();
        //获取扫描结果
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            Log.i("WIFIActivity", "扫描结果：" + scanResult.toString()+ " 信号强度：" + scanResult.level+"SSID:"+scanResult.SSID);
        }
    }

    public void connectWifiDevice(Context context) {
        //连接wifi设备

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}