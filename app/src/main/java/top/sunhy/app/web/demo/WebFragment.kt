package top.sunhy.app.web.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.json.JSONObject
import top.sunhy.app.web.demo.databinding.FragmentWebBinding
import top.sunhy.component.jsbridge.IBridgeHandler
import top.sunhy.component.jsbridge.JsBridgeHandler

/**
 *
 *
 * @author Sunhy
 * Create time: 2024/3/22
 */
@SuppressLint("SetJavaScriptEnabled")
class WebFragment internal constructor() : Fragment(R.layout.fragment_web), IBridgeHandler by JsBridgeHandler() {

    companion object {
        fun newInstance(url: String): Fragment {
            val fragment = WebFragment()
            fragment.arguments = bundleOf(
                "url" to url
            )
            return fragment
        }
    }

    private var _vb: FragmentWebBinding? = null
    private val vb: FragmentWebBinding
        get() = requireNotNull(_vb)

    private val url: String
        get() = requireArguments().getString("url")!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _vb = FragmentWebBinding.bind(view)
        attach(vb.webView)
        vb.webView.settings.javaScriptEnabled = true
        vb.webView.webViewClient = DefaultWebClient()
        vb.webView.webChromeClient = DefaultWebChromeClient()
        vb.webView.loadUrl(url)

        vb.bn1.setOnClickListener {
            callBridger("showJsAlert")
        }

        vb.bn2.setOnClickListener {
            val data = mapOf("message" to "This is Native Params.")
            callBridger("showJsAlertWithParams", data) { response ->
                val result = JSONObject(response.toString()).optString("result")
                AlertDialog.Builder(requireContext())
                    .setTitle("Native Alert")
                    .setMessage("received Js Response.\nUser clicked \"${result}\".")
                    .setCancelable(false)
                    .setNegativeButton("cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("confirm") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        vb.bn3.setOnClickListener {
            callBridger("delayedRegisterAlert", null) { response ->
                val data = JSONObject(response.toString())
                val result = data.optString("result")
                AlertDialog.Builder(requireContext())
                    .setTitle("Native Alert")
                    .setMessage("received Js Response.\nUser clicked \"${result}\".")
                    .setCancelable(false)
                    .setNegativeButton("cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("confirm") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        registerBridger("showNativeAlert") { data, callback ->
            AlertDialog.Builder(requireContext())
                .setTitle("Native Alert")
                .setMessage("Js invoke \"showNativeAlert\" method success.")
                .setCancelable(false)
                .setNegativeButton("cancel") { dialog, _ ->
                    val response = mapOf("result" to "cancel")
                    callback?.invoke(response)
                }
                .setPositiveButton("confirm") { dialog, _ ->
                    val response = mapOf("result" to "confirm")
                    callback?.invoke(response)
                }
                .create()
                .show()
        }

        registerBridger("showNativeAlertWithParams") { data, callback ->
            AlertDialog.Builder(requireContext())
                .setTitle("Native Alert")
                .setMessage("Js invoke \"showNativeAlertWithParams\" method success.\nJs Params: [data=${data}]")
                .setCancelable(false)
                .setNegativeButton("cancel") { dialog, _ ->
                    val response = mapOf("result" to "cancel")
                    callback?.invoke(response)
                }
                .setPositiveButton("confirm") { dialog, _ ->
                    val response = mapOf("result" to "confirm")
                    callback?.invoke(response)
                }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        detach()
        vb.webView.settings.javaScriptEnabled = false
        _vb = null
    }

    private inner class DefaultWebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    private inner class DefaultWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Js Alert")
//                .setMessage(message)
//                .setCancelable(false)
//                .setNegativeButton("cancel") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .setPositiveButton("confirm") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//            return true
            return false
        }
    }
}