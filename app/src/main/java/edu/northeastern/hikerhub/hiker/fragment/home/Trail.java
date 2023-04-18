package edu.northeastern.hikerhub.hiker.fragment.home;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Trail {
    private String name;
    private double latitude;
    private double longitude;
    private double length;
    private Location location;
    private long time;
    private String imgUrl;
    private int recommendCount;
    public static final double AVERAGE_HIKING_SPEED_MPH = 2.5; // average hiking speed in miles per hour

    private Difficulty difficulty = Difficulty.EASY;

    public Trail() {}
    public Trail(double x, double y, String name, double length) {
        this.latitude = x;
        this.longitude = y;
        this.name = name;
        this.length = length;
        this.location = new Location("");
        this.location.setLatitude(x);
        this.location.setLatitude(y);
        this.time = calculateTimeTaken(length);
        this.imgUrl = ImgUrlGenerator.getInstance().getImgUrl();

        int hours = (int) (time / (60 * 60)); // convert to hours
        if (hours >= 1 && hours < 2) {
            this.difficulty = Difficulty.MODERATE;
        } else if (hours >= 2){
            this.difficulty = Difficulty.HARD;
        }

    }


    private long calculateTimeTaken(double trailLength) {
        double distanceInMiles = trailLength; // distance in miles
        double speedInMph = AVERAGE_HIKING_SPEED_MPH; // speed in miles per hour
        double timeInHours = distanceInMiles / speedInMph; // time taken in hours
        return (long) (timeInHours * 60 * 60);// convert to seconds
    }
    public String getTimeStr() {
        int hours = (int) (time / (60 * 60)); // convert to hours
        int minutes = (int) ((time / (60)) % 60); // convert to minutes
        String timeStr = hours + "h" + minutes + "m"; // concatenate into string
        return timeStr;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLenAndTime() {
        return String.format("%.2f", length) +"mi Est." + getTimeStr();
    }

    public Location getLocation() {
        return location;
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Trail{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", length=" + length +
                ", location=" + location +
                ", time=" + time +
                ", recommendCount=" + recommendCount +
                ", difficulty=" + difficulty +
                '}';
    }
}
