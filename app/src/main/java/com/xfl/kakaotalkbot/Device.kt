package com.xfl.kakaotalkbot

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader

class Device : ScriptableObject() {


    override fun getClassName(): String {
        return "Device"
    }

    companion object {
        val build: Any
            @JSStaticFunction
            get() = Build()

        val androidVersionCode: Int
            @JSStaticFunction
            get() = Build.VERSION.SDK_INT

        val androidVersionName: String
            @JSStaticFunction
            get() = Build.VERSION.RELEASE

        val phoneBrand: String
            @JSStaticFunction
            get() = Build.BRAND

        val phoneModel: String
            @JSStaticFunction
            get() = Build.DEVICE

        val isCharging: Boolean
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

                return plug == BatteryManager.BATTERY_PLUGGED_AC || plug == BatteryManager.BATTERY_PLUGGED_USB || plug == BatteryManager.BATTERY_PLUGGED_WIRELESS
            }

        val plugType: String
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                when (plug) {
                    BatteryManager.BATTERY_PLUGGED_AC -> return "ac"
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> return "wireless"
                    BatteryManager.BATTERY_PLUGGED_USB -> return "usb"
                }
                return "unknown"
            }

        val batteryLevel: Int
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            }

        val batteryHealth: Int
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            }

        val batteryTemperature: Int
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            }

        val batteryVoltage: Int
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            }

        val batteryStatus: Int
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            }
        val batteryIntent: Intent
            @JSStaticFunction
            get() = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }
}
