package kr.oror.arabot

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction

class Device : ScriptableObject() {


    override fun getClassName(): String {
        return "Device"
    }

    companion object {
        val build: Any
            @JvmStatic
            @JSStaticFunction
            get() = Build()

        val androidVersionCode: Int
            @JvmStatic
            @JSStaticFunction
            get() = Build.VERSION.SDK_INT

        val androidVersionName: String
            @JvmStatic
            @JSStaticFunction
            get() = Build.VERSION.RELEASE

        val phoneBrand: String
            @JvmStatic
            @JSStaticFunction
            get() = Build.BRAND

        val phoneModel: String
            @JvmStatic
            @JSStaticFunction
            get() = Build.DEVICE

        val isCharging: Boolean
            @JvmStatic
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

                return plug == BatteryManager.BATTERY_PLUGGED_AC || plug == BatteryManager.BATTERY_PLUGGED_USB || plug == BatteryManager.BATTERY_PLUGGED_WIRELESS
            }

        val plugType: String
            @JvmStatic
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
            @JvmStatic
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            }

        val batteryHealth: Int
            @JvmStatic
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            }

        val batteryTemperature: Int
            @JvmStatic
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            }

        val batteryVoltage: Int
            @JvmStatic
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            }

        val batteryStatus: Int
            @JvmStatic
            @JSStaticFunction
            get() {
                val batteryStatus = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            }
        val batteryIntent: Intent
            @JvmStatic
            @JSStaticFunction
            get() = MainApplication.context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }
}
