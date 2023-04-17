package edu.northeastern.hikerhub.hiker.fragment.home;

import android.location.Location;

import com.google.protobuf.DescriptorProtos;

public class Trail {
    private String name;
    private double x;
    private double y;
    private double length;
    private Location location;
    private long time;
    private String imageUrl;
    private int rate;
    private int recommendCount;
    public static final double AVERAGE_HIKING_SPEED_MPH = 2.5; // average hiking speed in miles per hour

    private Difficulty difficulty = Difficulty.EASY;;

    public Trail() {}
    public Trail(double x, double y, String name, double length) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.length = length;
        this.location = new Location("");
        this.location.setLatitude(x);
        this.location.setLatitude(y);
        this.time = calculateTimeTaken(length);

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

    public Trail(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    @Override
    public String toString() {
        return "Trail{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", length=" + length +
                ", time=" + time +
                ", difficulty=" + difficulty +
                '}';
    }
}
