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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

// 注册信息
public class callBluetooth {
    private static final String TAG = "BtMain";
    boolean IsRegisted;// 注册状态
    boolean connected;// 连接状态
    private Activity activity;
    private MainActivity mainActivity;
    private KeyMap keyMap;
    private WebView webView;
    int id = 8;
    private String mac;// 当前连接的设备mac地址
    private int count = 0;
    private byte[] mBuffer = new byte[8];

    private static final int CONNECT_SUCCESS = 0x01;
    private static final int CONNECT_FAILURE = 0x02;
    private static final int DISCONNECT_SUCCESS = 0x03;
    private static final int SEND_SUCCESS = 0x04;
    private static final int SEND_FAILURE = 0x05;
    private static final int RECEIVE_SUCCESS = 0x06;
    private static final int RECEIVE_FAILURE = 0x07;
    private static final int START_DISCOVERY = 0x08;
    private static final int STOP_DISCOVERY = 0x09;
    private static final int DISCOVERY_DEVICE = 0x0A;
    private static final int DEVICE_BOND_NONE = 0x0B;
    private static final int DEVICE_BONDING = 0x0C;
    private static final int DEVICE_BONDED = 0x0D;

    List<String> xiushi = Arrays.asList(
            "RIGHT_SHIFT",
            "LEFT_SHIFT",
            "LEFT_CTRL",
            "RIGHT_CTRL",
            "LEFT_ALT",
            "RIGHT_ALT",
            "HOME"
    );

    private BluetoothHidDevice mHidDevice;
    private BluetoothDevice mHostDevice;
    public BluetoothAdapter mBtAdapter;
    public BluetoothManager mBtManager;
    public RecyclerView mrecyclerView;
    public MyAdapter myAdapter;
    private Context context;
    private ActivityResultLauncher<Intent> requestLauncher;
    private ActivityResultLauncher<Intent> requestLauncher_for_bluetooth;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    // 建立设备列表
    private List<String> list_devices_name = new ArrayList<>();// 名称
    private List<String> list_devices_mac = new ArrayList<>();// mac地址
    private List<Boolean> list_devices_state = new ArrayList<>();// 状态

    public int numofcombo = HidConfig.numofCOMBO;// 此时为5
    public int numofchoose = 5;// 选择位数(默认)

    public callBluetooth(MainActivity mainActivity,WebView webView, Context context, Activity activity, ActivityResultLauncher<Intent> requestLauncher, ActivityResultLauncher<Intent> requestLauncher_for_bluetooth) {
        this.webView = webView;
        this.context = context;
        this.activity = activity;
        this.requestLauncher = requestLauncher;
        this.requestLauncher_for_bluetooth = requestLauncher_for_bluetooth;
        this.mainActivity = mainActivity;
    }

    public void initMap() {// 写在一起
        keyMap = new KeyMap();
        keyMap.initHashMap();
        Log.d(TAG, "RUN_Start");
    }

    @JavascriptInterface
    public void createoptions() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:showCombo("+numofcombo+")");
                Log.d(TAG,"num "+numofcombo);
            }
        });
    }

    @JavascriptInterface
    public void getnum(int num) {
        numofchoose = num;
        Log.d(TAG,"choose"+numofchoose);
    }

    // 实例化
    @JavascriptInterface//init
    public void CallBluetooth() {
        //getPermission();
        Log.d(TAG, "callBluetooth");
        // 在这里先初始化并打开蓝牙->见MainActivity.java文件
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBtAdapter == null) {
//            Log.d(TAG, "设备没有蓝牙适配器");
//            activity.finish();
//        }
//        if (mBtAdapter.getState() != BluetoothAdapter.STATE_ON) {
//            // 发起开启蓝牙弹窗
//        }
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return;
//        }
//        if (!mBtAdapter.isDiscovering()) {
//            mBtAdapter.startDiscovery();
//        }

        // 获取BluetoothHidDevice
        mBtAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                Log.d(TAG, "onServiceConnected: " + profile);
                Toast.makeText(context, "Okk_connected_service", Toast.LENGTH_SHORT).show();
                if (profile == BluetoothProfile.HID_DEVICE) {
                    Log.d(TAG, "Proxy received but it isn't hid_OUT");
                    if (!(proxy instanceof BluetoothHidDevice)) {
                        Log.e(TAG, "Proxy received but it isn't hid");
                        return;
                    }
                    Log.d(TAG, "Connecting HID…");
                    mHidDevice = (BluetoothHidDevice) proxy;
                    
                    // 注册到蓝牙HID管理器
                    BluetoothHidManager.getInstance(context).setCurrentHidDevice(mHidDevice);
                    
                    Log.d(TAG, "proxyOK");

                    BluetoothHidDeviceAppSdpSettings Sdpsettings = new BluetoothHidDeviceAppSdpSettings(
                            HidConfig.KEYBOARD_NAME,
                            HidConfig.DESCRIPTION,
                            HidConfig.PROVIDER,
                            BluetoothHidDevice.SUBCLASS1_KEYBOARD,
                            HidConfig.KEYBOARD_COMBO[numofchoose-1]
                    );
                    if (mHidDevice != null) {
                        Toast.makeText(context, "OK for HID profile", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "HID_OK");
                        Log.d(TAG, "Get in register");
                        //getPermission();
                        // 创建一个BluetoothHidDeviceAppSdpSettings对象

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            Log.d(TAG, "return before register");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl("javascript:Showinformation('赋予权限后，你需要再次点击init初始化')");
                                }
                            });
                            String[] list = new String[]{
                                    Manifest.permission.BLUETOOTH_SCAN,
                                    Manifest.permission.BLUETOOTH_CONNECT
                            };
                            requestPermissions(activity, list, 1);
                            return;
                        }
                        BluetoothHidDeviceAppQosSettings inQos = new BluetoothHidDeviceAppQosSettings(
                                BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED, 200, 2, 200,
                                10000 /* 10 ms */, 10000 /* 10 ms */);
                        BluetoothHidDeviceAppQosSettings outQos = new BluetoothHidDeviceAppQosSettings(
                                BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED, 900, 9, 900,
                                10000 /* 10 ms */, 10000 /* 10 ms */);
                        mHidDevice.registerApp(Sdpsettings, null, null, Executors.newCachedThreadPool(), mCallback);
                        // registerApp();// 注册
                    } else {
                        Toast.makeText(context, "Disable for HID profile", Toast.LENGTH_SHORT).show();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadUrl("javascript:Showinformation('手机厂商可能禁用了本机的HID服务')");
                            }
                        });
                    }
                    // 启用设备发现
                    // requestLauncher.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
                    Log.d(TAG, "Discover");
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onServiceDisconnected(int profile) {// 断开连接
                if (profile == BluetoothProfile.HID_DEVICE) {
                    Log.d(TAG, "Unexpected Disconnected: " + profile);
                    mHidDevice = null;
                    mHidDevice.unregisterApp();
                }
            }
        }, BluetoothProfile.HID_DEVICE);
        Toast.makeText(context, "Init Success!", Toast.LENGTH_SHORT).show();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:Showinformation('init success!')");
            }
        });
    }


    public final BluetoothHidDevice.Callback mCallback = new BluetoothHidDevice.Callback() {
        private final int[] mMatchingStates = new int[]{
                BluetoothProfile.STATE_DISCONNECTED,
                BluetoothProfile.STATE_CONNECTING,
                BluetoothProfile.STATE_CONNECTED
        };

        @Override
        public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
            Log.d(TAG, "ccccc_str");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            Log.d(TAG, "onAppStatusChanged: " + (pluggedDevice != null ? pluggedDevice.getName() : "null") + "registered:" + registered);
            // Toast.makeText(context, "onAppStatusChanged", Toast.LENGTH_SHORT).show();
            IsRegisted = registered;
            if (registered) {
                // 应用已注册
                Log.d(TAG, "register OK!.......");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:Showinformation('register OK!成功在本机注册HID服务')");
                    }
                });
                Log.d(TAG, "AFTER WEBVIEWSEND");
            }
        }

        @Override
        public void onConnectionStateChanged(BluetoothDevice device, int state) {
            Log.d(TAG, "onConnectStateChanged:" + device + "  state:" + state);
            // Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
            if (state == BluetoothProfile.STATE_CONNECTED) {// 已经连接了
                connected = true;
                mHostDevice = device;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                Log.d(TAG, "hid state is connected");
                Log.d(TAG, "-----------------------------------connected HID");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:Showinformation('已经连接到了所连接设备的HID服务')");
                    }
                });
                Log.d(TAG, device.getName().toString());
                // Toast.makeText(context, "Connected to " + device.getName().toString(), Toast.LENGTH_SHORT).show();
                // Toast.makeText(context, "device_is_ok: " + mHostDevice.getName() + mHostDevice.getAddress(), Toast.LENGTH_SHORT).show();
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                connected = false;
                Log.d(TAG, "hid state is disconnected");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:Showinformation('所连接设备HID服务断开')");
                    }
                });

                Log.d(TAG, "connect failed");

            } else if (state == BluetoothProfile.STATE_CONNECTING) {
                Log.d(TAG, "hid state is connecting");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:Showinformation('正在尝试连接到所连接设备的HID服务')");
                    }
                });
                // Toast.makeText(context, "Trying connecting...", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void SendBKToHost() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e(TAG, "check permission Error ,Exit SendBKtohost Function");
            String[] list = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

            ActivityCompat.requestPermissions(activity, list, 1);

            return;
        }
        Log.d(TAG, "-----------------preparing send key");
        sendKey("s");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            System.exit(0);
        }
        sendKey("s");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            System.exit(0);
        }
        sendKey("enter");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            System.exit(0);
        }
        sendKey("x");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            System.exit(0);
        }
        sendKey("h");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            System.exit(0);
        }
        sendKey("m");
    }

    // 发送信息
    @JavascriptInterface
    @SuppressLint("MissingPermission")
    public void sendKey(String key) {
        byte b1 = 0;
        byte keyByte = 0;
        Log.d(TAG,key);
        // 修饰键处理
        if (key.contains("+")||xiushi.contains(key)) {
            String[] keys = key.split("\\+");
            for (String k : keys) {
                if (k.equalsIgnoreCase("LEFT_SHIFT")||k.equalsIgnoreCase("RIGHT_SHIFT")){
                    b1 |= 2;
                } else if (k.equalsIgnoreCase("LEFT_CTRL")||k.equalsIgnoreCase("RIGHT_CTRL")) {
                    b1 |= 1;
                } else if (k.equalsIgnoreCase("LEFT_ALT")||k.equalsIgnoreCase("RIGHT_ALT")) {
                    b1 |= 4;
                } else if (k.equalsIgnoreCase("HOME")) {
                    b1 |= 8;
                } else {
                    keyByte = keyMap.KEY2BYTE.get(k.toUpperCase());
                }
            }
        } else {// 其实貌似没啥用
            if (key.length() <= 1) {
                char keychar = key.charAt(0);
                if ((keychar >= 65) && (keychar <= 90)) {
                    b1 = 2;
                }
            }
            if (keyMap.SHITBYTE.containsKey(key)) {
                b1 = 2;
            }
            keyByte = keyMap.KEY2BYTE.get(key.toUpperCase());
        }
        //Log.d(TAG, "pre_send: " + key);
        Log.d(TAG,"b1="+b1);
        Log.d(TAG,"keybyte="+keyByte);

        mHidDevice.sendReport(mHostDevice, 8, new byte[]{
                b1, 0, keyByte, 0, 0, 0, 0, 0
        });
        mHidDevice.sendReport(mHostDevice, 8, new byte[]{
                0, 0, 0, 0, 0, 0, 0, 0
        });
        Log.d(TAG, "after_send: " + key);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:Showinformation('你在本机发送了：" + key + "')");
            }
        });
    }

    @SuppressLint("MissingPermission")
    @JavascriptInterface//connect
    public void ConnectotherBluetooth_temp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setTitle("连接的设备的蓝牙Mac地址：");
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_mac,null);
        // 设置输入框
        final EditText input = view.findViewById(R.id.input);
        input.setText(mac);
        //input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        Button positiveBtn = view.findViewById(R.id.positive_button);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mac = input.getText().toString();
                input.setText(mac);
                if (mac != null) {
                    mHostDevice = mBtAdapter.getRemoteDevice(mac);
                    if (mHostDevice != null) {
                        Log.d(TAG, "Connected is OK");
                        Log.d(TAG, mHostDevice.getName());
                        String finalMac = mac;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadUrl("javascript:Showinformation('你发起连接的设备名：" + mHostDevice.getName() + "  设备Mac地址：" + finalMac + "')");
                            }
                        });
                    }
                    mHidDevice.connect(mHostDevice);
                }
                dialog.dismiss();
            }
        });

        Button negativeBtn = view.findViewById(R.id.negative_button);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*  重新组织连接函数  */
    @JavascriptInterface
    public void ConnectotherBluetooth() {
//        list_devices_name.add("mc1");
//        list_devices_name.add("mc2");
//        list_devices_mac.add("123");
//        list_devices_mac.add("456");
//        list_devices_state.add(true);
//        list_devices_state.add(false);

        /*  处理前端信息  */
        SelectPairedDevices();
        createDia();
        StartScanDevice();
    }

    protected void createDia() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"enter create");
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
                View view1 = activity.getLayoutInflater().inflate(R.layout.dialog_bottom,null);
                bottomSheetDialog.setContentView(view1);
                View bottomSheet =  bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);

                // 设置一半屏幕高度
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(activity.getResources().getDisplayMetrics().heightPixels/2);

                mrecyclerView = view1.findViewById(R.id.recycler_view);
                myAdapter = new MyAdapter();
                mrecyclerView.setAdapter(myAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                mrecyclerView.setLayoutManager(linearLayoutManager);
                DividerItemDecoration mDivider = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
                mrecyclerView.addItemDecoration(mDivider);

                bottomSheetDialog.show();
            }
        });
    }

    public void StartScanDevice() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (!mBtAdapter.isDiscovering()) {
            mBtAdapter.startDiscovery();// 开始扫描
        }
        // 将扫描设备存入列表，进行读取
        //注册Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                String str_name = device.getName();
                String str_mac = device.getAddress();

                Log.d(TAG, "Name: " + str_name + " Mac: " + str_mac);
                // 存入列表
                if (list_devices_name.indexOf(str_name) == -1 && list_devices_mac.indexOf(str_mac) == -1) {// 即不在list中
                    list_devices_name.add(str_name);
                    list_devices_mac.add(str_mac);
                    list_devices_state.add(true);// 蓝牙处于打开状态
                    Log.d(TAG,"连接到新设备");

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }
                else if (list_devices_name.indexOf(str_name) != -1 && list_devices_mac.indexOf(str_mac) != -1) {// 在列表中，此时为配对的设备
                    list_devices_state.set(list_devices_name.indexOf(str_name),true);// 将state置为true
                    Log.d(TAG,"原设备蓝牙打开");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }
                // 后面做前端处理
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.d(TAG, "正在扫描");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:Showinformation('Scanning for Bluetooth Devices...')");
                        Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:Showinformation('Scan finish')");
                        Toast.makeText(context, "Finish,点击列表发起连接...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    /*  处理已经配对的设备  */
    private void SelectPairedDevices() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Set<BluetoothDevice> paired = mBtAdapter.getBondedDevices();
        if (paired.size()>0) {
            for (BluetoothDevice device:paired){
                // 判断是否在蓝牙开启列表中
                String str_name = device.getName();
                String str_mac = device.getAddress();
                // 统一存为false
                if (list_devices_name.indexOf(str_name)==-1) {
                    list_devices_name.add(str_name);
                    list_devices_mac.add(str_mac);
                    list_devices_state.add(false);
                }
            }
        }
    }

    /*  前端处理  */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Mac;
        TextView State;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name=itemView.findViewById(R.id.textName);
            Mac=itemView.findViewById(R.id.textMac);
            State=itemView.findViewById(R.id.textState);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
            View view = View.inflate(context, R.layout.item_list, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position){
            holder.Name.setText(list_devices_name.get(position));
            holder.Mac.setText(list_devices_mac.get(position));
            if (list_devices_state.get(position)==true){
                holder.State.setText("设备蓝牙已开启，可以连接");
            }
            else if (list_devices_state.get(position)==false){
                holder.State.setText("设备蓝牙未开启，已配对");
            }
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                holder.itemView.setBackground(context.getDrawable(R.drawable.ripple_effect));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mac = list_devices_mac.get(position);
                    ConnectotherBluetooth_temp();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list_devices_name.size();
        }
    }

    // Android设备注册为蓝牙设备
    public void sendReport() {
        if (mHidDevice != null && mHostDevice != null) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Bluetooth connect permission is denied", Toast.LENGTH_SHORT).show();
                // TODO: Consider calling
                return;
            }
            byte[] report = new byte[]{0x04};// a
            mHidDevice.sendReport(mHostDevice, 1, report);
            Toast.makeText(context, "has_sent_a", Toast.LENGTH_SHORT).show();
        }
    }


    public void enableBluetooth() {
        Log.d(TAG, "enableBluetooth");
        //getPermission();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            // 设备不支持蓝牙
            Toast.makeText(context, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBtAdapter.isEnabled()) {
                // 如果蓝牙未开启，请求用户开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                requestLauncher.launch(enableBtIntent);
                Log.d(TAG, "OpenBLE");
            } else {
                // 蓝牙已经开启
                Toast.makeText(context, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
                CallBluetooth();
            }
        }
    }

    public boolean isSupportBluetoothHid() {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.bluetooth.IBluetoothHidDevice");
        List<ResolveInfo> results = pm.queryIntentServices(intent, 0);
        if (results == null) {
            return false;
        }
        ComponentName comp = null;
        for (int i = 0; i < results.size(); i++) {
            ResolveInfo ri = results.get(i);
            if ((ri.serviceInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                continue;
            }
            ComponentName foundComp = new ComponentName(ri.serviceInfo.applicationInfo.packageName,
                    ri.serviceInfo.name);

            if (comp != null) {
                throw new IllegalStateException("Multiple system services handle " + this
                        + ": " + comp + ", " + foundComp);
            }
            comp = foundComp;
        }
        return comp != null;
    }


    public void discoverAndPairDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "PairStart");

        // 注册设备发现的广播接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);

        // 开始设备发现
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 发现设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "findDevice");

                // 添加设备到列表
                discoveredDevices.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 设备发现结束，显示设备列表
                showDeviceList();
            }
        }
    };

    private void showDeviceList() {
        // 创建设备名称列表
        List<String> deviceNames = new ArrayList<>();
        for (BluetoothDevice device : discoveredDevices) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            deviceNames.add(device.getName());
        }
        Log.d(TAG, "Show");
        // 创建并显示 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a device")
                .setItems(deviceNames.toArray(new String[0]), (dialog, which) -> {
                    // 用户选择了一个设备，进行配对操作
                    BluetoothDevice selectedDevice = discoveredDevices.get(which);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    if (selectedDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        selectedDevice.createBond();
                    }
                })
                .show();
    }

    ;


    // 以下为废稿
    private void registerApp() {
        Log.d(TAG, "Get in register");
        //getPermission();
        // 创建一个BluetoothHidDeviceAppSdpSettings对象
        BluetoothHidDeviceAppSdpSettings Sdpsettings = new BluetoothHidDeviceAppSdpSettings(
                HidConfig.KEYBOARD_NAME,
                HidConfig.DESCRIPTION,
                HidConfig.PROVIDER,
                BluetoothHidDevice.SUBCLASS1_KEYBOARD,
                HidConfig.KEYBOARD_COMBO5
        );

        // 创建一个BluetoothHidDeviceAppQosSettings对象，随机设置的(
        BluetoothHidDeviceAppQosSettings qosSettings = new BluetoothHidDeviceAppQosSettings(
                BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                800, 9, 0, 11250, BluetoothHidDeviceAppQosSettings.MAX);
//        BluetoothHidDeviceAppQosSettings qosOut = new BluetoothHidDeviceAppQosSettings(
//                BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED,
//                0, 0, 0, 0, BluetoothHidDeviceAppQosSettings.MAX);

        // 注册应用
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Log.d(TAG, "Before callback");
        mHidDevice.registerApp(Sdpsettings, null, qosSettings, Executors.newCachedThreadPool(), new BluetoothHidDevice.Callback() {
            private final int[] mMatchingStates = new int[]{
                    BluetoothProfile.STATE_DISCONNECTED,
                    BluetoothProfile.STATE_CONNECTING,
                    BluetoothProfile.STATE_CONNECTED,
                    BluetoothProfile.STATE_DISCONNECTED
            };

            @Override
            public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                }
                Log.d(TAG, "ccccc_str");
                Log.d(TAG, "onAppStatusChanged: " + (pluggedDevice != null ? pluggedDevice.getName() : "null") + "registered:" + registered);
                Toast.makeText(context, "onAppStatusChanged", Toast.LENGTH_SHORT).show();
                if (registered) {
                    // 应用已注册
                    List<BluetoothDevice> matchingDevices = mHidDevice.getDevicesMatchingConnectionStates(mMatchingStates);
                    Log.d(TAG, "paired devices: " + matchingDevices + "  " + mHidDevice.getConnectionState(pluggedDevice));
                    Toast.makeText(context, "paired devices: " + matchingDevices + "  " + mHidDevice.getConnectionState(pluggedDevice), Toast.LENGTH_SHORT).show();
                    if (pluggedDevice != null && mHidDevice.getConnectionState(pluggedDevice) != BluetoothProfile.STATE_CONNECTED) {
                        boolean result = mHidDevice.connect(pluggedDevice);// pluggedDevice即为连接到模拟HID的设备
                        Log.d(TAG, "hidDevice connect:" + result);
                        Toast.makeText(context, "hidDevice connect:" + result, Toast.LENGTH_SHORT).show();
                    } else if (matchingDevices != null && matchingDevices.size() > 0) {
                        // 选择连接的设备
                        mHostDevice = matchingDevices.get(0);// 获得第一个已经配对过的设备
                        Toast.makeText(context, "device_is_ok: " + mHostDevice.getName() + mHostDevice.getAddress(), Toast.LENGTH_SHORT).show();
                    } else {
                        // 注册成功未配对
                    }
                } else {
                    // 应用未注册
                }
            }

            @Override
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                Log.d(TAG, "omVonnectStateChanged:" + device + "  state:" + state);
                Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
                if (state == BluetoothProfile.STATE_CONNECTED) {// 已经连接了
                    mHostDevice = device;
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    Toast.makeText(context, "device_is_ok: " + mHostDevice.getName() + mHostDevice.getAddress(), Toast.LENGTH_SHORT).show();
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    mHostDevice = null;
                    Toast.makeText(context, "device_is_null", Toast.LENGTH_SHORT).show();
                } else if (state == BluetoothProfile.STATE_CONNECTING) {

                }
            }
        });
    }

    // 清理资源
    public void cleanup() {
        try {
            // 注销广播接收器
            if (broadcastReceiver != null) {
                try {
                    context.unregisterReceiver(broadcastReceiver);
                } catch (IllegalArgumentException e) {
                    // 广播接收器可能已经注销
                    Log.d(TAG, "广播接收器已经注销");
                }
            }
            
            if (receiver != null) {
                try {
                    context.unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    // 广播接收器可能已经注销
                    Log.d(TAG, "设备发现广播接收器已经注销");
                }
            }
            
            // 断开HID连接
            if (mHidDevice != null && mHostDevice != null) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    mHidDevice.disconnect(mHostDevice);
                    Log.d(TAG, "断开HID设备连接");
                }
            }
            
            // 注销HID应用
            if (mHidDevice != null) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    mHidDevice.unregisterApp();
                    Log.d(TAG, "注销HID应用");
                }
            }
            
            // 停止设备发现
            if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                    mBtAdapter.cancelDiscovery();
                    Log.d(TAG, "停止设备发现");
                }
            }
            
            // 清空设备列表
            list_devices_name.clear();
            list_devices_mac.clear();
            list_devices_state.clear();
            discoveredDevices.clear();
            
            // 重置状态
            IsRegisted = false;
            connected = false;
            mHostDevice = null;
            
            Log.d(TAG, "键盘蓝牙资源清理完成");
            
        } catch (Exception e) {
            Log.e(TAG, "清理资源时出错", e);
        }
    }

    // 暂停连接
    public void pauseConnection() {
        try {
            if (mHidDevice != null && mHostDevice != null && connected) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    // 发送空报告清除按键状态
                    byte[] emptyReport = new byte[8];
                    mHidDevice.sendReport(mHostDevice, id, emptyReport);
                    Log.d(TAG, "暂停键盘连接，清除按键状态");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "暂停连接时出错", e);
        }
    }

    // 恢复连接
    public void resumeConnection() {
        try {
            // 重新初始化键盘映射
            if (keyMap == null) {
                initMap();
            }
            Log.d(TAG, "恢复键盘连接");
        } catch (Exception e) {
            Log.e(TAG, "恢复连接时出错", e);
        }
    }
}