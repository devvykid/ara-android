package kr.oror.arabot

import java.io.File

object FileManager {
    fun read(script: File): String? {


        val str = script.inputStream().readBytes().toString(Charsets.UTF_8)
        script.inputStream().close()
        return str


    }

}
