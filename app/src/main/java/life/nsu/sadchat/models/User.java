package life.nsu.sadchat.models;

public class User {
    private String id;
    private String username;
    private String phone;
    private String image;
    private String bio;
    private String activeStatus;

    public User() {

    }

    public User(String id, String username, String phone, String image, String bio, String activeStatus) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.bio = bio;
        this.activeStatus = activeStatus;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    public String getBio() {
        return bio;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

}
