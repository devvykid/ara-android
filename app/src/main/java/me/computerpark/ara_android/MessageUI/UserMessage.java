package me.computerpark.ara_android.MessageUI;

public class UserMessage {

    String message;
    String sender;
    boolean isBot;
    long createdAt;

    public UserMessage(boolean isBot, String message, String sender) {
        this.isBot = isBot;
        this.message = message;
        this.sender = sender;
    }

    public boolean getIsBot() {
        return isBot;
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