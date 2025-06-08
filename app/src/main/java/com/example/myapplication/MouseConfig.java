package com.example.myapplication;

public class MouseConfig {
    public final static String MOUSE_NAME = "VV Mouse";
    public final static String DESCRIPTION = "VV Virtual Mouse";
    public final static String PROVIDER = "VV";

    // 鼠标HID描述符 - 支持3个按键 + 滚轮 + X/Y移动
    public static final byte[] MOUSE_DESCRIPTOR = {
        (byte) 0x05, (byte) 0x01,              // USAGE_PAGE (Generic Desktop)
        (byte) 0x09, (byte) 0x02,              // USAGE (Mouse)
        (byte) 0xa1, (byte) 0x01,              // COLLECTION (Application)
        (byte) 0x85, (byte) 0x04,              // REPORT_ID (4)
        (byte) 0x09, (byte) 0x01,              //  USAGE (Pointer)
        (byte) 0xa1, (byte) 0x00,              //  COLLECTION (Physical)
        (byte) 0x05, (byte) 0x09,              //   USAGE_PAGE (Button)
        (byte) 0x19, (byte) 0x01,              //   USAGE_MINIMUM (Button 1)
        (byte) 0x29, (byte) 0x03,              //   USAGE_MAXIMUM (Button 3)
        (byte) 0x15, (byte) 0x00,              //   LOGICAL_MINIMUM (0)
        (byte) 0x25, (byte) 0x01,              //   LOGICAL_MAXIMUM (1)
        (byte) 0x95, (byte) 0x03,              //   REPORT_COUNT (3)
        (byte) 0x75, (byte) 0x01,              //   REPORT_SIZE (1)
        (byte) 0x81, (byte) 0x02,              //   INPUT (Data,Var,Abs)
        (byte) 0x95, (byte) 0x01,              //   REPORT_COUNT (1)
        (byte) 0x75, (byte) 0x05,              //   REPORT_SIZE (5)
        (byte) 0x81, (byte) 0x03,              //   INPUT (Cnst,Var,Abs)
        (byte) 0x05, (byte) 0x01,              //   USAGE_PAGE (Generic Desktop)
        (byte) 0x09, (byte) 0x30,              //   USAGE (X)
        (byte) 0x09, (byte) 0x31,              //   USAGE (Y)
        (byte) 0x09, (byte) 0x38,              //   USAGE (Wheel)
        (byte) 0x15, (byte) 0x81,              //   LOGICAL_MINIMUM (-127)
        (byte) 0x25, (byte) 0x7F,              //   LOGICAL_MAXIMUM (127)
        (byte) 0x75, (byte) 0x08,              //   REPORT_SIZE (8)
        (byte) 0x95, (byte) 0x03,              //   REPORT_COUNT (3)
        (byte) 0x81, (byte) 0x06,              //   INPUT (Data,Var,Rel)
        (byte) 0xc0,                           //  END_COLLECTION
        (byte) 0xc0,                           // END_COLLECTION
    };

    // 鼠标按键常量
    public static final byte BUTTON_LEFT = 1;
    public static final byte BUTTON_RIGHT = 2;
    public static final byte BUTTON_MIDDLE = 4;

    // 移动灵敏度设置
    public static final float SENSITIVITY_LOW = 0.5f;
    public static final float SENSITIVITY_NORMAL = 1.0f;
    public static final float SENSITIVITY_HIGH = 2.0f;

    // 滚轮设置
    public static final byte SCROLL_UP = -1;
    public static final byte SCROLL_DOWN = 1;
}