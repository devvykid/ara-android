package com.xfl.kakaotalkbot

import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class SessionCacheReplier(private val room: String)//this.session = session;
{

    private var session: Notification.Action? = null


    private fun replyTo(value: String?) {

        if (session == null) return

        val sendIntent = Intent()

        val msg = Bundle()

        for (inputable in session!!.remoteInputs)
            msg.putCharSequence(inputable.resultKey, value)

        RemoteInput.addResultsToIntent(session!!.remoteInputs, sendIntent, msg)


        try {

            session!!.actionIntent.send(MainApplication.context, 0, sendIntent)

        } catch (e: PendingIntent.CanceledException) {


        }


    }

    fun reply(value: String?) {
        if (NotificationListener.debugRoom != null && this.room == NotificationListener.debugRoom) {
            DebugModeScreen.appendReply(value)
            return
        }
        this.session = NotificationListener.getSession(this.room)
        replyTo(value)
    }

    @JvmOverloads
    fun reply(room: String?, value: String?, hideToast: Boolean = false): Boolean {

        if (NotificationListener.debugRoom != null && this.room == NotificationListener.debugRoom) {

            DebugModeScreen.appendReply(value)
            return true
        } else {
            try {

                return if (NotificationListener.hasSession(room)) {
                    this.session = NotificationListener.getSession(room)
                    replyTo(value)
                    true
                } else {
                    if (!hideToast) {
                        NotificationListener.UIHandler!!.post { Toast.makeText(MainApplication.context, "아직 " + room + "방의 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show() }
                    }

                    false
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

    }

}