package com.xfl.kakaotalkbot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import com.faendir.rhino_android.RhinoAndroidHelper

import org.mozilla.javascript.ScriptableObject
import java.util.*

class ScriptActivity : AppCompatActivity() {
    private lateinit var parseCtx: org.mozilla.javascript.Context
    private lateinit var excScope: ScriptableObject
    private var scriptName: String? = null
    private var manager: ScriptsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scriptName = intent.extras!!.getString("scriptName")
        manager = NotificationListener.container[scriptName!!]

        if (manager == null) {
            Toast.makeText(this, resources.getString(R.string.please_compile_first), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (manager!!.onCreate == null) {
            Toast.makeText(this, "There is no function onCreate", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            parseCtx = RhinoAndroidHelper().enterContext()
            parseCtx.wrapFactory = PrimitiveWrapFactory()
            parseCtx.optimizationLevel = manager!!.getOptimization()
            excScope = manager!!.getExecScope()

            manager!!.onCreate!!.call(parseCtx, excScope, excScope, arrayOf(savedInstanceState, this))
        } catch (e: Throwable) {
            Log.error("onCreate Error(" + scriptName + "):" + e.message, true)
        }

    }

    override fun onStop() {
        super.onStop()
        if (manager == null) {
            return
        }
        if (manager!!.onStop == null) {
            Toast.makeText(this, "There is no function onStop", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            manager!!.onStop!!.call(parseCtx, excScope, excScope, arrayOf<Any>(this))

        } catch (e: Throwable) {
            Log.error("onStop Error(" + scriptName + "):" + e.message, true)
        }

    }

    override fun onResume() {
        super.onResume()
        if (manager == null) {
            return
        }
        if (manager!!.onResume == null) {
            Toast.makeText(this, "There is no function onResume", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            manager!!.onResume!!.call(parseCtx, excScope, excScope, arrayOf<Any>(this))
        } catch (e: Throwable) {
            Log.error("onResume Error(" + scriptName + "):" + e.message, true)
        }

    }

    override fun onPause() {
        super.onPause()
        if (manager == null) {
            return
        }
        if (manager!!.onPause == null) {
            Toast.makeText(this, "There is no function onPause", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            manager!!.onPause!!.call(parseCtx, excScope, excScope, arrayOf<Any>(this))
        } catch (e: Throwable) {
            Log.error("onPause Error(" + scriptName + "):" + e.message, true)
        }

    }

}
