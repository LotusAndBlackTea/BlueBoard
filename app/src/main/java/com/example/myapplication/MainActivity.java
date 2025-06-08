package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import com.yalantis.ucrop.UCrop;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BtMain";
    private callBluetooth callBluetooth;
    private WebView webView;
    private JavaScriptInterfaces javaScriptInterfaces;
    private Vibrators vibrators;
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG,"活动触发后");
                javaScriptInterfaces.SendResult_pic(result.getResultCode(),result.getData());
            }
    );
    private ActivityResultLauncher<Intent> resultLauncher_forbluetooth = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    callBluetooth.CallBluetooth();
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // 开启蓝牙申请
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null){
            this.finish();// 退出
        } else {
            if (!bluetoothAdapter.isEnabled()){
                Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mRequestLauncher.launch(enableBt);
            }
        }

        webView = (WebView) findViewById(R.id.webview);
        this.createWebView(webView);

        // 蓝牙：目前有问题暂时不解决
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 0);
//        }
        Toast.makeText(this, "注意需要打开系统蓝牙", Toast.LENGTH_SHORT).show();
        
        // 通知蓝牙HID管理器当前使用键盘模式
        BluetoothHidManager.getInstance(this).switchToKeyboard();
        
        javaScriptInterfaces = new JavaScriptInterfaces(this,webView,resultLauncher);
        webView.addJavascriptInterface(javaScriptInterfaces,"Androids");
        callBluetooth = new callBluetooth(this,webView,this,this,mRequestLauncher,resultLauncher_forbluetooth);
        webView.addJavascriptInterface(callBluetooth,"bluetooth");
        vibrators = new Vibrators(this);
        webView.addJavascriptInterface(vibrators,"Vibra");
//        callBluetooth.CallBluetooth();
//        callBluetooth.enableBluetooth();// 启动蓝牙
        Log.d(TAG, "RUN_Start");
        callBluetooth.initMap();
        callBluetooth.mBtManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        callBluetooth.mBtAdapter = callBluetooth.mBtManager.getAdapter();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // discoverAndPairDevice();
//        Log.d(TAG,"outENABLE enterCALL");
//        callBluetooth.CallBluetooth();
        Log.d(TAG, "RUN_End");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 申请权限
    public ArrayList<String> requestList = new ArrayList<String>();
    private static final int REQ_PERMISSION_CODE = 1;

    public void getPermission() {// 暂时先不用
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
        webView.getSettings().setDefaultTextEncodingName("GBK");//设置字符编码
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.addJavascriptInterface(MainActivity.this,"android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT > 11) {
            webView.getSettings().setDisplayZoomControls(false);
        }
        // webView.setWebViewClient(new WebViewClient(){});
        String url = "file:///android_asset/index.html";

        try (
                InputStream inputStream = getAssets().open("data/position.csv");// initial
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            StringBuilder csvData = new StringBuilder();
            String line;
            String jsScript;

            while ((line = reader.readLine()) != null) {
                // 转义单引号
                line = line.replace("'", "\\'");
                csvData.append(line).append("\\n"); // 使用双反斜杠来表示换行，以便在JavaScript中正确解析
            }
            // 将换行符转换为字符串字面量
            jsScript = "javascript:processCSVData('" + csvData.toString() + "')";
            webView.loadUrl(url);
            // 确保WebView已经准备好接收JavaScript代码
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    webView.evaluateJavascript(jsScript, null);
                }
            });
//            if (webView != null) {
//                webView.evaluateJavascript(jsScript, null);
//            }
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }

    }

    public void doClick(View view){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_bottom,null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            javaScriptInterfaces.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理蓝牙资源
        if (callBluetooth != null) {
            callBluetooth.cleanup();
        }
        Log.d(TAG, "MainActivity资源已清理");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停时断开连接以避免冲突
        if (callBluetooth != null) {
            callBluetooth.pauseConnection();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复时重新初始化
        if (callBluetooth != null) {
            callBluetooth.resumeConnection();
        }
    }
}
