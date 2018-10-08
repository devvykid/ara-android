package me.computerpark.ara_android

import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction

class Bridge : ScriptableObject() {

    override fun getClassName(): String {
        return "Bridge"
    }

    companion object {
        @JvmStatic
        @JSStaticFunction
        fun getScopeOf(scriptName: String): ScriptableObject? {
            if (!isAllowed(scriptName)) {
                return null
            }
            if (ScriptsManager.container[scriptName] == null) {
                //Context.reportError("java.lang.NullPointerException: The script '"+scriptName+"' is not compiled yet");

                return null
            }
            try {
                return ScriptsManager.container[scriptName]!!.getScope()
            } catch (e: Throwable) {
                Context.reportError(e.toString())
                return null
            }

        }

        @JvmStatic
        @JSStaticFunction
        fun isAllowed(scriptName: String): Boolean {

            return MainApplication.context!!.getSharedPreferences("settings$scriptName", 0).getBoolean("allowBridge", true)
        }
    }


}
