package com.xfl.kakaotalkbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();

        String action = intent.getStringExtra("action");
        if (action.equals("reload")) {
            Toast.makeText(context, context.getResources().getString(R.string.snackbar_compiling), Toast.LENGTH_SHORT).show();
            NotificationListener.initializeAll(true);

        } else if (action.equals("off")) {
            ScriptSelectActivity.putOnAll(false);


        }

    }


}