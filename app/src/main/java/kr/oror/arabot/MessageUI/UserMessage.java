package kr.oror.arabot.MessageUI;

public class UserMessage {

    private String message;
    private String sender;
    private final boolean isBot;
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