package life.nsu.sadchat.models;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;
    private String seenTime;
    private String time;

    public Chat() {
        // default constructor
    }

    public Chat(String sender, String receiver, String message, boolean isSeen, String seenTime, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.seenTime = seenTime;
        this.time = time;
    }

    public Chat(String sender, String receiver, String message, boolean isSeen, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }


    public boolean isIsSeen() {
        return isSeen;
    }

    public String getTime() {
        return time;
    }

    public String getSeenTime() {
        return seenTime;
    }
}
