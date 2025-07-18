let laslclick = 0;

function showCombo(num) {
    // 创建背景div
    let div_back = document.createElement('div');
    div_back.setAttribute('class', 'modal2');
    div_back.style.position = 'fixed';
    div_back.style.width = "100%";
    div_back.style.height = "100%";
    div_back.style.left = "0";
    div_back.style.top = "0";
    div_back.style.backgroundColor = "rgba(0,0,0,0.5)";
    div_back.onclick = function () { // 点击背景div时，移除背景div
        div_back.style.opacity = "0";
        // 使用setTimeout来延迟移除div_back，以确保消失的平滑效果可以完全显示
        setTimeout(function () {
            document.body.removeChild(div_back);
        }, 500); // 500毫秒是transition的持续时间
    }

    // 创建内容div
    let div_contain = document.createElement('div');
    div_contain.className = "Contain2";
    div_contain.style.backgroundColor = "#fff";
    div_contain.style.position = "absolute";
    div_contain.style.left = "25%";
    div_contain.style.right = "25%";
    div_contain.style.bottom = "25%";
    div_contain.style.top = "25%";
    div_contain.style.margin = "auto";
    div_contain.style.boxSizing = "border-box";
    div_contain.style.display = "flex"; // 纵向排列
    div_contain.style.flexDirection = "column"; // 纵向排列
    div_contain.style.alignItems = "center"; // 纵向排列
    div_contain.style.justifyContent = "center"; // 纵向排列
    div_back.style.borderRadius = '10px';
    div_back.style.transition = "opacity 0.5s"; // 添加平滑效果
    div_back.style.opacity = "0"; // 初始透明度为0
    div_contain.style.borderRadius = '10px';
    div_contain.style.transition = "opacity 0.5s"; // 添加平滑效果

    // 添加选项
    for (let i = 1; i <= num; i++) {
        let a = document.createElement('a');
        a.href = '#';
        a.textContent = 'KEYCOMBO ' + i;
        a.className = i;
        if (a.className === laslclick) {
            a.style.color = 'blue';
        }
        a.style.textDecoration = "none"; // 去除下划线
        a.style.fontWeight = "bold"; // 字体加粗
        a.style.fontSize = '35px';
        a.style.lineHeight = '50px';
        a.onclick = function () { // 点击选项时，移除背景div
            laslclick = a.className;
            bluetooth.getnum(parseInt(laslclick));
            div_back.style.opacity = "0";
            // 使用setTimeout来延迟移除div_back，以确保消失的平滑效果可以完全显示
            setTimeout(function () {
                document.body.removeChild(div_back);
            }, 500); // 500毫秒是transition的持续时间
        }
        div_contain.appendChild(a);
    }
    setTimeout(function() {
        div_back.style.opacity = "1";
    }, 0);
    // 将内容div添加到背景div
    div_back.appendChild(div_contain);

    // 将背景div添加到body
    document.body.appendChild(div_back);
}

function showCheck() {
    let div_back = document.createElement('div');
    div_back.setAttribute('class', 'modal');
    div_back.style.position = 'fixed';
    div_back.style.width = "100%";
    div_back.style.height = "100%";
    div_back.style.left = "0";
    div_back.style.top = "0";
    div_back.style.backgroundColor = "rgba(0,0,0,0.5)";
    let div_contain = document.createElement('div');
    div_contain.className = "Contain";
    div_contain.innerHTML += "<a href='#' onclick='Androids.getImage()'>更换键盘背景</a><a href='#' onclick='Androids.getPosition()'>添加键位文件</a><a href='#' onclick='bluetooth.createoptions()'>描述符切换</a><a href='#' onclick='reset()'>恢复默认</a><a href='./html/appinfo.html'>APP INFO</a>";
    if (window.innerHeight <= 450) {
        div_contain.style.width = "250px";
        div_contain.style.height = "300px";
        div_contain.style.backgroundColor = "#fff";
        div_contain.style.position = "absolute";
        div_contain.style.left = "0";
        div_contain.style.right = "0";
        div_contain.style.bottom = "0";
        div_contain.style.top = "0";
        div_contain.style.margin = "auto";
        div_contain.style.boxSizing = "border-box";
        div_contain.style.borderRadius = "5px";
        div_contain.style.padding = "1em";
        div_contain.style.display = "flex";
        div_contain.style.flexDirection = "column";
        div_contain.style.fontSize = "15px";
    }
    else {
        div_contain.style.width = "404px";
        div_contain.style.height = "512px";
        div_contain.style.backgroundColor = "#fff";
        div_contain.style.position = "absolute";
        div_contain.style.left = "0";
        div_contain.style.right = "0";
        div_contain.style.bottom = "0";
        div_contain.style.top = "0";
        div_contain.style.margin = "auto";
        div_contain.style.boxSizing = "border-box";
        div_contain.style.borderRadius = "6px";
        div_contain.style.padding = "1em";
        div_contain.style.display = "flex";
        div_contain.style.flexDirection = "column";
        div_contain.style.fontSize = "25px";
    }
    let close = document.createElement('div');
    if (window.innerHeight <= 450) {
        close.innerHTML += "×";
        close.className = "close";
        close.style.width = "20px";
        close.style.height = "20px";
        close.style.borderRadius = "50%";
        close.style.position = "absolute";
        close.style.right = "-10px";
        close.style.top = "-10px";
        close.style.border = "2px solid #fff";
        close.style.cursor = "pointer";
        close.style.backgroundColor = "#008c8c";
        close.style.textAlign = "center";
        close.style.lineHeight = "20px";
        close.style.color = "#ffffff";
        close.style.fontSize = "15px";
        close.style.fontWeight = "bold";
    } else {
        close.innerHTML += "×";
        close.className = "close";
        close.style.width = "30px";
        close.style.height = "30px";
        close.style.borderRadius = "50%";
        close.style.position = "absolute";
        close.style.right = "-10px";
        close.style.top = "-10px";
        close.style.border = "2px solid #fff";
        close.style.cursor = "pointer";
        close.style.backgroundColor = "#008c8c";
        close.style.textAlign = "center";
        close.style.lineHeight = "30px";
        close.style.color = "#ffffff";
        close.style.fontSize = "20px";
        close.style.fontWeight = "bold";
    }
    div_contain.appendChild(close);
    div_back.appendChild(div_contain);
    div_back.style.animation += "popup 0.5s";
    document.body.appendChild(div_back);
    let contexts = document.querySelectorAll('.Contain a');
    for (let i = 0; i < contexts.length; i++) {
        contexts[i].style.textDecoration = "none";
        contexts[i].style.color = "black";
        contexts[i].style.fontWeight = "bold";
        contexts[i].style.borderBottom = "1px solid #d3d3d3";
        contexts[i].style.textAlign = "center";
        contexts[i].style.lineHeight = "2";
    }
    close.addEventListener('click', function () {
        div_back.style.animation = "popdown 0.5s";
        setTimeout(function () {
            document.body.removeChild(div_back);
        }, 500);
    });
}

function delayedRedirect(url, delay) {
    setTimeout(function () {
        window.location.href = url;
    }, delay);
}

function changepic() {
    let pict = document.querySelector('img.sunANDmoon');
    let pict2 = document.querySelector('img.sunANDmoon_intouchbar');
    pict.style.opacity = 0;
    pict2.style.opacity = 0;
    setTimeout(function () {
        if (pict.getAttribute('src') === "./img/sun.svg") {
            pict.setAttribute('src', './img/moon.svg');
            pict2.setAttribute('src', './img/moon.svg');
            changeBODY_black();
        }
        else {
            pict.setAttribute('src', './img/sun.svg');
            pict2.setAttribute('src', './img/sun.svg');
            changeBODY_ori();
        }
        pict.style.opacity = 1;
        pict2.style.opacity = 1;
    }, 300);
}

function changeBODY_black() {
    document.body.style.backgroundColor = "black";
    let imag = document.querySelector("img.Img");
    // imag.style.border = "2px solid wheat";
    imag.setAttribute('src', "./img/svg_dark.svg");
}

function changeBODY_ori() {
    document.body.style.backgroundColor = '';
    let imag = document.querySelector("img.Img");
    // imag.style.border = "2px solid orangered";
    imag.setAttribute('src', "./img/svg.svg");
}

let count = 0;
let count2 = 0;

function StartBluetooth() {
    let IMGF = document.querySelector("img.Init");
    IMGF.style.transform = 'rotate(360deg)';
    if (count === 0) {
        let newButton = document.createElement("button");
        let newButton2 = document.createElement("button");
        newButton.textContent = "Init"; // 设置按钮的文本
        newButton.className = "ButtonLeft";
        newButton2.textContent = "Connect"; // 设置按钮的文本
        newButton2.className = "ButtonRight";
        newButton.style.animation = 'slideLeft 1s forwards'; // 设置按钮的动画
        newButton2.style.animation = 'slideRight 1s forwards'; // 设置按钮的动画
        // 设置样式
        newButton.style.border = "3px solid gray";
        newButton.style.background = 'repeating-linear-gradient(135deg, transparent, transparent 10px, bisque 10px, bisque 12px)';
        newButton.style.borderRadius = '10px';
        newButton.style.fontWeight = "bold";
        newButton.style.fontSize = '20px';
        newButton2.style.fontWeight = 'bold';
        newButton2.style.border = '3px solid gray';
        newButton2.style.background = 'repeating-linear-gradient(135deg, transparent, transparent 10px, bisque 10px, bisque 12px)';
        newButton2.style.borderRadius = '10px';
        newButton2.style.fontSize = '20px';
        newButton.onclick = function () {
            bluetooth.CallBluetooth();
        }
        newButton2.onclick = function () {
            bluetooth.ConnectotherBluetooth();
        }
        // 将新的按钮元素添加到图片元素的父元素中
        IMGF.parentNode.insertBefore(newButton, IMGF);
        IMGF.parentNode.insertBefore(newButton2, IMGF.nextSibling);
        count = 1;
        count2 = 0;
    }
}

function RemoveBluetooth() {
    let IMGF = document.querySelector('img.Init');
    IMGF.style.transform = 'rotate(0deg)';
    if (count2 === 0) {
        let ButtonLeft = document.querySelector(".ButtonLeft");
        let ButtonRight = document.querySelector(".ButtonRight");
        ButtonLeft.style.animation = 'slideLeft_re 1s forwards';
        ButtonRight.style.animation = 'slideRight_re 1s forwards';

        // 在动画结束后删除按钮
        setTimeout(
            function () {
                ButtonLeft.parentNode.removeChild(ButtonLeft);
                ButtonRight.parentNode.removeChild(ButtonRight);
            }, 1000
        );
        count2 = 1;
        count = 0;// 恢复
    }
}

var Rem = 0;
function SETBluetooth() {
    if (Rem === 0) {
        StartBluetooth();
        Rem = 1;
        return;
    }
    else if (Rem === 1) {
        RemoveBluetooth();
        Rem = 0;
        return;
    }
}
