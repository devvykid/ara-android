package com.xfl.kakaotalkbot

import android.os.Environment
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Created by XFL on 2/20/2018.
 */

class DataBase : ScriptableObject() {


    override fun getClassName(): String {
        return "DataBase"
    }

    companion object {
        internal var dbDir = Environment.getExternalStorageDirectory().toString() + File.separator + "katalkbot" + File.separator + "Database"
        @JvmStatic
        @JSStaticFunction
        fun appendDataBase(fileName: String, data: String?): String? {
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
                var f = File(dbDir, fileName)
                f.mkdirs()
                f.createNewFile()
                var fw = FileWriter(f, true)

                fw.write(data)
                fw.close()
            } catch (e: Exception) {
                Context.reportError(e.message)
            }
            return getDataBase(fileName)
        }

        @JvmStatic
        @JSStaticFunction
        fun setDataBase(fileName: String, data: String?): String? {
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


                val file = File(dbDir + File.separator + fileName)
                file.mkdirs()
                file.createNewFile()
                file.bufferedWriter().use{ out-> out.write(data)
                }

            } catch (e: Exception) {
                Context.reportError(e.message)
            }
            return getDataBase(fileName)
        }

        @JvmStatic
        @JSStaticFunction
        fun getDataBase(fileName: String): String? {
            var fileName=fileName
            if (!fileName.contains(".")) {
                fileName += ".txt"
            }
            var f = File(dbDir + File.separator + fileName)
            f.mkdirs()
            f.createNewFile()
            if(!f.exists())return null
            try {
                return FileManager.read(f)
            } catch (e: IOException) {
                org.mozilla.javascript.Context.reportError(e.message)
            }

            return null
        }

        @JvmStatic
        @JSStaticFunction
        fun removeDataBase(fileName: String): Boolean {
            var fileName = fileName
            if (!fileName.contains(".")) {
                fileName += ".txt"
            }
            val file = File(dbDir + File.separator + fileName)
            if (!file.exists()) return false
            if (MainApplication.context!!.getSharedPreferences("settings", 0).getBoolean("onDeleteBackup", true)) {
                setDataBase("$fileName.bak", getDataBase(fileName))
            }

            return file.delete()
        }
    }
}