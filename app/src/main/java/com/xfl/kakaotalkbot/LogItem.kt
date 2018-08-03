package com.xfl.kakaotalkbot

class LogItem {
    companion object {

        const val TYPE_ERROR = 0
        const val TYPE_INFO = 1
        const val TYPE_DEBUG = 2
        const val TYPE_WARN = 3

    }

    var logType: Int = 0
    var logMessage: String = ""
    var logTitle: String = ""


}