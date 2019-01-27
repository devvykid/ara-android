package kr.oror.arabot

import android.os.Environment
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Created by XFL, modified by 컴터박 on 2/20/2018.
 */

class DataBase : ScriptableObject() {


    override fun getClassName(): String {
        return "DataBase"
    }

    companion object {
        private var dbDir = Environment.getExternalStorageDirectory().toString() + File.separator + "arabot" + File.separator + "Database"
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
                val f = File(dbDir + File.separator + fileName)
                f.parentFile.mkdirs()
                f.createNewFile()
                val fw = FileWriter(f, true)

                fw.write(data)
                fw.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Context.reportError(e.toString())
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
                file.parentFile.mkdirs()

                file.createNewFile()

                file.absoluteFile.bufferedWriter().use { out ->
                    out.write(data)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Context.reportError(e.toString())
            }
            return getDataBase(fileName)
        }

        @JvmStatic
        @JSStaticFunction
        fun getDataBase(fileName: String): String? {
            var fileName = fileName
            if (!fileName.contains(".")) {
                fileName += ".txt"
            }
            val f = File(dbDir + File.separator + fileName)
            f.parentFile.mkdirs()

            if (!f.exists()) return null
            try {
                return FileManager.read(f.absoluteFile)
            } catch (e: IOException) {
                e.printStackTrace()
                org.mozilla.javascript.Context.reportError(e.toString())
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