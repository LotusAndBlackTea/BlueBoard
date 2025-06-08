package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;

public class MouseJavaScriptInterfaces {
    private static final String TAG = "MouseJSInterface";
    private Activity activity;
    private WebView webView;
    private ActivityResultLauncher<Intent> resultLauncher;

    public MouseJavaScriptInterfaces(Activity activity, WebView webView, ActivityResultLauncher<Intent> resultLauncher) {
        this.activity = activity;
        this.webView = webView;
        this.resultLauncher = resultLauncher;
    }

    @JavascriptInterface
    public void showToast(String message) {
        activity.runOnUiThread(() -> {
            android.widget.Toast.makeText(activity, message, android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @JavascriptInterface
    public void vibrate(int duration) {
        // 调用震动功能
        try {
            android.os.Vibrator vibrator = (android.os.Vibrator) activity.getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(duration);
            }
        } catch (Exception e) {
            Log.e(TAG, "震动失败", e);
        }
    }

    @JavascriptInterface
    public void logMessage(String message) {
        Log.d(TAG, "来自JavaScript的消息: " + message);
    }

    @JavascriptInterface
    public void updateStatus(String status) {
        activity.runOnUiThread(() -> {
            webView.loadUrl("javascript:updateStatusDisplay('" + status + "')");
        });
    }

    // 处理Activity结果
    public void SendResult_pic(int resultCode, Intent data) {
        // 处理图片相关的结果
        Log.d(TAG, "处理Activity结果: " + resultCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 处理其他Activity结果
        Log.d(TAG, "onActivityResult: " + requestCode + ", " + resultCode);
    }
} 