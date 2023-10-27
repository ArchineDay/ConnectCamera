package com.example.connectcamera.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.connectcamera.R;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class WIFIActivity extends AppCompatActivity {

    private static WifiManager wifiManager;
    private Context mContext = this;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);

        scanWifiDevice(mContext);

//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                connectWifi("CAM8Z8_003C84C78420", "1234567890", this);
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void scanWifiDevice(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            Log.d("WIFIActivity", "scanWifiDevice: " + "wifi未开启");
            Toast.makeText(context, "wifi未开启", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                wifiManager.setWifiEnabled(true);
            else {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                context.startActivity(panelIntent);
            }
        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}