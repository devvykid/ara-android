package com.xfl.kakaotalkbot;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class AppData extends ScriptableObject {
    public AppData() {
    }

    @JSStaticFunction
    public static void putBoolean(String key, boolean bool) {
        MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).edit().putBoolean(key, bool).apply();
    }

    @JSStaticFunction
    public static boolean getBoolean(String key, boolean defaultValue) {
        return MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).getBoolean(key, defaultValue);
    }

    @JSStaticFunction
    public static void putInt(String key, int integer) {
        MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).edit().putInt(key, integer).apply();
    }

    @JSStaticFunction
    public static Integer getInt(String key, int defaultValue) {
        return MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).getInt(key, defaultValue);
    }

    @JSStaticFunction
    public static void putString(String key, String string) {
        MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).edit().putString(key, string).apply();
    }

    @JSStaticFunction
    public static String getString(String key, String defaultValue) {
        return MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).getString(key, defaultValue);
    }

    @JSStaticFunction
    public static void remove(String key) {
        MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).edit().remove(key).apply();
    }

    @JSStaticFunction
    public static void clear() {
        MainApplication.getContext().getSharedPreferences("sharedbotdata", 0).edit().clear().apply();
    }

    @Override
    public String getClassName() {
        return "AppData";
    }
}
