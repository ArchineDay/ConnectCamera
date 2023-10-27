package com.example.connectcamera.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.connectcamera.R;
import com.example.connectcamera.deprecated.WIFIActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取按钮并监听
        Button blueConnectBtn = findViewById(R.id.blueConnect);
        Button wifiConnectBtn = findViewById(R.id.wifiConnect);

        System.out.println(blueConnectBtn.getId());

        blueConnectBtn.setOnClickListener(this);
        wifiConnectBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()== R.id.blueConnect) {
            //跳转到蓝牙连接界面
            Intent intent = new Intent();
            intent.setClass(this, FastBleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.wifiConnect) {
            //跳转到wifi连接界面
            Intent intent = new Intent();
            intent.setClass(this, WIFIActivity.class);
            startActivity(intent);
        }
    }



}