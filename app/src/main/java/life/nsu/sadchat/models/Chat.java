package life.nsu.sadchat.models;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean seen;
    private String time;

    public Chat() {
        // default constructor
    }

    public Chat(String sender, String receiver, String message, boolean isSeen, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.seen = isSeen;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
