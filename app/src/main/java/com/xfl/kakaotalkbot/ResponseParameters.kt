package com.xfl.kakaotalkbot


class ResponseParameters(var room: String, var msg: String, var sender: String, var isGroupChat: Boolean, var replier: SessionCacheReplier, var ImageDB: ImageDB, var packageName: String)/*
    @JSGetter
    public String getRoom() {
        return room;
    }

    @JSGetter
    public String getSender(){
        return sender;
    }
    @JSGetter
    public String getMsg(){
        return msg;
    }
    @JSGetter
    public boolean isGroupChat(){
        return isGroupChat;
    }
    @JSGetter
    public SessionCacheReplier replier(){
        return replier;
    }
*/
