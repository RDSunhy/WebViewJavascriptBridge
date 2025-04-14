# WebViewJavascriptBridge

> [English README](https://github.com/RDSunhy/WebViewJavascriptBridge/blob/main/README_EN.md) | [更新日志](https://github.com/RDSunhy/WebViewJavascriptBridge/blob/main/CHANGELOG.md)

参考 [WKWebViewJavascriptBridge](https://github.com/Lision/WKWebViewJavascriptBridge) 实现的 Android 平台 Js 桥接库，尽可能保持了和 [WKWebViewJavascriptBridge](https://github.com/Lision/WKWebViewJavascriptBridge) 相同的 api，支持自定义桥接名称，并且没有与 WebView 强行耦合，更像是对 WebView 能力的增强。



# 引入

```kotlin
repositories {
    mavenCentral()
}

implementation("io.github.rdsunhy:component-jsbridge:1.0.0")
```



# 使用

### 1. 将桥接能力挂载到您的 WebView 实例上：



```kotlin
val bridgeHandler: IBridgeHandler = JsBridgeHandler()
bridgeHandler.attach(webView)

// 当 view 销毁时调用
bridgeHandler.detach()

// 支持自定义桥接名称
WebJsBridge.setBridgeName("YourCustomName") // 默认: WKWebViewJavascriptBridge
```



### 2. 在原生层注册一个桥接，以及调用 Js 层注册的桥接方法：



```kotlin
// 原生层注册方法供 js 调用
bridgeHandler.registerBridger("NativeBridgeMethodName") { data, callback ->
		// todo
}
// 原生层调用 js 提供的方法
bridgeHandler.callBridger("JsBridgeMethodName", data) { response ->
		// todo
}
```



### 3. Js 中使用桥接的封装示例，将其复制到您的 Js 代码中：



```javascript
function setupWebViewJsBridge(callback) {
    let isAndroid = Boolean(navigator.userAgent.match(/android/ig));
    let isIOS = Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig));
    if (window.WKWebViewJavascriptBridge) {
        return callback(window.WKWebViewJavascriptBridge);
    }
    if (window.WKWVJBCallbacks) {
        window.WKWVJBCallbacks.push(callback);
    } else {
        window.WKWVJBCallbacks = [callback];
    }
    if (isAndroid) { // 对比 ios 平台的 WKWebViewJavascriptBridge 库这里需要区分一下 Android 平台
        if (!window.InjectJavascript) {
            window.alert("ERROR: window.InjectJavascript if null!");
            return;
        }
        window.InjectJavascript.init();
    } else if (isIOS) {
        window.webkit.messageHandlers.iOS_Native_InjectJavascript.postMessage(null);
    }
}
```



### 4. 在 Js 中通过调用 setupWebViewJsBridge 方法得到的 bridge 对象来注册、调用桥接方法：



```javascript
// js 层注册桥接方法
setupWebViewJsBridge(function(bridge) {
    bridge.registerHandler("JsBridgeMethodName", function(data, callback) {
        // todo
      	let response = {
          "msg": "invoke success"
        }
      	callback(response) // 支持回调
    })
})

// js 层调用原生提供的方法
setupWebViewJsBridge(function(bridge) {
    bridge.callHandler("NativeBridgeMethodName", params, function(data) {
        // todo
        let response = JSON.parse(data)
    })
})
```
