package edu.northeastern.hikerhub.hiker.fragment.home;

import android.location.Location;

public class Trail {
    private int id;
    private String name;
    private Location location;
    private double length;
    private long time;
    private String imageUrl;
    private int rate;
    public static final double AVERAGE_HIKING_SPEED_MPH = 2.5; // average hiking speed in miles per hour

    private Difficulty difficulty;
    private String description;


    public Trail(int id, String name, int x, int y, double length) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.length = length;
        this.time = calculateTimeTaken(length);

    }

    private static long calculateTimeTaken(double trailLength) {
        double distanceInMiles = trailLength; // distance in miles
        double speedInMph = AVERAGE_HIKING_SPEED_MPH; // speed in miles per hour
        double timeInHours = distanceInMiles / speedInMph; // time taken in hours
        long timeInseconds = (long) (timeInHours * 60 * 60); // convert to seconds
        return timeInseconds;
    }
    public String getTimeStr() {
        int hours = (int) (time / (60 * 60)); // convert to hours
        int minutes = (int) ((time / (60)) % 60); // convert to minutes
        String timeStr = hours + "h" + minutes + "m"; // concatenate into string
        return timeStr;
    }

    public Trail(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rate;
    }

    public void setRating(int rate) {
        this.rate = rate;
    }

    public double getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Trail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rate +
                ", length=" + length +
                ", difficulty=" + difficulty +
                '}';
    }
}
