package com.example.connectcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button blueConnectBtn;
    Button wifiConnectBtn;

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
        Intent intent = new Intent();
        intent.setClass(this, ShowConnectInfo.class);
        startActivity(intent);

    }
}