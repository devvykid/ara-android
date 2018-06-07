package com.xfl.kakaotalkbot;

import android.app.Application;
import android.content.Context;


/**
 * Created by XFL on 2/21/2018.
 */

public class MainApplication extends Application {
    private static Context sContext;

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        sContext = getApplicationContext();

    }


}
