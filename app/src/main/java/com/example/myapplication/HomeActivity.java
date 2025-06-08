package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Button btnVirtualKeyboard;
    private Button btnVirtualMouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        // 初始化按钮
        btnVirtualKeyboard = findViewById(R.id.btnVirtualKeyboard);
        btnVirtualMouse = findViewById(R.id.btnVirtualMouse);

        // 设置虚拟键盘按钮点击事件
        btnVirtualKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用蓝牙HID管理器切换到键盘模式
                BluetoothHidManager.getInstance(HomeActivity.this).switchToKeyboard();
                
                // 跳转到MainActivity（现有的虚拟键盘功能）
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 设置虚拟鼠标按钮点击事件
        btnVirtualMouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用蓝牙HID管理器切换到鼠标模式
                BluetoothHidManager.getInstance(HomeActivity.this).switchToMouse();
                
                // 跳转到MouseMainActivity（完整的鼠标功能）
                Intent intent = new Intent(HomeActivity.this, MouseMainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当返回到主页时，清理可能残留的蓝牙HID状态
        Log.d(TAG, "返回主页，准备清理蓝牙状态");
        
        // 延迟执行清理，确保其他Activity已经完全销毁
        btnVirtualKeyboard.postDelayed(new Runnable() {
            @Override
            public void run() {
                cleanupBluetoothHidState();
            }
        }, 500); // 延迟500ms执行
    }

    /**
     * 清理蓝牙HID状态，解决功能切换冲突问题
     */
    private void cleanupBluetoothHidState() {
        try {
            // 使用蓝牙HID管理器重置状态
            BluetoothHidManager hidManager = BluetoothHidManager.getInstance(this);
            hidManager.resetBluetoothHidState();
            
            Log.d(TAG, "蓝牙HID状态已重置");
            
            // 显示提示信息
            Toast.makeText(this, "蓝牙状态已重置，可以正常切换功能", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "清理蓝牙状态时出错", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeActivity销毁");
    }
} 