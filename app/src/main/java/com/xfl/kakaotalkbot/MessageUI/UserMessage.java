package com.xfl.kakaotalkbot.MessageUI;

public class UserMessage {

    String message;
    String sender;
    long createdAt;

    public UserMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getName() {
        return sender;
    }

    public String getText() {
        return message;
    }

    public UserMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public UserMessage setSender(String sender) {
        this.sender = sender;
        return this;
    }
}
