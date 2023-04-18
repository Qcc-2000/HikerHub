package edu.northeastern.hikerhub.hiker.fragment.home;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.TableLayout;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CsvReader {
    private static final String TAG = "CsvReader";

    public static Map<String, Trail>  readCsvFromAssets(Context context, String filename) {
        Map<String, Trail> trailMap = new HashMap<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);

            String[] nextLine;
            int num = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (num == 0) {
                    num++;
                    continue;
                }
                // Process the next line of the CSV file

                String name = nextLine[2];
                double length = Double.parseDouble(nextLine[3]);
                double longitude = Double.parseDouble(nextLine[4]);
                double latitude = Double.parseDouble(nextLine[5]);
                Trail trail = new Trail(latitude,longitude,name,length);
                trailMap.put(name, trail);
                num++;
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return trailMap;
    }
}
