package com.xfl.kakaotalkbot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SessionCacheReplier {

    private Notification.Action session = null;
    private String room;


    public SessionCacheReplier(String room) {
        super();
        this.room = room;

        //this.session = session;
    }


    private void replyTo(String value) {

        if (session == null) return;

        Intent sendIntent = new Intent();

        Bundle msg = new Bundle();

        for (RemoteInput inputable : session.getRemoteInputs())
            msg.putCharSequence(inputable.getResultKey(), value);

        RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);


        try {

            session.actionIntent.send(MainApplication.getContext(), 0, sendIntent);

        } catch (PendingIntent.CanceledException e) {


        }


    }

    public void reply(final String value) {
        if (NotificationListener.debugRoom != null && this.room.equals(NotificationListener.debugRoom)) {
            DebugModeScreen.appendReply(value);
            return;
        }
        this.session = NotificationListener.getSession(this.room);
        replyTo(value);
    }

    public boolean reply(final String room, final String value) {
        return reply(room, value, false);
    }

    public boolean reply(final String room, final String value, final boolean hideToast) {
        if (NotificationListener.debugRoom != null && this.room.equals(NotificationListener.debugRoom)) {

            DebugModeScreen.appendReply(value);
            return true;
        } else {
            try {
                if (NotificationListener.getRoomNum(room) != -1) {
                    this.session = NotificationListener.getSession(room);
                    replyTo(value);
                    return true;
                } else {
                    if (!hideToast) {
                        NotificationListener.UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainApplication.getContext(), "아직 " + room + "방의 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    }

}