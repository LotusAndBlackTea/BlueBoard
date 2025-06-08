# 蓝牙键盘鼠标模拟器 (Bluetooth Keyboard & Mouse Emulator)

一个功能完整的Android应用，可以将您的Android设备转换为蓝牙HID键盘和鼠标，用于控制电脑、平板、智能电视等支持蓝牙输入设备的设备。

## 📱 项目概述

本项目基于Android蓝牙HID协议，实现了完整的虚拟键盘和鼠标功能。通过WebView技术提供现代化的用户界面，支持触控操作和实时反馈。

### 🎯 主要功能

- **🎹 虚拟键盘**：完整的QWERTY键盘布局，支持所有标准按键
- **🖱️ 虚拟鼠标**：触控板操作、左右中键点击、滚轮功能
- **📡 蓝牙连接**：自动扫描和连接蓝牙设备
- **🔧 多配置支持**：5种不同的HID描述符配置
- **📳 触觉反馈**：按键震动反馈
- **🌙 主题切换**：支持明暗主题切换

## 🏗️ 项目结构

```
keyboardOFbluetooth-master/
├── app/                                    # 主应用模块
│   ├── src/main/
│   │   ├── java/com/example/myapplication/
│   │   │   ├── HomeActivity.java          # 主页活动（应用入口）
│   │   │   ├── MainActivity.java          # 键盘功能主活动
│   │   │   ├── MouseMainActivity.java     # 鼠标功能主活动
│   │   │   ├── callBluetooth.java         # 键盘蓝牙HID控制核心
│   │   │   ├── callMouseBluetooth.java    # 鼠标蓝牙HID控制核心
│   │   │   ├── HidConfig.java             # 键盘HID配置和描述符
│   │   │   ├── MouseConfig.java           # 鼠标HID配置和描述符
│   │   │   ├── KeyMap.java                # 键盘映射配置
│   │   │   ├── JavaScriptInterfaces.java # 键盘JS接口
│   │   │   ├── MouseJavaScriptInterfaces.java # 鼠标JS接口
│   │   │   ├── MouseController.java       # 鼠标控制器
│   │   │   ├── MouseActivity.java         # 简单鼠标活动
│   │   │   └── Vibrators.java             # 震动反馈控制
│   │   ├── res/                           # 资源文件
│   │   │   ├── layout/                    # 布局文件
│   │   │   │   ├── activity_home.xml      # 主页布局
│   │   │   │   ├── activity_main.xml      # 键盘主界面布局
│   │   │   │   ├── activity_mouse_main.xml # 鼠标主界面布局
│   │   │   │   ├── dialog_bottom.xml      # 底部对话框布局
│   │   │   │   ├── dialog_mac.xml         # MAC地址输入对话框
│   │   │   │   └── item_list.xml          # 设备列表项布局
│   │   │   ├── drawable/                  # 图形资源
│   │   │   ├── mipmap-*/                  # 应用图标
│   │   │   └── values/                    # 值资源
│   │   ├── assets/                        # 静态资源
│   │   │   ├── index.html                 # 键盘主界面
│   │   │   ├── mouse.html                 # 鼠标主界面
│   │   │   ├── css/                       # 样式文件
│   │   │   │   ├── keyboard.css           # 键盘样式
│   │   │   │   ├── mouse.css              # 鼠标样式
│   │   │   │   ├── style.css              # 通用样式
│   │   │   │   └── ...                    # 其他样式文件
│   │   │   ├── js/                        # JavaScript文件
│   │   │   │   ├── keyboard.js            # 键盘逻辑
│   │   │   │   ├── mouse.js               # 鼠标逻辑
│   │   │   │   └── ...                    # 其他JS文件
│   │   │   ├── img/                       # 图片资源
│   │   │   ├── fonts/                     # 字体文件
│   │   │   └── data/                      # 数据文件
│   │   └── AndroidManifest.xml            # 应用清单
│   ├── build.gradle.kts                   # 模块构建配置
│   └── proguard-rules.pro                 # 代码混淆规则
├── python/                                # Python辅助脚本
│   ├── changesvgtoarea.py                 # SVG处理脚本
│   └── text.txt                           # 文本数据
├── gradle/                                # Gradle包装器
├── build.gradle.kts                       # 项目级构建配置
├── settings.gradle.kts                    # 项目设置
├── gradlew                                # Gradle包装器脚本(Unix)
├── gradlew.bat                            # Gradle包装器脚本(Windows)
└── LICENSE                                # 许可证文件
```

## 🔧 技术架构

### 核心技术栈

- **Android SDK**: 原生Android开发
- **WebView**: 混合应用架构
- **Bluetooth HID**: 蓝牙人机接口设备协议
- **JavaScript**: 前端交互逻辑
- **CSS3**: 现代化UI样式

### 架构模式

```
用户操作 → WebView界面 → JavaScript接口 → Java蓝牙处理 → HID协议 → 目标设备
```

### 关键组件

#### 1. 蓝牙HID控制层

- `callBluetooth.java`: 键盘蓝牙HID核心控制
- `callMouseBluetooth.java`: 鼠标蓝牙HID核心控制
- `HidConfig.java`: HID设备配置和描述符
- `MouseConfig.java`: 鼠标HID配置

#### 2. 用户界面层

- `HomeActivity.java`: 应用主入口，功能选择
- `MainActivity.java`: 键盘功能主界面
- `MouseMainActivity.java`: 鼠标功能主界面

#### 3. JavaScript桥接层

- `JavaScriptInterfaces.java`: 键盘JS-Java桥接
- `MouseJavaScriptInterfaces.java`: 鼠标JS-Java桥接

#### 4. 前端界面层

- `index.html` + `keyboard.js`: 键盘界面和逻辑
- `mouse.html` + `mouse.js`: 鼠标界面和逻辑

## 📋 功能特性详解

### 🎹 键盘功能

- **完整键盘布局**: 支持所有标准QWERTY键盘按键
- **组合键支持**: Ctrl+C、Alt+Tab等组合键操作
- **修饰键处理**: 自动处理Shift、Ctrl、Alt等修饰键
- **特殊字符**: 支持符号和特殊字符输入
- **功能键**: F1-F12功能键支持
- **多配置**: 5种不同的HID描述符配置，兼容更多设备

### 🖱️ 鼠标功能

- **触控板操作**: 支持手指滑动控制鼠标移动
- **三键支持**: 左键、右键、中键点击
- **滚轮功能**: 上下滚动支持
- **灵敏度调节**: 可调节鼠标移动灵敏度
- **触觉反馈**: 点击时的震动反馈

### 📡 蓝牙连接

- **自动扫描**: 自动扫描附近的蓝牙设备
- **设备列表**: 显示已配对和可用设备
- **连接管理**: 一键连接和断开
- **状态监控**: 实时显示连接状态

## 🚀 安装和使用

### 系统要求

- Android 6.0 (API 23) 或更高版本
- 支持蓝牙4.0或更高版本
- 目标设备需支持蓝牙HID协议

### 安装步骤

1. **克隆项目**

   ```bash
   git clone https://github.com/LotusAndBlackTea/BlueBoard.git
   cd BlueBoard
   ```

2. **使用Android Studio打开项目**

   - 打开Android Studio
   - 选择"Open an existing Android Studio project"
   - 选择项目根目录

3. **构建和安装**

   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### 使用指南

#### 首次使用

1. **启动应用**: 点击应用图标启动
2. **选择功能**: 在主页选择"虚拟键盘"或"虚拟鼠标"
3. **开启蓝牙**: 确保设备蓝牙已开启
4. **注册HID设备**: 点击蓝牙图标初始化HID设备

#### 连接设备

1. **扫描设备**: 点击连接按钮扫描可用设备
2. **选择目标**: 从设备列表中选择要控制的设备
3. **建立连接**: 点击设备项建立连接
4. **开始使用**: 连接成功后即可开始操作

#### 键盘使用

- **基本输入**: 点击虚拟键盘上的按键
- **组合键**: 同时点击多个修饰键
- **设置调节**: 点击设置图标调整配置

#### 鼠标使用

- **移动鼠标**: 在触控板区域滑动手指
- **点击操作**: 点击左键、右键、中键按钮
- **滚轮操作**: 点击滚轮上下按钮
- **灵敏度**: 在设置中调节移动灵敏度

## 🔧 开发指南

### 环境配置

- **JDK**: 11或更高版本
- **Android Studio**: 最新稳定版
- **Android SDK**: API 33或更高
- **Gradle**: 7.0或更高版本

### 构建命令

```bash
# 清理项目
./gradlew clean

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 运行测试
./gradlew test
```

### 自定义开发

#### 添加新的键盘布局

1. 修改`KeyMap.java`添加新的键位映射
2. 更新`index.html`中的键盘布局
3. 调整`keyboard.css`样式

#### 扩展鼠标功能

1. 在`MouseConfig.java`中定义新的HID描述符
2. 修改`callMouseBluetooth.java`添加新功能
3. 更新`mouse.js`前端逻辑

#### 添加新的HID设备类型

1. 创建新的Config类定义HID描述符
2. 实现对应的蓝牙控制类
3. 添加相应的用户界面

## 🐛 故障排除

### 常见问题

#### 1. 蓝牙连接失败

- **检查权限**: 确保应用已获得蓝牙相关权限
- **重启蓝牙**: 关闭并重新开启设备蓝牙
- **清除缓存**: 清除应用数据和缓存

#### 2. 按键无响应

- **检查连接**: 确认设备已成功连接
- **重新注册**: 重新初始化HID设备
- **兼容性**: 尝试不同的HID配置

#### 3. 鼠标移动不准确

- **调节灵敏度**: 在设置中调整移动灵敏度
- **校准触控**: 重新校准触控板区域
- **检查延迟**: 确保蓝牙连接稳定

### 调试技巧

- 启用WebView调试: 在Chrome中访问`chrome://inspect`
- 查看日志: 使用`adb logcat`查看应用日志
- 蓝牙调试: 在开发者选项中启用蓝牙HCI日志

## 📄 许可证

本项目采用MIT许可证，详见[LICENSE](LICENSE)文件。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 提交Issue: [GitHub Issues](https://github.com/LotusAndBlackTea/BlueBoard/issues)
- 邮箱: 3055779101@qq.com

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和用户！

---

**注意**: 使用本应用时请确保遵守当地法律法规，仅在授权设备上使用。 
