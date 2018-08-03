package com.xfl.kakaotalkbot

import org.mozilla.javascript.Function
import org.mozilla.javascript.ScriptableObject

class ScriptContainer {
    private var responder: Function? = null
    private lateinit var execScope: ScriptableObject
    private var onStartCompile: Function? = null
    private var optimization: Int = 0
    private lateinit var scope: ScriptableObject

    var onCreate: Function? = null
        private set
    var onStop: Function? = null
        private set
    var onResume: Function? = null
        private set
    var onPause: Function? = null
        private set

    @Deprecated("")
    constructor(responder: Function, execScope: ScriptableObject, onStartCompile: Function, scope: ScriptableObject) {
        this.responder = responder
        this.execScope = execScope
        this.onStartCompile = onStartCompile
        this.scope = scope
    }

    constructor() {}

    fun setResponder(responder: Function?): ScriptContainer {
        this.responder = responder
        return this
    }

    fun setExecScope(execScope: ScriptableObject): ScriptContainer {
        this.execScope = execScope
        return this
    }

    fun setOnStartCompile(onStartCompile: Function?): ScriptContainer {
        this.onStartCompile = onStartCompile
        return this
    }

    fun setOptimization(optimization: Int): ScriptContainer {
        this.optimization = optimization
        return this
    }

    fun getScope(): ScriptableObject? {
        return scope
    }

    fun setScope(scope: ScriptableObject): ScriptContainer {
        this.scope = scope
        return this
    }

    fun setScriptActivity(onCreate: Function?, onStop: Function?, onResume: Function?, onPause: Function?): ScriptContainer {
        this.onCreate = onCreate
        this.onStop = onStop
        this.onResume = onResume
        this.onPause = onPause
        return this
    }

    fun getExecScope(): ScriptableObject {
        return execScope
    }

    fun getOnStartCompile(): Function? {
        return onStartCompile
    }

    fun getResponder(): Function? {
        return responder
    }

    fun getOptimization(): Int {
        return optimization
    }
}