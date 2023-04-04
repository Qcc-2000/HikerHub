package edu.northeastern.hikerhub;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.northeastern.hikerhub.databinding.ActivityHikeBinding;

public class HikeActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;
    Chronometer chronometer;
    private GoogleMap mMap;
    private ActivityHikeBinding binding;
    FusedLocationProviderClient mFusedLocationClient;
    TextView distanceTextView, elevationTextView, avgSpeedTextView, stepTextView;
    int PERMISSION_ID = 44;
    private double lastLatitude;
    private double lastLongitude;
    private double lastElevation;
    private double recordLatitude;
    private double recordLongitude;
    private double distance = 0.0;
    private double avg_speed = 0.0;
    private boolean startCalDistance = false;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSensorManager();
        binding = ActivityHikeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        distanceTextView = (TextView) findViewById(R.id.distance);
        elevationTextView = (TextView) findViewById(R.id.elevation);
        chronometer = findViewById(R.id.timer);
        avgSpeedTextView = (TextView) findViewById(R.id.avg_speed);
        stepTextView = (TextView) findViewById(R.id.step);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private void initSensorManager() {

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        stepTextView.setText("Step:" + event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void back(View view) {
        finish();
    }

    public void startCal(MenuItem item) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        if (startCalDistance == false) {
            startCalDistance = true;
            recordLatitude = lastLatitude;
            recordLongitude = lastLongitude;
        }
        else {
            startCalDistance = false;
            reset();
        }
    }

    public void reset() {
        distance = 0.;
        avg_speed = 0.;
        refreshDistance();
    }

    private void refreshDistance() {
        distanceTextView.setText("travel distance: " + distance);
        elevationTextView.setText("elevation: " + lastElevation);
        avgSpeedTextView.setText("Avg speed: " + avg_speed);
    }

    private void getLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData();
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLatitude();

            if (startCalDistance) {
                distance += getDistance(lastLatitude, latitude, lastLongitude, longitude);
                refreshDistance();
            } else {
                startCalDistance = true;
            }
            lastLatitude = latitude;
            lastLongitude = longitude;
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {

        ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (!result){
                    System.out.println("No!");
                }
            }
        });
        activityResultLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLocation();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLatitude();
        lastLatitude = latitude;
        lastLongitude = longitude;
        lastElevation = location.getAltitude();
        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(position).title("Your Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        elevationTextView.setText((int) lastElevation);
        if (startCalDistance == true) {
            distance = getDistance(recordLatitude, latitude, recordLongitude, longitude);
            refreshDistance();
            avg_speed = distance / SystemClock.elapsedRealtime()- chronometer.getBase();
        }
    }

    private double getDistance(double lat1, double lat2, double lon1,
                               double lon2) {

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        distance = savedInstanceState.getDouble("distance", 0.);
        startCalDistance = savedInstanceState.getBoolean("startCalDistance", false);
        lastLatitude = savedInstanceState.getDouble("lastLatitude");
        lastLongitude = savedInstanceState.getDouble("lastLongitude");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putDouble("distance", distance);
        outState.putBoolean("startCalDistance", startCalDistance);
        outState.putDouble("lastLatitude", lastLatitude);
        outState.putDouble("lastLongitude", lastLongitude);
        super.onSaveInstanceState(outState);
    }

}
