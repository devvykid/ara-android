package com.xfl.kakaotalkbot

import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.faendir.rhino_android.RhinoAndroidHelper
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject
import java.io.File
import java.io.FileReader
import java.util.*

class ScriptsManager {

    companion object {
        var execScope: ScriptableObject? = null
        var container: MutableMap<String, ScriptContainer> = HashMap()
        val isCompiling = HashMap<String, Boolean>()
        var scriptName: String? = null
        fun initializeScript(scriptName: String, isManual: Boolean, ignoreError: Boolean): Boolean {

            /*if (isCompiling.get(scriptName) != null && isCompiling.get(scriptName)) {
            return false;
        }*/
            isCompiling[scriptName] = true
            MainApplication.context!!.getSharedPreferences("log", 0).edit().putString("logTarget", scriptName).apply()
            val PREF_SETTINGS = "settings$scriptName"
            val optimization = MainApplication.context!!.getSharedPreferences(PREF_SETTINGS, 0).getInt("optimization", -1)
            val script = File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + scriptName)

            if (ScriptsManager.container[scriptName] != null) {
                //execScope = container.get(scriptName).execScope;
            }
            val responder: Function?


            com.xfl.kakaotalkbot.Log.info(MainApplication.context!!.resources.getString(R.string.snackbar_compileStart) + ": $scriptName")

            val parseContext: Context
            try {
                parseContext = RhinoAndroidHelper().enterContext()
                parseContext.wrapFactory = PrimitiveWrapFactory()
                parseContext.languageVersion = Context.VERSION_ES6
                parseContext.optimizationLevel = optimization
            } catch (e: Exception) {
                if (!isManual) {
                    NotificationListener.UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(scriptName, false, false) }
                    Context.reportError(e.toString())
                }
                return false
            }



            System.gc()
            if (MainApplication.context!!.getSharedPreferences("publicSettings", 0).getBoolean("resetSession", false))
                NotificationListener.resetSession()
            val scope: ScriptableObject

            try {
                if (container[scriptName] != null) {
                    if (container[scriptName]!!.getOnStartCompile() != null) {
                        container[scriptName]!!.getOnStartCompile()!!.call(parseContext, execScope, execScope, arrayOf<Any>())
                    }
                }
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
                container[scriptName] = ScriptContainer()
                        .setExecScope(execScope!!)
                        .setResponder(responder)
                        .setOnStartCompile(onStartCompile)
                        .setOptimization(optimization)
                        .setScope(scope)
                        .setScriptActivity(onCreate, onStop, onResume, onPause)

                Api.scriptName = scriptName
                Context.exit()

                com.xfl.kakaotalkbot.Log.info(MainApplication.context!!.resources.getString(R.string.snackbar_compiled) + ": $scriptName")

                isCompiling[scriptName] = false
            } catch (e: Throwable) {

                container[scriptName]?.setOnStartCompile(null)
                if (NotificationListener.UIHandler != null) {
                    NotificationListener.UIHandler!!.post {
                        Log.e("parser", "?", e)
                        Toast.makeText(MainApplication.context!!, "Compile Error:" + e.toString(), Toast.LENGTH_LONG).show()
                        com.xfl.kakaotalkbot.Log.error(e.toString(), false)
                    }
                }

                isCompiling[scriptName] = false
                if (!isManual) {
                    NotificationListener.UIHandler!!.post { ScriptSelectActivity.refreshProgressBar(scriptName, false, false) }
                    if (!ignoreError)
                        Context.reportError(e.toString())

                }
                return false

            }


            MainApplication.context!!.getSharedPreferences("lastCompileSuccess2", 0).edit().putLong(scriptName, Date().time).apply()

            NotificationListener.UIHandler!!.post { Toast.makeText(MainApplication.context!!, MainApplication.context!!.resources.getString(R.string.snackbar_compiled) + ":" + scriptName, Toast.LENGTH_SHORT).show() }
            return true
        }

        fun initializeAll(isManual: Boolean) {//isManual: true on Api.reload
            MainApplication.basePath.mkdir()
            val files = MainApplication.basePath.listFiles()
            for (k in files) {
                if (k.name.endsWith(".js")) {

                    if (isCompiling[k.name] == null || !isCompiling[k.name]!!)
                    initializeScript(k.name, isManual, true)


                }
            }
        }
     }


}
