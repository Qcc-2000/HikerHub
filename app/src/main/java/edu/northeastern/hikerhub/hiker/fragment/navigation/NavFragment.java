package edu.northeastern.hikerhub.hiker.fragment.navigation;

import static android.content.Context.LOCATION_SERVICE;

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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.northeastern.hikerhub.R;

public class NavFragment extends Fragment implements OnMapReadyCallback, LocationListener, SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    Chronometer chronometer;
    private GoogleMap mMap;
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
    private Button start;
    private Button stop;
    private Button con;
    private Button clean;
    private long pause;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav, container, false);
        initSensorManager();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);
        distanceTextView = view.findViewById(R.id.distance);
        elevationTextView = view.findViewById(R.id.elevation);
        chronometer = view.findViewById(R.id.timer);
        avgSpeedTextView = view.findViewById(R.id.avg_speed);
        stepTextView = view.findViewById(R.id.step);
        start = view.findViewById(R.id.Start);
        stop = view.findViewById(R.id.Stop);
        con = view.findViewById(R.id.Continue);
        clean = view.findViewById(R.id.Clean);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLocation();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                distance = 0.;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                startCalDistance = true;
                start.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.stop();
                pause = SystemClock.elapsedRealtime();
                startCalDistance = false;
                stop.setVisibility(View.INVISIBLE);
                clean.setVisibility(View.VISIBLE);
                con.setVisibility(View.VISIBLE);
            }
        });
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - pause);
                chronometer.start();
                startCalDistance = true;
                clean.setVisibility(View.INVISIBLE);
                con.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);
            }
        });
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                distance = 0.;
                avg_speed = 0.;
                refreshDistance();
                clean.setVisibility(View.INVISIBLE);
                con.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void initSensorManager() {

        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        stepTextView.setText("STEP:" + event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void refreshDistance() {
        distanceTextView.setText("DISTANCE: " + String.format("%.2f",distance * 0.000621371192237) + " MI");
        elevationTextView.setText("ELEVATION: " + String.format("%.2f",lastElevation));
        avgSpeedTextView.setText("SPEED: " + String.format("%.2f",avg_speed) + " MI/h");
    }

    private void getLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData();
            } else {
                Toast.makeText(requireContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            recordLatitude = lastLatitude;
            recordLongitude = lastLongitude;
            lastLatitude = mLastLocation.getLatitude();
            lastLongitude = mLastLocation.getLongitude();
            lastElevation = mLastLocation.getAltitude();
            LatLng position = new LatLng(lastLatitude, lastLongitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(position).title("Your Position"));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 15);
            mMap.animateCamera(yourLocation);
            if (startCalDistance == true) {
                distance = distance + getDistance(recordLatitude, lastLatitude, recordLongitude, lastLongitude);
                refreshDistance();
                avg_speed = distance * 2236.9362920532 / (SystemClock.elapsedRealtime() - chronometer.getBase());
            }
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
        activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);
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
        recordLatitude = lastLatitude;
        recordLongitude = lastLongitude;
        lastLatitude = location.getLatitude();
        lastLongitude = location.getLatitude();
        System.out.println(recordLatitude);
        System.out.println(lastLatitude);
        lastElevation = location.getAltitude();
        LatLng position = new LatLng(lastLatitude, lastLongitude);
        mMap.addMarker(new MarkerOptions().position(position).title("Your Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        if (startCalDistance == true) {
            distance = distance + getDistance(recordLatitude, lastLatitude, recordLongitude, lastLongitude);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble("distance", distance);
        outState.putBoolean("startCalDistance", startCalDistance);
        outState.putDouble("lastLatitude", lastLatitude);
        outState.putDouble("lastLongitude", lastLongitude);
        super.onSaveInstanceState(outState);
    }

}