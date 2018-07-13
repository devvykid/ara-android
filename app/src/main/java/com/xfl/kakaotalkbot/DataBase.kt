package com.xfl.kakaotalkbot

import android.os.Environment

import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Created by XFL on 2/20/2018.
 */

class DataBase : ScriptableObject() {


    override fun getClassName(): String {
        return "DataBase"
    }

    companion object {
        internal var dbDir = File(Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + "Database")

        @JSStaticFunction
        fun setDataBase(fileName: String, data: String?) {
            var fileName = fileName
            var data = data
            try {

                if (MainApplication.context!!.getSharedPreferences("compatibility", 0).getBoolean("JBBot", false)) {
                    if (!data!!.contains(".")) {
                        data += ".txt"
                    }
                    val temp = data
                    data = fileName
                    fileName = temp
                } else {
                    if (!fileName.contains(".")) {
                        fileName += ".txt"
                    }
                }

                dbDir.mkdirs()
                val file = File(dbDir, fileName)

                file.createNewFile()
                file.bufferedWriter().use{
                    out-> out.write(data)
                }

            } catch (e: Exception) {
                MainApplication.reportInternalError(e)
            }

        }

        @JSStaticFunction
        fun getDataBase(fileName: String): String? {
            var fileName=fileName

            try {
                return FileManager.read(File(fileName))
            } catch (e: IOException) {
                MainApplication.reportInternalError(e)
            }

            return null
        }

        @JSStaticFunction
        fun removeDataBase(fileName: String): Boolean {
            var fileName = fileName
            if (!fileName.contains(".")) {
                fileName += ".txt"
            }
            val file = File(dbDir, fileName)
            if (!file.exists()) return false
            if (MainApplication.context!!.getSharedPreferences("settings", 0).getBoolean("onDeleteBackup", true)) {
                setDataBase("$fileName.bak", getDataBase(fileName))
            }

            return file.delete()
        }
    }
}