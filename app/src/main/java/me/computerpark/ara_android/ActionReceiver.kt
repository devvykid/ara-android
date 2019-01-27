package me.computerpark.ara_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.widget.Toast

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Toast.makeText(context,"received",Toast.LENGTH_SHORT).show();

        val action = intent.getStringExtra("action")
        if (action == "reload") {

            Toast.makeText(context, context.resources.getString(R.string.snackbar_compiling), Toast.LENGTH_SHORT).show()
            //Snackbar.make(v, R.string.snackbar_script_saved, Snackbar.LENGTH_SHORT).setAction(R.string.btn_dismiss, null).show()

            ScriptsManager.initializeAll(true)

        } else if (action == "off") {
            ScriptSelectActivity.putOnAll(false)


        }

    }


}