package me.computerpark.ara_android

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        context = applicationContext

    }

    companion object {
        var basePath = File(Environment.getExternalStorageDirectory().toString() + File.separator + "arabot")
        /**
         * Returns the application context
         *
         * @return application context
         */
        var context: Context? = null
            private set

        @JvmStatic
        fun getContextForJava(): Context {
            return context!!
        }

        fun reportInternalError(e: Throwable) {

            NotificationListener.UIHandler!!.post { Toast.makeText(MainApplication.context, MainApplication.context!!.resources.getString(R.string.internal_error), Toast.LENGTH_LONG).show() }
            val stack = StringBuilder()
            stack.append("\n")

            for (element in e.stackTrace) {
                stack.append("at ").append(element.toString())
                stack.append("\n")
            }
            val formed = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(Date()) + (MainApplication.context!!.resources.getString(R.string.internal_error) + "\n" + e.toString() + stack.toString()).replace("<".toRegex(), "&lt;").replace(">".toRegex(), "&gt;").replace("\n".toRegex(), "<br>")

            me.computerpark.ara_android.Log.internalError("<font color=RED>$formed</font><br><br>")
            Log.e("App Internal Error", "", e)

        }
    }


}
