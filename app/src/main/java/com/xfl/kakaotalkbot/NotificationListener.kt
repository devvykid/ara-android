package com.xfl.kakaotalkbot


import android.app.Notification
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Toast
import com.faendir.rhino_android.RhinoAndroidHelper
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * Created by XFL on 2/19/2018.
 */

class NotificationListener : NotificationListenerService() {
    lateinit internal var context: android.content.Context
    internal var photo: Bitmap? = null
    internal var firstCompiling = false

    override fun onCreate() {

        super.onCreate()
        context = applicationContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        super.onNotificationPosted(sbn)
        if (!MainApplication.context!!.getSharedPreferences("bot", 0).getBoolean("activate", true))
            return
        if (firstCompiling) return
        val packName = sbn.packageName
        val extras = sbn.notification.extras
        Log.d("extras", extras.toString())
        try {
            Log.d("txt", extras.getCharSequence("android.text")!!.toString())
            Log.d("Package", sbn.packageName)
        } catch (e: NullPointerException) {
        }

        if (!(packName == "jp.naver.line.android"
                        || packName == "com.facebook.orca"
                        || packName == "com.lbe.parallel.intl"
                        || packName == "com.kakao.talk"
                        || packName == "org.telegram.messenger") && !MainApplication.context!!.getSharedPreferences("publicSettings", 0).getString("customPackages", "")!!.contains(packName)) {
            return
        }

        if (isCompiling.size <= 0 && MainApplication.context!!.getSharedPreferences("publicSettings", 0).getBoolean("autoCompile", true)) {//hasEverCompiled

            firstCompiling = true
            Thread(Runnable {
                basePath.mkdir()
                val files = basePath.listFiles() ?: return@Runnable
                for (k in files) {
                    if (k.name.endsWith(".js")) {
                        if (MainApplication.context!!.getSharedPreferences("bot" + k.name, 0).getBoolean("on", false)) {
                            var bool = false
                            UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(k.name, true, true) }
                            if (isCompiling[k.name] == null) {
                                bool = initializeScript(k.name, true)

                            }
                            val fbool = bool
                            UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(k.name, false, fbool) }
                        }
                    }
                }
                firstCompiling = false
                //                        onNotificationPosted(sbn);
            }).start()
            return

        }
        var PREF_SETTINGS: String

        try {
            if (Build.VERSION.SDK_INT <= 23)
                if ((extras.get("android.rebuild.applicationInfo") as ApplicationInfo).packageName.contains("com.kakao.talk")) {
                    Log.d("ApplicationInfo", "katalk")

                    if (extras.get("android.largeIcon") is Bitmap)
                        photo = extras.get("android.largeIcon") as Bitmap
                }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        var isAvailable: Boolean
        val wExt = Notification.WearableExtender(sbn.notification)
        if (container.size <= 0) return
        val keySet = container.keys
        for (act in wExt.actions) {

            Log.d("actions", act.title.toString())
            Log.d("actionsExtra", act.extras.toString())

            if (act.remoteInputs != null && act.remoteInputs.size > 0) {

                if (act.title.toString().toLowerCase().contains("reply") ||
                        act.title.toString().contains("답장") || act.title.toString().contains("返信") || act.title.toString().contains("답글")) {

                    var room: String?
                    var sender: String
                    var msg: String


                    var isGroupChat:Boolean=extras.get("android.text")is SpannableString

                    if (Build.VERSION.SDK_INT > 23) {
                        try {
                            if (extras.get("android.largeIcon") is Bitmap)
                                photo = extras.get("android.largeIcon") as Bitmap
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }

                        room = extras.getString("android.summaryText")

                        sender = extras.get("android.title")!!.toString()
                        msg = extras.get("android.text")!!.toString()

                        if (room == null) {
                            room = sender
                            isGroupChat = false
                        } else {
                            isGroupChat = true
                        }
                        if (packName == "com.facebook.orca") {
                            if (extras.get("android.text") !is String) {
                                val html = Html.toHtml(extras.get("android.text") as Spanned)

                                sender = Html.fromHtml(html.split("<b>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split("</b>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]).toString()
                                msg = Html.fromHtml(html.split("</b>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split("</p>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].substring(1)).toString()
                                isGroupChat = true
                            } else {
                                isGroupChat = false
                            }
                        }

                    } else {
                        var katalk_ver=0
                        var no_katalk=false
                        try{
                            katalk_ver=MainApplication.context!!.packageManager.getPackageInfo("com.kakao.talk",0).versionCode
                        }catch(thr:Throwable){
                            no_katalk=true
                        }
                        if(no_katalk||packName!="com.kakao.talk"||katalk_ver<1907310){
                            room = extras.getString("android.title")

                            if (extras.get("android.text") !is String) {

                                val html = Html.toHtml(extras.get("android.text") as Spanned)

                                sender = Html.fromHtml(html.split("<b>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split("</b>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]).toString()

                                msg = Html.fromHtml(html.split("</b>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split("</p>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].substring(1)).toString()

                            } else {

                                sender = room

                                msg = extras.get("android.text")!!.toString()

                            }
                        }else {
                            room = extras.getString("android.subText")
                            sender = extras.getString("android.title")
                            msg = extras.getString("android.text")
                            isGroupChat = room != null
                            if (room == null) room = sender
                        }
                    }
                    Log.d("room", room)
                    Log.d("msg", msg)
                    Log.d("sender", sender)
                    Log.d("isGroupChat", isGroupChat.toString() + "")
                    /*if (MainApplication.getContext().getSharedPreferences(PREF_SETTINGS, 0).getBoolean("specificLog", false)) {
                                com.xfl.kakaotalkbot.Log.debug("App: " + packName);
                                com.xfl.kakaotalkbot.Log.debug("room: " + room);
                                com.xfl.kakaotalkbot.Log.debug("msg: " + msg);
                                com.xfl.kakaotalkbot.Log.debug("sender: " + sender);
                                com.xfl.kakaotalkbot.Log.debug("isGroupChat: " + isGroupChat);
                            }
*/

                    for (key in keySet) {
                        if (!applicationContext.getSharedPreferences("bot$key", 0).getBoolean("on", false))
                            continue
                        val banNames = banNameArr[key]
                        val banRooms = banRoomArr[key]
                        if (banNames != null && banRooms != null) {
                            var banned = false
                            for (k in banNames) {
                                if (k == sender) banned = true
                            }
                            for (k in banRooms) {
                                if (k == room) banned = true
                            }
                            if (banned) continue
                        }
                        PREF_SETTINGS = "settings$key"

                        isAvailable = (packName == "jp.naver.line.android" && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useLine", false)
                                || packName == "com.facebook.orca" && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useFacebookMessenger", false)
                                || packName == "com.lbe.parallel.intl" && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useParallelSpace", false)
                                || packName == "com.kakao.talk" && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useNormal", true)
                                || packName == "org.telegram.messenger" && context.getSharedPreferences(PREF_SETTINGS, 0).getBoolean("useTelegram", false))
                        if (!isAvailable) {
                            for (k in MainApplication.context!!.getSharedPreferences("customs$key", 0).all.keys) {
                                if (packName == k && MainApplication.context!!.getSharedPreferences("customs$key", 0).getBoolean(k, false)) {
                                    isAvailable = true
                                    break
                                }
                            }
                        }
                        //TODO: isAvailable에 패키지 추가하면 위에있는 패키지 목록도 갱신해야함

                        if (isAvailable) {
                            Log.d("isAvailable", "true")

                            val fisGroupChat = isGroupChat

                            val imageDB = ImageDB(photo!!)


                            val thr = Thread(Runnable { callResponder(key, room, msg, sender, fisGroupChat, imageDB, packName, act, false) })

                            thr.start()
                        }
                    }

                    break

                }
            }
        }


    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        //Log.i("Msg", "Notification Removed");

    }

    companion object {
        var debugRoom: String? = null
        var container: MutableMap<String, ScriptsManager> = HashMap()
        var UIHandler: Handler? = Handler()
        var execScope: ScriptableObject? = null
        lateinit var rootView: View
        var SavedSessions: MutableMap<String?, Notification.Action> = HashMap()
        private val basePath = MainApplication.basePath
        // static File sessionsPath = new File(basePath + File.separator + "Sessions");
        private val banNameArr = HashMap<String, Array<String>>()
        private val banRoomArr = HashMap<String, Array<String>>()
        private val isCompiling = HashMap<String, Boolean>()

        private fun resetSession() {
            try {

                SavedSessions.clear()


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun getSession(room: String): Notification.Action? {
            return SavedSessions[room]
        }

        fun hasSession(room: String): Boolean {
            return SavedSessions[room] != null

        }

        fun callResponder(scriptName: String, room: String?, msg: String, sender: String, isGroupChat: Boolean, imageDB: ImageDB, packName: String, session: Notification.Action, isDebugMode: Boolean) {


            val execScope = container[scriptName]!!.getExecScope()
            val responder = container[scriptName]!!.getResponder()


            if (!isDebugMode) {
                SavedSessions[room] = session
            }

            try {
                Context.enter()
                val parseContext = RhinoAndroidHelper().enterContext()
                parseContext.wrapFactory = PrimitiveWrapFactory()
                parseContext.languageVersion = Context.VERSION_ES6
                parseContext.optimizationLevel = container[scriptName]!!.getOptimization()
                Api.isDebugMode = isDebugMode

                MainApplication.context!!.getSharedPreferences("log", 0).edit().putString("logTarget", scriptName).apply()
                if (responder != null) {
                    if (MainApplication.context!!.getSharedPreferences("settings$scriptName", 0).getBoolean("useUnifiedParams", false)) {
                        responder.call(parseContext, execScope, execScope, arrayOf<Any>(ResponseParameters(room!!, msg, sender, isGroupChat, SessionCacheReplier(room), imageDB, packName)))
                    } else {
                        responder.call(parseContext, execScope, execScope, arrayOf(room!!, msg, sender, isGroupChat, SessionCacheReplier(room), imageDB, packName))
                    }
                }

                Context.exit()
            } catch (e: Throwable) {


                Log.e("parser", "?", e)
                val stack = StringBuilder()
                stack.append("\n")
                if (MainApplication.context!!.getSharedPreferences("settings$scriptName", 0).getBoolean("specificLog", false)) {
                    for (element in e.stackTrace) {
                        stack.append("at ").append(element.toString())
                        stack.append("\n")
                    }
                }
                com.xfl.kakaotalkbot.Log.error(e.toString() + stack.toString(), true)
                UIHandler!!.post {
                    if (MainApplication.context!!.getSharedPreferences("settings$scriptName", 0).getBoolean("offOnRuntimeError", true)) {
                        ScriptSelectActivity.putOn(scriptName, false)
                    }
                }

            }

        }

        internal fun initializeBanList(scriptName: String) {
            val ctx = MainApplication.context!!
            val banRoom = ctx.getSharedPreferences("bot", 0).getString("banRoom$scriptName", "")
            val banName = ctx.getSharedPreferences("bot", 0).getString("banName$scriptName", "")
            banRoomArr[scriptName] = banRoom!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            banNameArr[scriptName] = banName!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        }

        fun initializeAll(isManual: Boolean) {//isManual: true on Api.reload
            basePath.mkdir()
            val files = basePath.listFiles()
            for (k in files) {
                if (k.name.endsWith(".js")) {


                    initializeScript(k.name, isManual)


                }
            }
        }

        fun initializeScript(scriptName: String, isManual: Boolean): Boolean {

            /*if (isCompiling.get(scriptName) != null && isCompiling.get(scriptName)) {
            return false;
        }*/
            isCompiling[scriptName] = true
            MainApplication.context!!.getSharedPreferences("log", 0).edit().putString("logTarget", scriptName).apply()
            val PREF_SETTINGS = "settings$scriptName"
            val optimization = MainApplication.context!!.getSharedPreferences(PREF_SETTINGS, 0).getInt("optimization", -1)
            val script = File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + scriptName)

            if (container[scriptName] != null) {
                //execScope = container.get(scriptName).execScope;
            }
            val responder: Function?


            com.xfl.kakaotalkbot.Log.info(MainApplication.context!!.resources.getString(R.string.snackbar_compileStart)+": $scriptName")

            val parseContext: Context
            try {
                parseContext = RhinoAndroidHelper().enterContext()
                parseContext.wrapFactory = PrimitiveWrapFactory()
                parseContext.languageVersion = Context.VERSION_ES6
                parseContext.optimizationLevel = optimization
            } catch (e: Exception) {
                if (!isManual) {
                    UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(scriptName, false, false) }
                    Context.reportError(e.toString())
                }
                return false
            }

            if (container[scriptName] != null) {
                if (container[scriptName]!!.getOnStartCompile() != null) {
                    container[scriptName]!!.getOnStartCompile()!!.call(parseContext, execScope, execScope, arrayOf<Any>())
                }

            }

            System.gc()
            if (MainApplication.context!!.getSharedPreferences("publicSettings", 0).getBoolean("resetSession", false))
                resetSession()
            val scope: ScriptableObject

            try {

                parseContext.languageVersion = Context.VERSION_ES6
                scope = parseContext.initStandardObjects(ImporterTopLevel(parseContext)) as ScriptableObject
                val fileReader = FileReader(script)
                val script_real = parseContext.compileReader(fileReader, scriptName, 0, null)
                fileReader.close()

                // ScriptableObject.putProperty(scope, "DataBase", Context.javaToJS(new DataBase(), scope));
                // ScriptableObject.putProperty(scope, "Api", Context.javaToJS(new Api(), scope));
                // ScriptableObject.putProperty(scope, "Utils", Context.javaToJS(new Utils(), scope));
                //ScriptableObject.putProperty(scope, "Log", Context.javaToJS(new com.xfl.kakaotalkbot.Log(), scope));

                ScriptableObject.defineClass(scope, Api::class.java)
                ScriptableObject.defineClass(scope, DataBase::class.java)
                ScriptableObject.defineClass(scope, Utils::class.java)
                ScriptableObject.defineClass(scope, com.xfl.kakaotalkbot.Log::class.java)
                ScriptableObject.defineClass(scope, AppData::class.java)
                ScriptableObject.defineClass(scope, Bridge::class.java)
                ScriptableObject.defineClass(scope, Device::class.java)
                ScriptableObject.defineClass(scope, FileStream::class.java)
                execScope = scope


                script_real.exec(parseContext, scope)
                if (scope.has("response", scope)) {
                    responder = scope.get("response", scope) as Function
                } else {
                    responder = null
                }
                var onStartCompile: Function? = null
                var onCreate: Function? = null
                var onStop: Function? = null
                var onResume: Function? = null
                var onPause: Function? = null
                if (scope.has("onStartCompile", scope)) {
                    onStartCompile = scope.get("onStartCompile", scope) as Function
                }
                if (scope.has("onCreate", scope)) {
                    onCreate = scope.get("onCreate", scope) as Function
                }
                if (scope.has("onStop", scope)) {
                    onStop = scope.get("onStop", scope) as Function
                }
                if (scope.has("onResume", scope)) {
                    onResume = scope.get("onResume", scope) as Function
                }
                if (scope.has("onPause", scope)) {
                    onPause = scope.get("onPause", scope) as Function
                }
                container[scriptName] = ScriptsManager()
                        .setExecScope(execScope!!)
                        .setResponder(responder)
                        .setOnStartCompile(onStartCompile)
                        .setOptimization(optimization)
                        .setScope(scope)
                        .setScriptActivity(onCreate, onStop, onResume, onPause)

                Api.scriptName = scriptName
                Context.exit()

                com.xfl.kakaotalkbot.Log.info(MainApplication.context!!.resources.getString(R.string.snackbar_compiled)+": $scriptName")

                isCompiling[scriptName] = false
            } catch (e: Throwable) {

                if (UIHandler != null) {
                    UIHandler!!.post {
                        Log.e("parser", "?", e)
                        Toast.makeText(MainApplication.context!!, "Compile Error:" + e.toString(), Toast.LENGTH_LONG).show()
                        com.xfl.kakaotalkbot.Log.error(e.toString(), false)
                        ScriptSelectActivity.putOn(scriptName, false)
                    }
                }

                isCompiling[scriptName] = false
                if (!isManual) {
                    UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(scriptName, false, false) }
                    Context.reportError(e.toString())

                }
                return false

            }


            MainApplication.context!!.getSharedPreferences("lastCompileSuccess2", 0).edit().putLong(scriptName, Date().time).apply()

            UIHandler!!.post { Toast.makeText(MainApplication.context!!, MainApplication.context!!.resources.getString(R.string.snackbar_compiled) + ":" + scriptName, Toast.LENGTH_SHORT).show() }
            return true
        }
    }


}
