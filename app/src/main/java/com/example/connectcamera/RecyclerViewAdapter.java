package com.example.connectcamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.InnerHolder> {
    private Context context;
    private List bleDevices;

    //声明自定义的监听接口
    private static OnItemClickListener mOnItemClickListener;

    //定义接口点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);//单击
    }

    //设置接口的接收方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public RecyclerViewAdapter(Context context, List bleDevices) {
        this.context = context;
        Log.d("RecyclerViewAdapter", "Context: " + context.getClass().getSimpleName());
        this.bleDevices = bleDevices;
    }

    @NonNull
    @Override //创建ViewHolder,创建布局
    public RecyclerViewAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_device, parent, false);
        return new InnerHolder(view);
    }

    @Override //返回子项个数
    public int getItemCount() {
        return this.bleDevices.size();
    }

    @SuppressLint("SetTextI18n")
    @Override //绑定子项数据
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.InnerHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.deviceName.setText(((BleDevice)bleDevices.get(position)).getName());
        holder.deviceAddress.setText(((BleDevice)bleDevices.get(position)).getMac());

//        holder.deviceName.setText(bleDevices.get(position).toString());
//        holder.deviceAddress.setText(bleDevices.get(position).toString());

        holder.position = position;//设置position
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        int position;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);

            //设置点击事件监听器
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }
}
