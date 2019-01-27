package me.computerpark.ara_android;


public class ResponseParameters {

    public int threadId;

    public ResponseParameters(String room, String msg, String sender, boolean isGroupChat, SessionCacheReplier replier, ImageDB imageDB, String packName/*,int threadId*/) {


        String room1 = room;

        String msg1 = msg;

        String sender1 = sender;

        boolean isGroupChat1 = isGroupChat;

        SessionCacheReplier replier1 = replier;

        me.computerpark.ara_android.ImageDB imageDB1 = imageDB;

        String packageName = packName;
//this.threadId=threadId;
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