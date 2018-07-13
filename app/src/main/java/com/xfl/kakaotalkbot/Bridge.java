package com.xfl.kakaotalkbot;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class Bridge extends ScriptableObject {
    public Bridge() {

    }

    @JSStaticFunction
    public static ScriptableObject getScopeOf(String scriptName) {
        if (!isAllowed(scriptName)) {
            return null;
        }
        if (NotificationListener.Companion.getContainer().get(scriptName) == null) {
            //Context.reportError("java.lang.NullPointerException: The script '"+scriptName+"' is not compiled yet");

            return null;
        }
        try {
            return NotificationListener.Companion.getContainer().get(scriptName).getScope();
        } catch (Throwable e) {
            Context.reportError(e.toString());
            return null;
        }
    }

    @JSStaticFunction
    public static boolean isAllowed(String scriptName) {

        return MainApplication.getContext().getSharedPreferences("settings" + scriptName, 0).getBoolean("allowBridge", true);
    }

    public String getClassName() {
        return "Bridge";
    }


}
