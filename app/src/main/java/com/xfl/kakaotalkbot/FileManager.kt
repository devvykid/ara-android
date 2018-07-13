package com.xfl.kakaotalkbot

import java.io.File

object FileManager {
    fun read(script: File): String? {
        try {

            var str=script.inputStream().readBytes().toString(Charsets.UTF_8)
            script.inputStream().close()
            return str
        } catch (e: Throwable) {
            MainApplication.reportInternalError(e)
        }

        return null
    }

}
