package edu.northeastern.hikerhub.hiker.fragment.Profile;

public class User {
    public String name;
    public String location;
    public String hikingLevel;
    public String profilePictureUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String location, String hikingLevel, String profilePictureUrl) {
        this.name = name;
        this.location = location;
        this.hikingLevel = hikingLevel;
        this.profilePictureUrl = profilePictureUrl;
    }
}
