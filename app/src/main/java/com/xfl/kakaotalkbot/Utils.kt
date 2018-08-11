package com.xfl.kakaotalkbot


import android.os.Build

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction


/**
 * Created by XFL on 2/20/2018.
 */

class Utils : ScriptableObject() {


    override fun getClassName(): String {
        return "Utils"
    }

    companion object {


        @JvmStatic
        @JSStaticFunction
        fun getWebText(str: String): String? {
            val timeout = MainApplication.context!!.getSharedPreferences("publicSettings", 0).getInt("jsoupTimeout", 10000)
            return try {
                Jsoup.connect(str).ignoreContentType(true).timeout(timeout).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                        .referrer("http://www.google.com")
                        .get().toString()
            } catch (e: Exception) {

                Context.reportError(e.toString())
                null
            }

        }

        @JvmStatic
        @JSStaticFunction
        fun parse(str: String): Document? {
            val timeout = MainApplication.context!!.getSharedPreferences("publicSettings", 0).getInt("jsoupTimeout", 10000)
            return try {
                Jsoup.connect(str).ignoreContentType(true).timeout(timeout).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                        .referrer("http://www.google.com")
                        .get()
            } catch (e: Exception) {

                Context.reportError(e.toString())
                null
            }

        }

        val androidVersionCode: Int
            @JvmStatic
        @JSStaticFunction
            get() = Build.VERSION.SDK_INT

        val androidVersionName: String
            @JvmStatic
        @JSStaticFunction
            get() = Build.VERSION.RELEASE


        val phoneBrand: String
            @JvmStatic
        @JSStaticFunction
            get() = Build.BRAND

        val phoneModel: String
            @JvmStatic
        @JSStaticFunction
            get() = Build.DEVICE
    }


}
