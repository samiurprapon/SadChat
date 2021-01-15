package life.nsu.sadchat.models;

public class ChatList {

    private String id;

    public ChatList() {
        // default constructor
    }

    public ChatList(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
