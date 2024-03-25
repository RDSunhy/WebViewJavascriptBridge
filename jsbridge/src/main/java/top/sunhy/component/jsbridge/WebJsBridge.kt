package top.sunhy.component.jsbridge

/**
 * Js Bridge Manager
 *
 * @author Sunhy
 * Create time: 2024/3/20
 */
object WebJsBridge {

    /**
     * [WKWebViewJavascriptBridge](https://baidu.com)
     * The JavascriptBridge default object name is "WKWebViewJavascriptBridge".
     */
    internal var JS_BRIDGE_INSTANCE = "WKWebViewJavascriptBridge"

    internal var DEBUG = false

    fun setBridgeName(name: String) {
        this.JS_BRIDGE_INSTANCE = name
    }

}