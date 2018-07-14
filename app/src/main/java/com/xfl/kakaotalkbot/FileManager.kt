package com.xfl.kakaotalkbot

import java.io.File

object FileManager {
    fun read(script: File): String? {
        

            var str=script.inputStream().readBytes().toString(Charsets.UTF_8)
            script.inputStream().close()
            return str
        

        
    }

}
