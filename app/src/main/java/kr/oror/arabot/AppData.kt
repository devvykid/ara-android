package kr.oror.arabot

import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction

class AppData : ScriptableObject() {

    override fun getClassName(): String {
        return "AppData"
    }

    companion object {

        @JvmStatic
        @JSStaticFunction
        fun putBoolean(key: String, bool: Boolean) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().putBoolean(key, bool).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).getBoolean(key, defaultValue)
        }

        @JvmStatic
        @JSStaticFunction
        fun putInt(key: String, integer: Int) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().putInt(key, integer).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun getInt(key: String, defaultValue: Int): Int? {
            return MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).getInt(key, defaultValue)
        }

        @JvmStatic
        @JSStaticFunction
        fun putString(key: String, string: String) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().putString(key, string).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun getString(key: String, defaultValue: String): String {
            return MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).getString(key, defaultValue)
        }

        @JvmStatic
        @JSStaticFunction
        fun remove(key: String) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().remove(key).apply()
        }

        @JvmStatic
        @JSStaticFunction
        fun clear() {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().clear().apply()
        }
    }
}
