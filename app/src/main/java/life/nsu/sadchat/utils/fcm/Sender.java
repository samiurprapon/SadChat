package life.nsu.sadchat.utils.fcm;

public class Sender {
    Notification notification;
    String to;

    public Sender(Notification notification, String to) {
        this.notification = notification;
        this.to = to;
    }

}
