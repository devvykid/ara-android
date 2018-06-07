package com.xfl.kakaotalkbot;

import android.content.Intent;

import java.io.File;

public class ScriptSelectFunction {
    public static void showEditor(File script) {
        Intent intent = new Intent(MainApplication.getContext(), ScriptEditor.class);
        intent.putExtra("selectedScript", script.getName());
        MainApplication.getContext().startActivity(intent);
    }

    public static void showManage(File script) {
        Intent intent = new Intent(MainApplication.getContext(), SettingsScreen.class);
        intent.putExtra("selectedScript", script.getName());
        MainApplication.getContext().startActivity(intent);
    }

    public static void reload(File script) {
        NotificationListener.initializeScript(script.getName(), true);
    }
}
