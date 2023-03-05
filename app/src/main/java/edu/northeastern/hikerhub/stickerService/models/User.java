package edu.northeastern.hikerhub.stickerService.models;

public class User {

    public String username;
    public String fcmToken;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this(username, null);
    }

    public User(String username, String fcmToken) {
        this.username = username;
        this.fcmToken = fcmToken;
    }

}
