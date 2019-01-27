package kr.oror.arabot

import android.text.Html
import android.widget.Toast
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by XFL, modified by 컴터박 on 2/20/2018.
 */

class Log : ScriptableObject() {

    override fun getClassName(): String {
        return "Log"
    }

    companion object {

        private val ctx = MainApplication.context
        private var log: String? = null
        private var logStack = ctx!!.getSharedPreferences("log", 0).getString("log", "")

        @JvmStatic
        @get:JSStaticFunction
        var debugLength: Int = ctx!!.getSharedPreferences("log", 0).getInt("debugLength", 0)
            private set
        @JvmStatic
        @get:JSStaticFunction
        var infoLength: Int = ctx!!.getSharedPreferences("log", 0).getInt("infoLength", 0)
            private set
        @JvmStatic
        @get:JSStaticFunction
        var errorLength: Int = ctx!!.getSharedPreferences("log", 0).getInt("errorLength", 0)
            private set

        @JvmStatic
        @JSStaticFunction
        fun d(str: String) {
            debug(str)
        }

        @JvmStatic
        @JSStaticFunction
        fun e(str: String, bool: Boolean) {
            error(str, bool)
        }

        @JvmStatic
        @JSStaticFunction
        fun i(str: String) {
            info(str)
        }

        @JvmStatic
        @JSStaticFunction
        fun debug(str: String) {
            var str = str
            //val scriptName = MainApplication.context!!.getSharedPreferences("log", 0).getString("logTarget", "")
            debugLength++
            ctx!!.getSharedPreferences("log", 0).edit().putInt("debugLength", debugLength).apply()
            str = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(Date()) + str.replace("<".toRegex(), "&lt;").replace(">".toRegex(), "&gt;").replace("\n".toRegex(), "<br>")
            log = "<font color=GREEN>$str</font><br><br>"

            logStack += log

            LoggerScreen.appendLogText(Html.fromHtml(log))


            ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun info(str: String) {
            var str = str
            //val scriptName = MainApplication.context!!.getSharedPreferences("log", 0).getString("logTarget", "")
            infoLength++
            ctx!!.getSharedPreferences("log", 0).edit().putInt("infoLength", infoLength).apply()
            str = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(Date()) + str.replace("<".toRegex(), "&lt;").replace(">".toRegex(), "&gt;").replace("\n".toRegex(), "<br>")
            log = "$str<br><br>"
            logStack += log

            LoggerScreen.appendLogText(Html.fromHtml(log))

            ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun error(str: String, toast: Boolean) {
            // val scriptName = MainApplication.context!!.getSharedPreferences("log", 0).getString("logTarget", "")

            errorLength++
            ctx!!.getSharedPreferences("log", 0).edit().putInt("errorLength", errorLength).apply()
            val formed = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(Date()) + str.replace("<".toRegex(), "&lt;").replace(">".toRegex(), "&gt;").replace("\n".toRegex(), "<br>")
            log = "<font color=RED>$formed</font><br><br>"
            logStack += log
            NotificationListener.UIHandler!!.post {
                if (toast) {
                    Toast.makeText(ctx, "Runtime Error:$str", Toast.LENGTH_LONG).show()
                }
            }
            LoggerScreen.appendLogText(Html.fromHtml(log))
            ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply()
        }

        fun internalError(str: String) {
            errorLength++
            ctx!!.getSharedPreferences("log", 0).edit().putInt("errorLength", errorLength).apply()
            logStack += str
            LoggerScreen.appendLogText(Html.fromHtml(str))
            ctx.getSharedPreferences("log", 0).edit().putString("log", logStack).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun clear() {
            //val scriptName = MainApplication.context!!.getSharedPreferences("log", 0).getString("logTarget", "")
            logStack = ""
            infoLength = 0
            debugLength = 0
            errorLength = 0
            NotificationListener.UIHandler!!.post { LoggerScreen.clearLogText() }
            ctx!!.getSharedPreferences("log", 0).edit().putString("log", "").apply()
            ctx.getSharedPreferences("log", 0).edit().putInt("infoLength", 0).apply()
            ctx.getSharedPreferences("log", 0).edit().putInt("debugLength", 0).apply()
            ctx.getSharedPreferences("log", 0).edit().putInt("errorLength", 0).apply()
        }

        val length: Int
            @JvmStatic
            @JSStaticFunction
            get() = debugLength + infoLength + errorLength
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
