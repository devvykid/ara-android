package com.xfl.kakaotalkbot;

import android.content.Context;
import android.text.Html;
import android.widget.Toast;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XFL on 2/20/2018.
 */

public class Log extends ScriptableObject {

    private static Context ctx = MainApplication.getContext();
    private static String log;
    private static String logStack = ctx.getSharedPreferences("log", 0).getString("log", "");
    ;


    private static Integer debugLength = ctx.getSharedPreferences("log", 0).getInt("debugLength", 0);

    private static Integer infoLength = ctx.getSharedPreferences("log", 0).getInt("infoLength", 0);

    private static Integer errorLength = ctx.getSharedPreferences("log", 0).getInt("errorLength", 0);

    public Log() {

    }


    @JSStaticFunction
    public static void d(String str) {
        debug(str);
    }


    @JSStaticFunction
    public static void e(String str, boolean bool) {
        error(str, bool);
    }

    ;

    @JSStaticFunction
    public static void i(String str) {
        info(str);
    }

    ;

    @JSStaticFunction
    public static void debug(String str) {
        String scriptName = MainApplication.getContext().getSharedPreferences("log", 0).getString("logTarget", "");
        debugLength++;
        ctx.getSharedPreferences("log", 0).edit().putInt("debugLength", debugLength).apply();
        str = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(new Date()) + str.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
        log = "<font color=GREEN>" + str + "</font><br><br>";

        logStack += log;

        LoggerScreen.appendLogText(Html.fromHtml(log));


        ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply();
    }

    @JSStaticFunction
    public static void info(String str) {
        String scriptName = MainApplication.getContext().getSharedPreferences("log", 0).getString("logTarget", "");
        infoLength++;
        ctx.getSharedPreferences("log", 0).edit().putInt("infoLength", infoLength).apply();
        str = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(new Date()) + str.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
        log = str + "<br><br>";
        logStack += log;

        LoggerScreen.appendLogText(Html.fromHtml(log));

        ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply();
    }

    @JSStaticFunction
    public static void error(final String str, final boolean toast) {
        String scriptName = MainApplication.getContext().getSharedPreferences("log", 0).getString("logTarget", "");
        errorLength++;
        ctx.getSharedPreferences("log", 0).edit().putInt("errorLength", errorLength).apply();
        final String formed = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(new Date()) + str.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
        log = "<font color=RED>" + formed + "</font><br><br>";
        logStack += log;
        NotificationListener.UIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast) {
                    Toast.makeText(ctx, "Runtime Error:" + str, Toast.LENGTH_LONG).show();
                }

            }
        });
        LoggerScreen.appendLogText(Html.fromHtml(log));
        ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply();
    }


    @JSStaticFunction
    public static void clear() {
        String scriptName = MainApplication.getContext().getSharedPreferences("log", 0).getString("logTarget", "");
        logStack = "";
        infoLength = 0;
        debugLength = 0;
        errorLength = 0;
        NotificationListener.UIHandler.post(new Runnable() {
            @Override
            public void run() {
                LoggerScreen.clearLogText();
            }
        });
        ctx.getSharedPreferences("log", 0).edit().putString("log", "").apply();
        ctx.getSharedPreferences("log", 0).edit().putInt("infoLength", 0).apply();
        ctx.getSharedPreferences("log", 0).edit().putInt("debugLength", 0).apply();
        ctx.getSharedPreferences("log", 0).edit().putInt("errorLength", 0).apply();
    }

    @JSStaticFunction
    public static Integer getDebugLength() {
        return debugLength;
    }

    @JSStaticFunction
    public static Integer getInfoLength() {
        return infoLength;
    }

    @JSStaticFunction
    public static Integer getErrorLength() {
        return errorLength;
    }

    @JSStaticFunction
    public static Integer getLength() {
        return debugLength + infoLength + errorLength;
    }

    public final String getClassName() {
        return "Log";
    }
    /*@JSStaticFunction
    public static String[] getDebugLog(){

        String[] arr=new String[]{};
        if(debugLength==0){return arr;}
        Integer idx=0;
        String str;
        for(Integer i=0;i<logStack.split("<br><br>").length;i++){

            try {
                str = logStack.split("<br><br>")[i].split("<font color=GREEN>")[1];
            }catch(ArrayIndexOutOfBoundsException e){
                continue;
            }
            arr[idx]=str.split("</font>")[0];
            idx++;
        }
        return arr;
    }*/

}
