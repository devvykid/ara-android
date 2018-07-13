package com.xfl.kakaotalkbot

import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction

class AppData : ScriptableObject() {

    override fun getClassName(): String {
        return "AppData"
    }

    companion object {

        @JSStaticFunction
        fun putBoolean(key: String, bool: Boolean) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().putBoolean(key, bool).apply()
        }

        @JSStaticFunction
        fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).getBoolean(key, defaultValue)
        }

        @JSStaticFunction
        fun putInt(key: String, integer: Int) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().putInt(key, integer).apply()
        }

        @JSStaticFunction
        fun getInt(key: String, defaultValue: Int): Int? {
            return MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).getInt(key, defaultValue)
        }

        @JSStaticFunction
        fun putString(key: String, string: String) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().putString(key, string).apply()
        }

        @JSStaticFunction
        fun getString(key: String, defaultValue: String): String {
            return MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).getString(key, defaultValue)
        }

        @JSStaticFunction
        fun remove(key: String) {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().remove(key).apply()
        }

        @JSStaticFunction
        fun clear() {
            MainApplication.context!!.getSharedPreferences("sharedbotdata", 0).edit().clear().apply()
        }
    }
}
