package com.xfl.kakaotalkbot

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

object FileManager {
    fun read(script: File): String? {
        try {
            script.createNewFile()
            script.bufferedReader().readLines().joinToString("\n")
        } catch (e: Exception) {
            MainApplication.reportInternalError(e)
        }

        return null
    }

}
