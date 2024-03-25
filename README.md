# WebViewJavascriptBridge

Reference [WKWebViewJavascriptBridge]() implementation of the android platform.



# Usage

### 1. Instantiate WKWebViewJavascriptBridge with a WKWebView:



```kotlin
val bridgeHandler: IBridgeHandler = JsBridgeHandler()
bridgeHandler.attach(webView)

// Called when the view is destroyed.
bridgeHandler.detach()
```



### 2. Register a Handler in Native, and Call a JS Handler:



```kotlin
bridgeHandler.registerBridger("NativeBridgeMethodName") { data, callback ->
    // todo
}

bridgeHandler.callBridger("JsBridgeMethodName", data) { response ->
	  // todo
}
```



### 3. Copy and Paste setupWKWebViewJavascriptBridge into Your JS:



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
    if (isAndroid) {
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



### 4. Finally, Call setupWKWebViewJavascriptBridge and then Use The Bridge to Register Handlers and Call Native Handlers:



```javascript
// registe js method
setupWebViewJsBridge(function(bridge) {
    bridge.registerHandler("JsBridgeMethodName", function(data, callback) {
        // todo
      	// e.g. callback
      	let response = {
          "msg": "invoke success"
        }
      	callback(response)
    })
})

// call native method
setupWebViewJsBridge(function(bridge) {
    bridge.callHandler("NativeBridgeMethodName", params, function(data) {
        // todo
        let response = JSON.parse(data)
    })
})
```
