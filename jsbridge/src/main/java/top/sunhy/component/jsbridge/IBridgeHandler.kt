package top.sunhy.component.jsbridge

import android.webkit.WebView

/**
 * Js Bridge Interface
 *
 * @author Sunhy
 * Create time: 2024/3/20
 */

typealias Callback = (responseData: Any?) -> Unit
typealias BridgeHandler = (parameters: String?, callback: Callback?) -> Unit
typealias Message = MutableMap<String, Any?>

interface IBridgeHandler {

    fun attach(view: WebView)

    fun detach()

    fun registerBridger(handlerName: String, handler: BridgeHandler)

    fun removeBridger(handlerName: String): BridgeHandler?

    fun callBridger(handlerName: String, data: Any? = null, callback: Callback? = null)
}