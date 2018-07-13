package com.xfl.kakaotalkbot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();

        val action = intent.getStringExtra("action")
        if (action == "reload") {
            Toast.makeText(context, context.resources.getString(R.string.snackbar_compiling), Toast.LENGTH_SHORT).show()
            NotificationListener.initializeAll(true)

        } else if (action == "off") {
            ScriptSelectActivity.putOnAll(false)


        }

    }


}