package com.example.myapplication;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class callMouseBluetooth {
    private static final String TAG = "MouseBluetooth";
    
    // 连接状态
    boolean isRegistered = false;
    boolean isConnected = false;
    
    // 组件引用
    private Activity activity;
    private MouseMainActivity mouseMainActivity;
    private WebView webView;
    private Context context;
    private ActivityResultLauncher<Intent> requestLauncher;
    private ActivityResultLauncher<Intent> requestLauncher_for_bluetooth;
    
    // 蓝牙相关
    private BluetoothHidDevice mHidDevice;
    private BluetoothDevice mHostDevice;
    public BluetoothAdapter mBtAdapter;
    public BluetoothManager mBtManager;
    
    // 设备列表
    public RecyclerView mrecyclerView;
    public MyAdapter myAdapter;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private List<String> list_devices_name = new ArrayList<>();
    private List<String> list_devices_mac = new ArrayList<>();
    private List<Boolean> list_devices_state = new ArrayList<>();
    
    // 鼠标状态
    private String mac;
    private byte lastButtons = 0;
    private byte lastX = 0;
    private byte lastY = 0;
    private byte lastWheel = 0;
    
    // 状态常量
    private static final int CONNECT_SUCCESS = 0x01;
    private static final int CONNECT_FAILURE = 0x02;
    private static final int DISCONNECT_SUCCESS = 0x03;
    private static final int SEND_SUCCESS = 0x04;
    private static final int SEND_FAILURE = 0x05;

    public callMouseBluetooth(MouseMainActivity mouseMainActivity, WebView webView, 
                             Context context, Activity activity, 
                             ActivityResultLauncher<Intent> requestLauncher, 
                             ActivityResultLauncher<Intent> requestLauncher_for_bluetooth) {
        this.mouseMainActivity = mouseMainActivity;
        this.webView = webView;
        this.context = context;
        this.activity = activity;
        this.requestLauncher = requestLauncher;
        this.requestLauncher_for_bluetooth = requestLauncher_for_bluetooth;
    }

    public void initMouseMap() {
        Log.d(TAG, "初始化鼠标映射");
        // 鼠标不需要复杂的键位映射，主要是坐标和按键状态
    }

    @JavascriptInterface
    public void CallBluetooth() {
        Log.d(TAG, "初始化鼠标蓝牙");
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // 获取BluetoothHidDevice
        mBtAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                Log.d(TAG, "onServiceConnected: " + profile);
                if (profile == BluetoothProfile.HID_DEVICE) {
                    if (!(proxy instanceof BluetoothHidDevice)) {
                        Log.e(TAG, "代理不是HID设备");
                        return;
                    }
                    Log.d(TAG, "连接鼠标HID设备...");
                    mHidDevice = (BluetoothHidDevice) proxy;
                    
                    // 注册到蓝牙HID管理器
                    BluetoothHidManager.getInstance(context).setCurrentHidDevice(mHidDevice);
                    
                    // 注册鼠标设备
                    BluetoothHidDeviceAppSdpSettings sdpSettings = new BluetoothHidDeviceAppSdpSettings(
                        MouseConfig.MOUSE_NAME,
                        MouseConfig.DESCRIPTION,
                        MouseConfig.PROVIDER,
                        BluetoothHidDevice.SUBCLASS1_MOUSE,
                        MouseConfig.MOUSE_DESCRIPTOR
                    );
                    
                    BluetoothHidDeviceAppQosSettings inQos = new BluetoothHidDeviceAppQosSettings(
                        BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED, 200, 2, 200,
                        10000, 10000);
                    
                    BluetoothHidDeviceAppQosSettings outQos = new BluetoothHidDeviceAppQosSettings(
                        BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED, 900, 9, 900,
                        10000, 10000);
                    
                    if (mHidDevice != null) {
                        Toast.makeText(context, "OK for HID profile", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "HID_OK");
                        Log.d(TAG, "Get in register");
                        
                        // 检查蓝牙权限
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "return before register");
                            activity.runOnUiThread(() -> {
                                webView.loadUrl("javascript:Showinformation('赋予权限后，你需要再次点击init初始化')");
                            });
                            String[] list = new String[]{
                                    Manifest.permission.BLUETOOTH_SCAN,
                                    Manifest.permission.BLUETOOTH_CONNECT
                            };
                            ActivityCompat.requestPermissions(activity, list, 1);
                            return;
                        }
                        
                        mHidDevice.registerApp(sdpSettings, inQos, outQos, 
                            Executors.newCachedThreadPool(), mCallback);
                        
                        activity.runOnUiThread(() -> {
                            Toast.makeText(context, "鼠标设备注册中...", Toast.LENGTH_SHORT).show();
                            webView.loadUrl("javascript:Showinformation('鼠标设备注册中...')");
                        });
                    } else {
                        Toast.makeText(context, "Disable for HID profile", Toast.LENGTH_SHORT).show();
                        activity.runOnUiThread(() -> {
                            webView.loadUrl("javascript:Showinformation('手机厂商可能禁用了本机的HID服务')");
                        });
                    }
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HID_DEVICE) {
                    Log.d(TAG, "HID设备服务断开连接");
                    mHidDevice = null;
                }
            }
        }, BluetoothProfile.HID_DEVICE);
        
        Toast.makeText(context, "Init Success!", Toast.LENGTH_SHORT).show();
        activity.runOnUiThread(() -> {
            webView.loadUrl("javascript:Showinformation('init success!')");
        });
    }

    // HID设备回调
    public final BluetoothHidDevice.Callback mCallback = new BluetoothHidDevice.Callback() {
        @Override
        public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
            isRegistered = registered;
            Log.d(TAG, "应用状态改变: " + registered);
            
            if (registered) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "鼠标设备注册成功", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("javascript:Showinformation('鼠标设备注册成功，可以开始连接设备')");
                });
            } else {
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "鼠标设备注册失败", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("javascript:Showinformation('鼠标设备注册失败')");
                });
            }
        }

        @Override
        public void onConnectionStateChanged(BluetoothDevice device, int state) {
            Log.d(TAG, "连接状态改变: " + state);
            
            if (state == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true;
                mHostDevice = device;
                activity.runOnUiThread(() -> {
                    String deviceName = "";
                    try {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            deviceName = device.getName();
                        }
                    } catch (SecurityException e) {
                        deviceName = "未知设备";
                    }
                    Toast.makeText(context, "已连接到: " + deviceName, Toast.LENGTH_SHORT).show();
                    webView.loadUrl("javascript:Showinformation('已连接到设备: " + deviceName + "')");
                });
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false;
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "设备已断开连接", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("javascript:Showinformation('设备已断开连接')");
                });
            }
        }
    };

    // 发送鼠标数据
    @JavascriptInterface
    @SuppressLint("MissingPermission")
    public void sendMouseData(String action, int x, int y) {
        if (!isConnected || mHidDevice == null || mHostDevice == null) {
            Log.w(TAG, "设备未连接，无法发送鼠标数据");
            return;
        }

        byte buttons = 0;
        byte deltaX = 0;
        byte deltaY = 0;
        byte wheel = 0;

        // 解析动作
        switch (action) {
            case "left_click":
                buttons = 1;
                break;
            case "right_click":
                buttons = 2;
                break;
            case "middle_click":
                buttons = 4;
                break;
            case "move":
                // 限制移动范围
                deltaX = (byte) Math.max(-127, Math.min(127, x));
                deltaY = (byte) Math.max(-127, Math.min(127, y));
                break;
            case "scroll_up":
                wheel = -1;
                break;
            case "scroll_down":
                wheel = 1;
                break;
            case "release":
                // 释放所有按键
                buttons = 0;
                break;
        }

        // 构建HID报告 (4字节)
        byte[] report = new byte[]{
            buttons,  // 按钮状态
            deltaX,   // X轴移动
            deltaY,   // Y轴移动
            wheel     // 滚轮状态
        };

        // 发送报告
        boolean success = mHidDevice.sendReport(mHostDevice, 4, report);
        
        if (success) {
            Log.d(TAG, "鼠标数据发送成功: " + action + " (" + x + "," + y + ")");
        } else {
            Log.e(TAG, "鼠标数据发送失败");
        }

        // 如果是点击操作，需要发送释放报告
        if (action.contains("click")) {
            // 延迟发送释放报告
            new android.os.Handler().postDelayed(() -> {
                byte[] releaseReport = new byte[]{0, 0, 0, 0};
                mHidDevice.sendReport(mHostDevice, 4, releaseReport);
            }, 50);
        }
    }

    // 连接其他蓝牙设备
    @JavascriptInterface
    public void ConnectotherBluetooth() {
        SelectPairedDevices();
        createDia();
        StartScanDevice();
    }

    // 创建设备选择对话框
    protected void createDia() {
        activity.runOnUiThread(() -> {
            Log.d(TAG, "创建设备选择对话框");
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
            View view1 = activity.getLayoutInflater().inflate(R.layout.dialog_bottom, null);
            bottomSheetDialog.setContentView(view1);
            View bottomSheet = bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackgroundColor(Color.TRANSPARENT);

            // 设置一半屏幕高度
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(activity.getResources().getDisplayMetrics().heightPixels / 2);

            mrecyclerView = view1.findViewById(R.id.recycler_view);
            myAdapter = new MyAdapter();
            mrecyclerView.setAdapter(myAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            mrecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration mDivider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            mrecyclerView.addItemDecoration(mDivider);

            bottomSheetDialog.show();
        });
    }

    // 开始扫描设备
    public void StartScanDevice() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!mBtAdapter.isDiscovering()) {
            mBtAdapter.startDiscovery();
        }
        
        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    // 广播接收器
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String str_name = device.getName();
                String str_mac = device.getAddress();

                Log.d(TAG, "发现设备: " + str_name + " Mac: " + str_mac);
                
                // 添加到列表
                if (list_devices_name.indexOf(str_name) == -1 && list_devices_mac.indexOf(str_mac) == -1) {
                    activity.runOnUiThread(() -> {
                        list_devices_name.add(str_name != null ? str_name : "未知设备");
                        list_devices_mac.add(str_mac);
                        list_devices_state.add(false);
                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                activity.runOnUiThread(() -> {
                    webView.loadUrl("javascript:Showinformation('开始扫描蓝牙设备...')");
                });
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                activity.runOnUiThread(() -> {
                    webView.loadUrl("javascript:Showinformation('蓝牙设备扫描完成')");
                });
            }
        }
    };

    // 选择已配对设备
    private void SelectPairedDevices() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        // 清空列表
        list_devices_name.clear();
        list_devices_mac.clear();
        list_devices_state.clear();
        
        // 获取已配对设备
        for (BluetoothDevice device : mBtAdapter.getBondedDevices()) {
            String name = device.getName();
            String mac = device.getAddress();
            list_devices_name.add(name != null ? name : "未知设备");
            list_devices_mac.add(mac);
            list_devices_state.add(true); // 已配对设备标记为true
        }
    }

    // RecyclerView适配器
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Mac;
        TextView State;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.textName);
            Mac = itemView.findViewById(R.id.textMac);
            State = itemView.findViewById(R.id.textState);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.Name.setText(list_devices_name.get(position));
            holder.Mac.setText(list_devices_mac.get(position));
            holder.State.setText(list_devices_state.get(position) ? "已配对" : "未配对");

            // 点击连接
            holder.itemView.setOnClickListener(v -> {
                mac = list_devices_mac.get(position);
                if (mac != null) {
                    mHostDevice = mBtAdapter.getRemoteDevice(mac);
                    if (mHostDevice != null && mHidDevice != null) {
                        Log.d(TAG, "尝试连接设备: " + list_devices_name.get(position));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            mHidDevice.connect(mHostDevice);
                        }
                        activity.runOnUiThread(() -> {
                            webView.loadUrl("javascript:Showinformation('正在连接设备: " + list_devices_name.get(position) + "')");
                        });
                    }
                }
            });

            // 长按显示详细信息
            holder.itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("设备信息")
                       .setMessage("名称: " + list_devices_name.get(position) + "\n" +
                                 "MAC: " + list_devices_mac.get(position) + "\n" +
                                 "状态: " + (list_devices_state.get(position) ? "已配对" : "未配对"))
                       .setPositiveButton("确定", null)
                       .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return list_devices_name.size();
        }
    }

    // 清理资源
    public void cleanup() {
        try {
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "清理资源时出错", e);
        }
        
        if (mHidDevice != null && mHostDevice != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                mHidDevice.disconnect(mHostDevice);
            }
        }
    }

    // 暂停连接
    public void pauseConnection() {
        // 可以在这里添加暂停逻辑
        Log.d(TAG, "暂停鼠标连接");
    }

    // 恢复连接
    public void resumeConnection() {
        // 可以在这里添加恢复逻辑
        Log.d(TAG, "恢复鼠标连接");
    }
} 