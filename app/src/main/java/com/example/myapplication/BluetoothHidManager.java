package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;

/**
 * 蓝牙HID设备管理器
 * 用于解决键盘和鼠标功能切换时的冲突问题
 */
public class BluetoothHidManager {
    private static final String TAG = "BluetoothHidManager";
    private static BluetoothHidManager instance;
    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothHidDevice currentHidDevice;
    private String currentDeviceType; // "keyboard" 或 "mouse"
    
    private BluetoothHidManager(Context context) {
        this.context = context.getApplicationContext();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
    }
    
    public static synchronized BluetoothHidManager getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothHidManager(context);
        }
        return instance;
    }
    
    /**
     * 切换到键盘模式
     */
    public void switchToKeyboard() {
        Log.d(TAG, "切换到键盘模式");
        if ("mouse".equals(currentDeviceType)) {
            cleanupCurrentDevice();
        }
        currentDeviceType = "keyboard";
    }
    
    /**
     * 切换到鼠标模式
     */
    public void switchToMouse() {
        Log.d(TAG, "切换到鼠标模式");
        if ("keyboard".equals(currentDeviceType)) {
            cleanupCurrentDevice();
        }
        currentDeviceType = "mouse";
    }
    
    /**
     * 清理当前HID设备
     */
    public void cleanupCurrentDevice() {
        try {
            if (currentHidDevice != null) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    // 注销当前HID应用
                    currentHidDevice.unregisterApp();
                    Log.d(TAG, "已注销当前HID设备: " + currentDeviceType);
                }
                currentHidDevice = null;
            }
            
            // 短暂延迟，让系统有时间处理注销
            Thread.sleep(200);
            
        } catch (Exception e) {
            Log.e(TAG, "清理HID设备时出错", e);
        }
    }
    
    /**
     * 设置当前HID设备
     */
    public void setCurrentHidDevice(BluetoothHidDevice hidDevice) {
        this.currentHidDevice = hidDevice;
    }
    
    /**
     * 获取当前设备类型
     */
    public String getCurrentDeviceType() {
        return currentDeviceType;
    }
    
    /**
     * 检查是否可以切换到指定类型
     */
    public boolean canSwitchTo(String deviceType) {
        return !deviceType.equals(currentDeviceType);
    }
    
    /**
     * 完全重置蓝牙HID状态
     */
    public void resetBluetoothHidState() {
        try {
            cleanupCurrentDevice();
            currentDeviceType = null;
            
            // 如果需要，可以重启蓝牙适配器
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "蓝牙HID状态已重置");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "重置蓝牙HID状态时出错", e);
        }
    }
    
    /**
     * 获取蓝牙适配器
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
} 