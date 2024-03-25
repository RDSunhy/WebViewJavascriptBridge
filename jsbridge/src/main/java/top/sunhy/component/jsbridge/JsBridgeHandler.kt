package top.sunhy.component.jsbridge

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Js Bridge Impl
 *
 * @author Sunhy
 * Create time: 2024/3/20
 */
class JsBridgeHandler : IBridgeHandler {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var webView: WebView? = null

    private var startupMessageQueue: MutableList<Message>? = mutableListOf()
    private val responseCallbacks = mutableMapOf<String, Callback>()
    private val messageHandlers = mutableMapOf<String, BridgeHandler>()
    private var messageUniqueId = 0

    override fun attach(view: WebView) {
        this.webView = view
        this.webView?.addJavascriptInterface(InjectJsBridge(), INTERFACE_INJECT_JS)
        this.webView?.addJavascriptInterface(FlushMessageBridge(), INTERFACE_FLUSH_MESSAGE)
    }

    override fun detach() {
        this.webView?.removeJavascriptInterface(INTERFACE_INJECT_JS)
        this.webView?.removeJavascriptInterface(INTERFACE_FLUSH_MESSAGE)
        this.webView = null
    }

    override fun registerBridger(handlerName: String, handler: BridgeHandler) {
        messageHandlers[handlerName] = handler
    }

    override fun removeBridger(handlerName: String): BridgeHandler? {
        return messageHandlers.remove(handlerName)
    }

    override fun callBridger(handlerName: String, data: Any?, callback: Callback?) {
        send(handlerName, data, callback)
    }

    private fun send(handlerName: String, data: Any?, callback: Callback?) {
        val message: Message = mutableMapOf()
        message["handlerName"] = handlerName
        data?.let {
            message["data"] = it
        }
        callback?.let {
            messageUniqueId++
            val callbackID = "native_android_cb_$messageUniqueId"
            responseCallbacks[callbackID] = callback
            message["callbackID"] = callbackID
        }
        queue(message)
    }

    private fun flushJsBridgeQueue() {
        if (webView == null) return
        val webView = requireNotNull(webView)
        webView.evaluateJavascript(SCRIPT_FETCH_QUEUE) { data ->
            if (data.isNullOrEmpty()) return@evaluateJavascript
            kotlin.runCatching {
                val messageQueue = JSONArray(data)
                for (i in 0 until messageQueue.length()) {
                    val message = messageQueue.getJSONObject(i)
                    val responseID = message.opt("responseID") as? String
                    var callback: Callback? = null
                    if (!responseID.isNullOrEmpty()) {
                        responseCallbacks[responseID]?.invoke(message.opt("responseData"))
                    } else {
                        callback = { responseData ->
                            queue(message = mutableMapOf(
                                "responseID" to message.opt("callbackID"),
                                "responseData" to responseData
                            ))
                        }
                    }
                    val handlerName = (message.opt("handlerName") as? String) ?: continue
                    val handler = messageHandlers[handlerName]
                    val data = message.opt("data") as? JSONObject
                    handler?.invoke(data?.toString(), callback)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun queue(message: Message) {
        if (startupMessageQueue == null) {
            dispatch(message)
        } else {
            startupMessageQueue?.add(message)
        }
    }

    private fun dispatch(message: Message) {
        if (webView == null) return
        val webView = requireNotNull(webView)
        if (Looper.getMainLooper() == Looper.myLooper()) {
            val serializedMessage = GsonUtils.toJson(message)
            val script = String.format(SCRIPT_HANDLE_MESSAGE, serializedMessage)
            webView.evaluateJavascript(script) {}
        } else {
            mainHandler.post { dispatch(message) }
        }
    }

    private inner class InjectJsBridge {
        @JavascriptInterface
        fun init() = mainHandler.post {
            if (webView == null) return@post
            val webView = requireNotNull(webView)
            webView.evaluateJavascript(SCRIPT_INJECT_BRIDGE) {
                startupMessageQueue?.forEach(::dispatch)
                startupMessageQueue?.clear()
                startupMessageQueue = null
            }
        }
    }

    private inner class FlushMessageBridge {
        @JavascriptInterface
        fun postMessage() = mainHandler.post {
            flushJsBridgeQueue()
        }
    }
}