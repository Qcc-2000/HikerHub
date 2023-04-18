package edu.northeastern.hikerhub.hiker.fragment.home;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Utils {
    private static Utils instance = null;
    private static Map<String, Trail> allTrails;//key: trail name
    private Utils(Context context, String pathName) {
        allTrails = new HashMap<>();
        initData(context, pathName);

    }
    private void initData(Context context, String pathName) {
        allTrails = CsvReader.readCsvFromAssets(context, pathName);
    }
    public static Utils getInstance(Context context, String pathName) {
        if (instance == null) {
            return new Utils(context, pathName);
        }
        return instance;
    }

    public static Map<String, Trail> getAllTrails() {
        return allTrails;
    }
    public static List<Trail> getTopTrails(double latitude, double longitude) {
        Location currentLocation = new Location("");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);

        //max heap
        PriorityQueue<Trail> nearbyTrailHeap = new PriorityQueue<>((t1, t2) -> {
            float dist1 = t1.getLocation().distanceTo(currentLocation);
            float dist2 = t2.getLocation().distanceTo(currentLocation);
            if (dist1 > dist2) {
                return -1;
            } else if (dist1 < dist2) {
                return 1;
            } else {
                return 0;
            }
        });
        return getTrailsDetail(nearbyTrailHeap);
    }
    public static List<Trail> getMostLikeTrails() {
        PriorityQueue<Trail> mostLikeTrailHeap =
                new PriorityQueue<>(Comparator.comparingInt(Trail::getRecommendCount));
        return getTrailsDetail(mostLikeTrailHeap);
    }

    private static List<Trail> getTrailsDetail(PriorityQueue<Trail> heap) {
        List<Trail> trails = new ArrayList<>();
        int size = 10;
        for (Trail trail : allTrails.values()) {
            heap.offer(trail);
            if (heap.size() > size) {
                heap.poll();
            }
        }
        while (!heap.isEmpty()) {
            trails.add(0,heap.poll());
        }
        return trails;
    }
}
