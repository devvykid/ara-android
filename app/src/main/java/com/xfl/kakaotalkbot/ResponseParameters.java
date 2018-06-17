package com.xfl.kakaotalkbot;



public class ResponseParameters {
    public String room;
    public String msg;
    public String sender;
    public boolean isGroupChat;
    public SessionCacheReplier replier;
    public ImageDB ImageDB;
    public String packageName;
    public ResponseParameters(String room, String msg, String sender, boolean isGroupChat, SessionCacheReplier replier, ImageDB imageDB,String packName){


        this.room=room;
        this.msg=msg;
        this.sender=sender;
        this.isGroupChat=isGroupChat;
        this.replier=replier;
        this.ImageDB=imageDB;
        this.packageName=packName;
    }
    /*
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

}
