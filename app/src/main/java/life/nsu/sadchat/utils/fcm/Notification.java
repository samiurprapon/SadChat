package life.nsu.sadchat.utils.fcm;

public class Notification {

    String user;
    int icon;
    String body;
    String title;
    String receiver;

    public Notification() {
    }

    public Notification(String user, int icon, String body, String title, String receiver) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.receiver = receiver;
    }

    public String getUser() {
        return user;
    }

    public int getIcon() {
        return icon;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
