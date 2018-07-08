package com.xfl.kakaotalkbot;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class Device extends ScriptableObject {
    @JSStaticFunction
    public static Object getBuild() {
        return new Build();
    }

    @JSStaticFunction
    public static int getAndroidVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    @JSStaticFunction
    public static String getAndroidVersionName() {
        return Build.VERSION.RELEASE;

    }

    @JSStaticFunction
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    @JSStaticFunction
    public static String getPhoneModel() {
        return Build.DEVICE;
    }

    @JSStaticFunction
    public static boolean isCharging() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        return plug == BatteryManager.BATTERY_PLUGGED_AC || plug == BatteryManager.BATTERY_PLUGGED_USB || plug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    @JSStaticFunction
    public static String getPlugType() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        switch (plug) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "ac";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "wireless";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "usb";
        }
        return "unknown";
    }

    @JSStaticFunction
    public static int getBatteryLevel() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    @JSStaticFunction
    public static int getBatteryHealth() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
    }

    @JSStaticFunction
    public static int getBatteryTemperature() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
    }

    @JSStaticFunction
    public static int getBatteryVoltage() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    }

    @JSStaticFunction
    public static int getBatteryStatus() {
        Intent batteryStatus = MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    }
    @JSStaticFunction
    public static Intent getBatteryIntent(){
        return MainApplication.getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    public String getClassName() {
        return "Device";
    }
}
