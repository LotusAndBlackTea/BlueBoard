package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MouseMainActivity extends AppCompatActivity {
    private static final String TAG = "MouseMain";
    private callMouseBluetooth callMouseBluetooth;
    private WebView webView;
    private MouseJavaScriptInterfaces mouseJavaScriptInterfaces;
    private Vibrators vibrators;
    
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "活动触发后");
                mouseJavaScriptInterfaces.SendResult_pic(result.getResultCode(), result.getData());
            }
    );
    
    private ActivityResultLauncher<Intent> resultLauncher_forbluetooth = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    callMouseBluetooth.CallBluetooth();
                }
            }
    );
    
    private final ActivityResultLauncher<Intent> mRequestLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // 用户已启用设备发现
                } else {
                    this.finish();// 关闭APP
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                           WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mouse_main);

        // 开启蓝牙申请
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            this.finish();// 退出
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mRequestLauncher.launch(enableBt);
            }
        }

        webView = findViewById(R.id.webview);
        this.createWebView(webView);

        Toast.makeText(this, "注意需要打开系统蓝牙", Toast.LENGTH_SHORT).show();
        
        // 通知蓝牙HID管理器当前使用鼠标模式
        BluetoothHidManager.getInstance(this).switchToMouse();
        
        // 初始化JavaScript接口
        mouseJavaScriptInterfaces = new MouseJavaScriptInterfaces(this, webView, resultLauncher);
        webView.addJavascriptInterface(mouseJavaScriptInterfaces, "MouseAndroids");
        
        // 初始化蓝牙鼠标控制
        callMouseBluetooth = new callMouseBluetooth(this, webView, this, this, 
                                                   mRequestLauncher, resultLauncher_forbluetooth);
        webView.addJavascriptInterface(callMouseBluetooth, "mouseBluetooth");
        
        // 初始化震动反馈
        vibrators = new Vibrators(this);
        webView.addJavascriptInterface(vibrators, "MouseVibra");

        Log.d(TAG, "RUN_Start");
        callMouseBluetooth.initMouseMap();
        callMouseBluetooth.mBtManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        callMouseBluetooth.mBtAdapter = callMouseBluetooth.mBtManager.getAdapter();
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        Log.d(TAG, "RUN_End");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    // 申请权限
    public ArrayList<String> requestList = new ArrayList<String>();
    private static final int REQ_PERMISSION_CODE = 1;

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestList.add(android.Manifest.permission.INTERNET);
            requestList.add(android.Manifest.permission.BLUETOOTH);
            requestList.add(android.Manifest.permission.BLUETOOTH_ADMIN);
            requestList.add(android.Manifest.permission.BLUETOOTH_ADVERTISE);
            requestList.add(android.Manifest.permission.BLUETOOTH_SCAN);
            requestList.add(android.Manifest.permission.BLUETOOTH_CONNECT);
            requestList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            requestList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            requestList.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if (requestList.size() != 0) {
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[0]), REQ_PERMISSION_CODE);
        }
    }

    // 创建WebView
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void createWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDefaultTextEncodingName("GBK");
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.addJavascriptInterface(MouseMainActivity.this, "android");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT > 11) {
            webView.getSettings().setDisplayZoomControls(false);
        }

        String url = "file:///android_asset/mouse.html";

        // 直接加载鼠标页面，不需要CSV配置文件
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面加载完成后的处理
                Log.d(TAG, "鼠标页面加载完成");
            }
        });
    }

    public void doClick(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet)
                .setBackgroundColor(Color.TRANSPARENT);
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理鼠标相关的Activity结果
        if (mouseJavaScriptInterfaces != null) {
            mouseJavaScriptInterfaces.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        if (callMouseBluetooth != null) {
            callMouseBluetooth.cleanup();
        }
        Log.d(TAG, "MouseMainActivity资源已清理");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停时断开连接以避免冲突
        if (callMouseBluetooth != null) {
            callMouseBluetooth.pauseConnection();
        }
        Log.d(TAG, "MouseMainActivity已暂停");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复时重新连接
        if (callMouseBluetooth != null) {
            callMouseBluetooth.resumeConnection();
        }
        Log.d(TAG, "MouseMainActivity已恢复");
    }
} 