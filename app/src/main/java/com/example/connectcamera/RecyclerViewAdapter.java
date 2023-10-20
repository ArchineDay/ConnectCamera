package com.example.connectcamera;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private Context context;

    private ArrayList<BLEDevice> bleDevices;

    //声明自定义的监听接口
    private static OnItemClickListener mOnItemClickListener;

    //定义接口点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);//单击
    }

    //设置接口的接收方法
    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }


    public RecyclerViewAdapter(Context context, ArrayList<BLEDevice> bleDevices) {
        this.context=context;
        this.bleDevices=bleDevices;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_device, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.deviceName.setText(bleDevices.get(position).getName());
        holder.deviceAddress.setText(bleDevices.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return this.bleDevices.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        TextView deviceAddress;
        int position;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName=itemView.findViewById(R.id.device_name);
            deviceAddress=itemView.findViewById(R.id.device_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener!=null){
                        mOnItemClickListener.onItemClick(v,position);
                    }
                }
            });
        }
    }


}
