package top.sunhy.component.jsbridge

/**
 * Javascript Constants.
 *
 * @author Sunhy
 * Create time: 2024/3/20
 */

internal const val INTERFACE_INJECT_JS = "InjectJavascript"
internal const val INTERFACE_FLUSH_MESSAGE = "FlushMessageQueue"

internal val JS_BRIDGE_INSTANCE get() = WebJsBridge.JS_BRIDGE_INSTANCE

internal val SCRIPT_FETCH_QUEUE = "window.${JS_BRIDGE_INSTANCE}._fetchQueue()"
internal val SCRIPT_HANDLE_MESSAGE = "window.${JS_BRIDGE_INSTANCE}._handleMessageFromAndroid('%s')"

internal val SCRIPT_INJECT_BRIDGE = """
;(function() {
    if (window.${JS_BRIDGE_INSTANCE}) {
        return;
    }

    window.${JS_BRIDGE_INSTANCE} = {
        registerHandler: registerHandler,
        callHandler: callHandler,
        _fetchQueue: _fetchQueue,
        _handleMessageFromAndroid: _handleMessageFromAndroid,
        messageHandlers: {},
        waitQueue: [],
    };

    var sendMessageQueue = [];
    var responseCallbacks = {};
    var uniqueId = 1;

    function registerHandler(handlerName, handler) {
        window.${JS_BRIDGE_INSTANCE}.messageHandlers[handlerName] = handler;
        for(var i = window.${JS_BRIDGE_INSTANCE}.waitQueue.length - 1; i >= 0; i--){
            var item = window.${JS_BRIDGE_INSTANCE}.waitQueue[i];
            var message = JSON.parse(item)
            if (message.handlerName == handlerName) {
                window.${JS_BRIDGE_INSTANCE}.waitQueue.splice(i, 1);
                _dispatchMessageFromAndroid(item);
            }
        }
    }

    function callHandler(handlerName, data, responseCallback) {
        _doSend({ handlerName:handlerName, data:data }, responseCallback);
    }

    function _doSend(message, responseCallback) {
        if (responseCallback) {
            var callbackID = 'cb_'+message.handlerName+'_'+(uniqueId++)+'_'+new Date().getTime();
            responseCallbacks[callbackID] = responseCallback;
            message['callbackID'] = callbackID;
        }
        sendMessageQueue.push(message);
        window.${INTERFACE_FLUSH_MESSAGE}.postMessage()
    }

    function _fetchQueue() {
        var tmp = sendMessageQueue.slice(0)
        sendMessageQueue = [];
        return tmp;
    }

    function _dispatchMessageFromAndroid(messageJSON) {
        var message = JSON.parse(messageJSON);
        var messageHandler;
        var responseCallback;
        if (message.responseID) {
            responseCallback = responseCallbacks[message.responseID];
            if (!responseCallback) {
                return;
            }
            responseCallback(JSON.stringify(message.responseData));
            delete responseCallbacks[message.responseID];
        } else {
            if (message.callbackID) {
                var callbackResponseId = message.callbackID;
                responseCallback = function(responseData) {
                    _doSend({ handlerName:message.handlerName, responseID:callbackResponseId, responseData:responseData });
                };
            }

            var handler = window.${JS_BRIDGE_INSTANCE}.messageHandlers[message.handlerName];
            if (handler){
                handler(message.data, responseCallback);
            } else {
                console.log("${JS_BRIDGE_INSTANCE}: WARNING: no handler for message from Android:", message);
                window.${JS_BRIDGE_INSTANCE}.waitQueue.push(messageJSON)
            }
        }
    }


    function _handleMessageFromAndroid(messageJSON) {
        _dispatchMessageFromAndroid(messageJSON);
    }


    setTimeout(_callJavascriptBridgeCallBacks, 0);
    function _callJavascriptBridgeCallBacks() {
        var callbacks = window.WKWVJBCallbacks || [];
        delete window.WKWVJBCallbacks;
        for (var i = 0; i < callbacks.length; i++) {
            callbacks[i](${JS_BRIDGE_INSTANCE});
        }
    }
    
    if (window.${JS_BRIDGE_INSTANCE}){
        console.log("$JS_BRIDGE_INSTANCE Init Successful!")
    }else {
        console.log("$JS_BRIDGE_INSTANCE Init Failure!")
    }
    
})();
""".trimIndent()
