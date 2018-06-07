package com.xfl.kakaotalkbot;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

public class ScriptsManager {
    public static String scriptName;
    public static boolean isDebugMode;
    public Function responder;
    public ScriptableObject execScope;
    public Function onStartCompile;
    public int optimization;
    public ScriptableObject scope;

    @Deprecated
    public ScriptsManager(Function responder, ScriptableObject execScope, Function onStartCompile, ScriptableObject scope) {
        this.responder = responder;
        this.execScope = execScope;
        this.onStartCompile = onStartCompile;
        this.scope = scope;
    }

    public ScriptsManager() {
    }

    public ScriptsManager setResponder(Function responder) {
        this.responder = responder;
        return this;
    }

    public ScriptsManager setExecScope(ScriptableObject execScope) {
        this.execScope = execScope;
        return this;
    }

    public ScriptsManager setOnStartCompile(Function onStartCompile) {
        this.onStartCompile = onStartCompile;
        return this;
    }

    public ScriptsManager setOptimization(int optimization) {
        this.optimization = optimization;
        return this;
    }

    public ScriptableObject getScope() {
        return scope;
    }

    public ScriptsManager setScope(ScriptableObject scope) {
        this.scope = scope;
        return this;
    }

}
