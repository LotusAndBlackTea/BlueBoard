// 鼠标控制JavaScript

let isMouseDown = false;
let lastX = 0;
let lastY = 0;
let sensitivity = 1.0;
let isConnected = false;

// 初始化鼠标功能
document.addEventListener('DOMContentLoaded', function() {
    initMousePad();
    initMouseButtons();
    initScrollArea();
    setupTouchEvents();
});

// 初始化鼠标触控板
function initMousePad() {
    const mouseArea = document.getElementById('mouseArea');
    if (!mouseArea) return;

    // 鼠标移动事件
    mouseArea.addEventListener('mousemove', handleMouseMove);
    mouseArea.addEventListener('mousedown', handleMouseDown);
    mouseArea.addEventListener('mouseup', handleMouseUp);
    
    // 触摸事件
    mouseArea.addEventListener('touchstart', handleTouchStart);
    mouseArea.addEventListener('touchmove', handleTouchMove);
    mouseArea.addEventListener('touchend', handleTouchEnd);
    
    // 防止默认行为
    mouseArea.addEventListener('contextmenu', e => e.preventDefault());
}

// 初始化鼠标按键
function initMouseButtons() {
    const leftClick = document.getElementById('leftClick');
    const rightClick = document.getElementById('rightClick');
    const middleClick = document.getElementById('middleClick');

    if (leftClick) {
        leftClick.addEventListener('click', () => sendMouseClick('left_click'));
        leftClick.addEventListener('touchstart', (e) => {
            e.preventDefault();
            sendMouseClick('left_click');
        });
    }

    if (rightClick) {
        rightClick.addEventListener('click', () => sendMouseClick('right_click'));
        rightClick.addEventListener('touchstart', (e) => {
            e.preventDefault();
            sendMouseClick('right_click');
        });
    }

    if (middleClick) {
        middleClick.addEventListener('click', () => sendMouseClick('middle_click'));
        middleClick.addEventListener('touchstart', (e) => {
            e.preventDefault();
            sendMouseClick('middle_click');
        });
    }
}

// 初始化滚轮区域
function initScrollArea() {
    const scrollArea = document.getElementById('scrollArea');
    if (!scrollArea) return;

    const scrollUp = scrollArea.querySelector('.scrollUp');
    const scrollDown = scrollArea.querySelector('.scrollDown');

    if (scrollUp) {
        scrollUp.addEventListener('click', () => sendMouseScroll('scroll_up'));
        scrollUp.addEventListener('touchstart', (e) => {
            e.preventDefault();
            sendMouseScroll('scroll_up');
        });
    }

    if (scrollDown) {
        scrollDown.addEventListener('click', () => sendMouseScroll('scroll_down'));
        scrollDown.addEventListener('touchstart', (e) => {
            e.preventDefault();
            sendMouseScroll('scroll_down');
        });
    }
}

// 处理鼠标移动
function handleMouseMove(event) {
    if (!isMouseDown) return;
    
    const deltaX = (event.clientX - lastX) * sensitivity;
    const deltaY = (event.clientY - lastY) * sensitivity;
    
    sendMouseMove(Math.round(deltaX), Math.round(deltaY));
    
    lastX = event.clientX;
    lastY = event.clientY;
}

// 处理鼠标按下
function handleMouseDown(event) {
    isMouseDown = true;
    lastX = event.clientX;
    lastY = event.clientY;
    event.preventDefault();
}

// 处理鼠标释放
function handleMouseUp(event) {
    isMouseDown = false;
    event.preventDefault();
}

// 处理触摸开始
function handleTouchStart(event) {
    if (event.touches.length === 1) {
        isMouseDown = true;
        const touch = event.touches[0];
        lastX = touch.clientX;
        lastY = touch.clientY;
    }
    event.preventDefault();
}

// 处理触摸移动
function handleTouchMove(event) {
    if (!isMouseDown || event.touches.length !== 1) return;
    
    const touch = event.touches[0];
    const deltaX = (touch.clientX - lastX) * sensitivity;
    const deltaY = (touch.clientY - lastY) * sensitivity;
    
    sendMouseMove(Math.round(deltaX), Math.round(deltaY));
    
    lastX = touch.clientX;
    lastY = touch.clientY;
    event.preventDefault();
}

// 处理触摸结束
function handleTouchEnd(event) {
    isMouseDown = false;
    event.preventDefault();
}

// 发送鼠标移动
function sendMouseMove(deltaX, deltaY) {
    if (typeof mouseBluetooth !== 'undefined') {
        mouseBluetooth.sendMouseData('move', deltaX, deltaY);
    } else {
        console.log('鼠标移动:', deltaX, deltaY);
    }
}

// 发送鼠标点击
function sendMouseClick(button) {
    if (typeof mouseBluetooth !== 'undefined') {
        mouseBluetooth.sendMouseData(button, 0, 0);
        // 震动反馈
        if (typeof MouseVibra !== 'undefined') {
            MouseVibra.vibrate(50);
        }
    } else {
        console.log('鼠标点击:', button);
    }
    
    // 视觉反馈
    showClickFeedback(button);
}

// 发送鼠标滚轮
function sendMouseScroll(direction) {
    if (typeof mouseBluetooth !== 'undefined') {
        mouseBluetooth.sendMouseData(direction, 0, 0);
        // 震动反馈
        if (typeof MouseVibra !== 'undefined') {
            MouseVibra.vibrate(30);
        }
    } else {
        console.log('鼠标滚轮:', direction);
    }
}

// 显示点击反馈
function showClickFeedback(button) {
    const buttonElement = document.getElementById(button.replace('_click', 'Click'));
    if (buttonElement) {
        buttonElement.classList.add('clicked');
        setTimeout(() => {
            buttonElement.classList.remove('clicked');
        }, 150);
    }
}

// 设置灵敏度
function setSensitivity(value) {
    sensitivity = value;
    Showinformation('鼠标灵敏度设置为: ' + value);
}

// 蓝牙相关函数
function SETBluetooth() {
    if (typeof mouseBluetooth !== 'undefined') {
        mouseBluetooth.CallBluetooth();
    }
}

function SETBluetooth_forbar() {
    if (typeof mouseBluetooth !== 'undefined') {
        mouseBluetooth.ConnectotherBluetooth();
    }
}

// 显示信息
function Showinformation(message) {
    const info = document.getElementById('info');
    if (info) {
        info.innerHTML = '<p>' + message + '</p>';
        
        // 3秒后清除信息
        setTimeout(() => {
            info.innerHTML = '<p>Waiting informations...</p>';
        }, 3000);
    }
    console.log('Info:', message);
}

// 更新状态显示
function updateStatusDisplay(status) {
    Showinformation(status);
}

// 处理CSV数据（如果需要）
function processMouseCSVData(csvData) {
    console.log('处理鼠标CSV数据:', csvData);
    // 可以在这里处理鼠标配置数据
}

