package com.xfl.kakaotalkbot;


import android.os.Build;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;


/**
 * Created by XFL on 2/20/2018.
 */

public class Utils extends ScriptableObject {
    static StringBuilder result;

    @JSStaticFunction
    public static String getWebText(final String str) {
        int timeout = MainApplication.getContext().getSharedPreferences("publicSettings", 0).getInt("jsoupTimeout", 10000);
        try {
            return Jsoup.connect(str).ignoreContentType(true).timeout(timeout).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                    .referrer("http://www.google.com")
                    .get().toString();
        } catch (Exception e) {

            Context.reportError(e.toString());
            return null;
        }

    }

    @JSStaticFunction
    public static Document parse(final String str) {
        int timeout = MainApplication.getContext().getSharedPreferences("publicSettings", 0).getInt("jsoupTimeout", 10000);
        try {
            return Jsoup.connect(str).ignoreContentType(true).timeout(timeout).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                    .referrer("http://www.google.com")
                    .get();
        } catch (Exception e) {

            Context.reportError(e.toString());
            return null;
        }

    }

    @JSStaticFunction
    public static int getAndroidVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    @JSStaticFunction
    public static String getAndroidVersionName() {
        return Build.VERSION.RELEASE;
    }


    @JSStaticFunction
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    @JSStaticFunction
    public static String getPhoneModel() {
        return Build.DEVICE;
    }


    public final String getClassName() {
        return "Utils";
    }


}
