package com.xfl.kakaotalkbot;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by XFL on 2/21/2018.
 */

public class MainApplication extends Application {
    public static File basePath = new File(Environment.getExternalStorageDirectory() + File.separator + "katalkbot");
    private static Context sContext;

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    public static void reportInternalError(Throwable e) {

        NotificationListener.UIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainApplication.getContext(), MainApplication.getContext().getResources().getString(R.string.internal_error), Toast.LENGTH_LONG).show();
            }
        });
        StringBuilder stack = new StringBuilder();
        stack.append("\n");

        for (StackTraceElement element : e.getStackTrace()) {
            stack.append("at ").append(element.toString());
            stack.append("\n");
        }
        final String formed = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(new Date()) + (MainApplication.getContext().getResources().getString(R.string.internal_error) + "\n" + e.toString() + stack.toString()).replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");

        com.xfl.kakaotalkbot.Log.internalError("<font color=RED>" + formed + "</font><br><br>");
        Log.e("App Internal Error", "", e);

    }

    @Override
    public void onCreate() {
        super.onCreate();


        sContext = getApplicationContext();

    }


}
