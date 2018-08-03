package com.xfl.kakaotalkbot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.faendir.rhino_android.RhinoAndroidHelper
import org.json.JSONObject
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

/**
 * Created by XFL on 2/20/2018.
 */

class Api : ScriptableObject() {

    override fun getClassName(): String {
        return "Api"
    }

    companion object {
        var isDebugMode: Boolean = false

        var scriptName: String? = null


        val rootView: View
            @JvmStatic
        @JSStaticFunction
            get() = NotificationListener.rootView

        val context: Context
            @JvmStatic
        @JSStaticFunction
            get() = MainApplication.context!!

        /*@JvmStatic
        @JSStaticFunction
        fun getThreads(scriptName: String):Scriptable{
            if(NotificationListener.threads[scriptName]==null){
                return org.mozilla.javascript.Context.enter().newArray(ScriptsManager.execScope!!, arrayOf())
            }
            return org.mozilla.javascript.Context.enter().newArray(ScriptsManager.execScope!!, NotificationListener.threads[scriptName]!!.toArray())
        }*/
        @JvmStatic
        @JSStaticFunction
        fun UIThread(function: org.mozilla.javascript.Function, onComplete: Function?) {
            val parseCtx = RhinoAndroidHelper().enterContext()
            parseCtx.wrapFactory = PrimitiveWrapFactory()
            val excScope: ScriptableObject? = ScriptsManager.execScope

            //parseCtx.setOptimizationLevel(ScriptsManager.container.get(scriptName).optimization);


            NotificationListener.UIHandler!!.post {
                org.mozilla.javascript.Context.enter()
                var error: Throwable? = null
                var result: Any? = null
                try {

                    result = function.call(parseCtx, excScope, excScope, arrayOf())

                } catch (e: Throwable) {
                    error = e
                    //parseCtx.getErrorReporter().error(e.getMessage(), "a", 0, "aaa", 0);
                }

                try {
                    onComplete?.call(parseCtx, excScope, excScope, arrayOf(error, result))
                } catch (e: Throwable) {
                    Log.error(e.toString(), true)
                }

                org.mozilla.javascript.Context.exit()
            }


        }

        @JvmStatic
        @JSStaticFunction
        fun showToast(str: String, length: Int) {
            NotificationListener.UIHandler!!.post { Toast.makeText(MainApplication.context, str, length).show() }

        }

        @JvmStatic
        @JSStaticFunction
        fun canReply(room: String): Boolean {
            return NotificationListener.hasSession(room) || room == NotificationListener.debugRoom
        }

        @JvmStatic
        @JSStaticFunction
        fun replyRoom(room: String, str: String, hideToast: Boolean): Boolean {
            try {
                return SessionCacheReplier(room).reply(room, str, hideToast)


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        @JvmStatic
        @JSStaticFunction
        fun off(scriptName: String): Boolean {

            if (scriptName == "undefined") {
                NotificationListener.UIHandler!!.post { ScriptSelectActivity.putOnAll(false) }

            } else {
                if (!File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + scriptName).exists()) {
                    return false
                }
                if (MainApplication.context!!.getSharedPreferences("settings$scriptName", 0).getBoolean("ignoreApiOff", false))
                    return false
                NotificationListener.UIHandler!!.post { ScriptSelectActivity.putOn(scriptName, false) }
            }
            return true

        }

        @JvmStatic
        @JSStaticFunction
        fun on(scriptName: String): Boolean {

            if (scriptName == "undefined") {
                NotificationListener.UIHandler!!.post { ScriptSelectActivity.putOnAll(true) }

            } else {
                if (!File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + scriptName).exists()) {
                    return false
                }
                NotificationListener.UIHandler!!.post { ScriptSelectActivity.putOn(scriptName, true) }
            }
            return true

        }

        @JvmStatic
        @JSStaticFunction
        fun isOn(scriptName: String): Boolean {
            return context.getSharedPreferences("bot$scriptName", 0).getBoolean("on", false)
        }

        @JvmStatic
        @JSStaticFunction
        fun isCompiled(scriptName: String): Boolean {
            return ScriptsManager.container[scriptName] != null
        }


            @JvmStatic
            @JSStaticFunction
            fun getScriptNames():Scriptable? {
                val basePath = File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot")
                basePath.mkdir()
                val files = basePath.listFiles()
                val list = ArrayList<String>()
                for (k in files) {
                    if (k.name.endsWith(".js")) {
                        list.add(k.name)
                    }
                }

                return org.mozilla.javascript.Context.enter().newArray(ScriptsManager.execScope!!, list.toArray())

            }
        /*@kotlin.jvm.JvmStatic
        @JSStaticFunction
    public static Scriptable getScriptFiles(){

        File basePath = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");
        basePath.mkdir();

        File[] files = basePath.listFiles();
        List<File>list=new ArrayList<>();

        for (File k : files) {
            if (k.getName().endsWith(".js")) {
                list.add(k);
            }
        }
        return org.mozilla.javascript.Context.enter().newArray(ScriptsManager.container.get(scriptName).execScope,list.toArray());
    }*/


        @JvmStatic
        @JSStaticFunction
        fun makeNoti(title: String, content: String, id: Int): Boolean {

            val notificationManager = MainApplication.context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            try {
                NotificationListener.UIHandler!!.post {
                    val noti = Notification.Builder(MainApplication.context)


                    noti.setSmallIcon(R.mipmap.ic_launcher)
                    noti.setContentTitle(title)
                    noti.setContentText(content)
                    noti.setPriority(Notification.PRIORITY_MAX)


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        noti.setChannelId("com.xfl.kakaotalkbot.customNotification")
                        // Create the NotificationChannel
                        val name = MainApplication.context!!.getString(R.string.channel_name)
                        val description = MainApplication.context!!.getString(R.string.channel_description)

                        val mChannel = NotificationChannel("com.xfl.kakaotalkbot.customNotification", name, NotificationManager.IMPORTANCE_HIGH)
                        mChannel.description = description
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this

                        notificationManager.createNotificationChannel(mChannel)
                    }


                    notificationManager.notify(id, noti.build())
                }


            } catch (e: Exception) {
                return false
            }

            return true
        }


        @JvmStatic
        @JSStaticFunction
        fun prepare(scriptName: String): Int {
            if (Api.isCompiled(scriptName)) return 2
            return if (Api.reload(scriptName))
                1
            else
                0
        }

        @JvmStatic
        @JSStaticFunction
        fun compile(scriptName: String): Boolean {
            return reload(scriptName)
        }

        @JvmStatic
        @JSStaticFunction
        fun unload(scriptName: String): Boolean {
            if (scriptName == "undefined") return false
            if (!ScriptsManager.container.containsKey(scriptName)) return false
            ScriptsManager.container.remove(scriptName)
            return true
        }

        @JvmStatic
        @JSStaticFunction
        fun reload(scriptName: String): Boolean {


            if (scriptName == "undefined") {

                ScriptsManager.initializeAll(false)


            } else {
                if (!File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + scriptName).exists()) {
                    return false
                }


                NotificationListener.UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(scriptName, true, true) }
                val bool = ScriptsManager.initializeScript(scriptName, false)

                NotificationListener.UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(scriptName, false, bool) }

                return bool
            }
            return true

        }

        @JvmStatic
        @JSStaticFunction
        fun papagoTranslate(source: String, target: String, str: String, errorToString: Boolean?): String? {
            return doPapagoTranslate(source, target, str, errorToString)
        }

        @JvmStatic
        @JSStaticFunction
        private fun doPapagoTranslate(source: String, target: String, str: String, errorToString: Boolean?): String? {

            var res: String? = null


            val clientId = "80WHs92cd42tX_6Y_mIZ"//애플리케이션 클라이언트 아이디값";
            val clientSecret = "8nD99bYAlD"//애플리케이션 클라이언트 시크릿값";
            try {
                val text = URLEncoder.encode(str, "UTF-8")
                val apiURL = "https://openapi.naver.com/v1/language/translate"
                val url = URL(apiURL)
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "POST"
                con.setRequestProperty("X-Naver-Client-Id", clientId)
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret)
                // post request
                val postParams = "source=$source&target=$target&text=$text"
                con.doOutput = true
                val wr = DataOutputStream(con.outputStream)
                wr.writeBytes(postParams)
                wr.flush()
                wr.close()
                val responseCode = con.responseCode
                val br: BufferedReader
                if (responseCode == 200) { // 정상 호출
                    br = BufferedReader(InputStreamReader(con.inputStream))
                } else {  // 에러 발생

                    br = BufferedReader(InputStreamReader(con.errorStream))
                    var einputLine: String
                    val eresponse = StringBuilder()

                    eresponse.append(br.readLines().joinToString("\n"))
                    throw Exception(JSONObject(eresponse.toString()).getString("errorMessage"))

                }
                var inputLine: String
                val response = StringBuilder()

                response.append(br.readLines().joinToString("\n"))
                br.close()
                val jsonObject = JSONObject(response.toString())

                res = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText")

            } catch (e: Exception) {
                if (errorToString!!) {
                    res = e.message
                } else {

                    org.mozilla.javascript.Context.enter()
                    org.mozilla.javascript.Context.reportError(e.message)

                }
                e.printStackTrace()
            }


            return res
        }

        @JvmStatic
        @JSStaticFunction
        fun gc() {
            System.gc()
        }
    }


}
