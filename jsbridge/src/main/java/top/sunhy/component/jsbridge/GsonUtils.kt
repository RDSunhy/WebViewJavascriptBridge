package top.sunhy.component.jsbridge

import com.google.gson.GsonBuilder

/**
 * Gson
 *
 * @author Sunhy
 * Create time: 2024/3/20
 */
internal object GsonUtils {

    private val gson by lazy {
        GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create()
    }

    fun toJson(obj: Any): String = gson.toJson(obj)

}