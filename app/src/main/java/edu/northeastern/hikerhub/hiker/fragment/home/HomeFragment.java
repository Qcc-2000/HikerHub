package edu.northeastern.hikerhub.hiker.fragment.home;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.northeastern.hikerhub.R;


public class HomeFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView topTrailsRecView;
    private RecyclerView likeTrailsRecView;
    private TrialRecViewAdapter topTrialRecViewAdapter;
    private TrialRecViewAdapter likeTrialRecViewAdapter;
    private FloatingActionButton btnLoadMap;
    private CardView cardViewTrail;
    private SearchView searchView;

    private Utils utils;
    private Map<String, Trail> allTrails;
    private final String PATHNAME = "trail.csv";
    private List<Trail> topTrails;

    private GoogleMap myMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastKnownLocation;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initiate(rootView);
        initRecViewData();

        return rootView;
    }

    private void initiate(View rootView) {
        topTrailsRecView = rootView.findViewById(R.id.topTrailsRecView);
        likeTrailsRecView = rootView.findViewById(R.id.likeTrailsRecView);
        btnLoadMap = rootView.findViewById(R.id.floatingLoadMap);
        searchView = rootView.findViewById(R.id.searchView);
        cardViewTrail = rootView.findViewById(R.id.parentTailMap);
        cardViewTrail.setVisibility(View.GONE);

        utils = Utils.getInstance(getContext(), PATHNAME);
        allTrails = utils.getAllTrails();
    }

    private void initRecViewData() {
        topTrialRecViewAdapter = new TrialRecViewAdapter(requireContext());
        topTrailsRecView.setAdapter(topTrialRecViewAdapter);
        topTrailsRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        likeTrialRecViewAdapter = new TrialRecViewAdapter(requireContext());
        likeTrailsRecView.setAdapter(likeTrialRecViewAdapter);
        likeTrailsRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //TODO：get the current location for user
        double latitude = 46.5301011;
        double longitude = -122.0326191;
        topTrails = utils.getTopTrails(latitude, longitude);
        topTrialRecViewAdapter.setTrails(topTrails);

        List<Trail> mostListTrails = utils.getMostLikeTrails();
        likeTrialRecViewAdapter.setTrails(mostListTrails);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ApplicationInfo ai = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = ai.metaData.getString("com.google.android.geo.API_KEY");
            Places.initialize(getContext(), apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("TAG", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        // Get the SupportMapFragment from the layout file
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Initialize the GoogleMap object
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        btnLoadMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topTrailsRecView.getVisibility() == View.VISIBLE) {
                    // Hide the RecyclerView and FloatingActionButton
                    topTrailsRecView.setVisibility(View.GONE);
                    likeTrailsRecView.setVisibility(View.GONE);
                    btnLoadMap.setImageResource(R.drawable.ic_list);
                    // Show the map
                    mapFragment.getView().setVisibility(View.VISIBLE);

                } else {
                    // Show the RecyclerView and FloatingActionButton
                    topTrailsRecView.setVisibility(View.VISIBLE);
                    likeTrailsRecView.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                    btnLoadMap.setImageResource(R.drawable.ic_map);
                    // Hide the map
                    mapFragment.getView().setVisibility(View.GONE);
                }
            }
        });
        cardViewTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewTrail.setVisibility(View.GONE);
                Toast.makeText(getContext(), "cardView is missing", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");
        // The map is ready, so it is safe to call getView() and hide the map.
        mapFragment.getView().setVisibility(View.GONE);
        myMap = googleMap;

        markerCurrentLocation();
        markerAllTrails();

        // Set a listener to the SearchView widget
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Use the Google Places API to search for the location
                PlacesClient placesClient = Places.createClient(getContext());
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                FindAutocompletePredictionsRequest request =
                        FindAutocompletePredictionsRequest.builder()
                                .setSessionToken(token)
                                .setQuery(query)
                                .build();
                placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener((response) -> {
                            if (!response.getAutocompletePredictions().isEmpty()) {
                                AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                                String placeId = prediction.getPlaceId();
                                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
                                FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                                placesClient.fetchPlace(placeRequest)
                                        .addOnSuccessListener((placeResponse) -> {
                                            Place place = placeResponse.getPlace();
                                            LatLng latLng = place.getLatLng();
                                            if (latLng != null) {
                                                // Move the camera to the new location
                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(latLng)
                                                        .zoom(12)
                                                        .build();
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }

                                            Log.e(TAG, "topTrailsRecView =====================");
                                            topTrails = utils.getTopTrails(latLng.latitude, latLng.longitude);
                                            // Update the adapter with the new list of top trails
                                            topTrialRecViewAdapter.setTrails(topTrails);
                                            // Notify the adapter of the changes
                                            topTrialRecViewAdapter.notifyDataSetChanged();
                                        });
                            }
                        });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    private void markerAllTrails() {
        List<MarkerItem> markerItems = new ArrayList<>();

        for (Trail trail : allTrails.values()) {
            MarkerItem item = new MarkerItem(trail.getLatitude(), trail.getLongitude(),trail.getName());
            markerItems.add(item);
        }
        // Set up the ClusterManager
        ClusterManager<MarkerItem> clusterManager = new ClusterManager<MarkerItem>(getContext(), myMap);
        clusterManager.addItems(markerItems);
        clusterManager.setAnimation(false); // Disable clustering animation
        clusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<MarkerItem>()); // Use a non-hierarchical clustering algorithm
        clusterManager.setRenderer(new DefaultClusterRenderer<MarkerItem>(getContext(), myMap, clusterManager)); // Use the default renderer

        // Set an info window click listener on the cluster manager
        clusterManager.setOnClusterItemInfoWindowClickListener(item -> {
            Log.e(TAG, "onClusterItemInfoWindowClick is called================");
            cardViewTrail.setVisibility(View.VISIBLE);
            TextView txtTitle = cardViewTrail.findViewById(R.id.txtTrailNameMap);
            TextView txtLenTime = cardViewTrail.findViewById(R.id.txtTrailLenTimeMap);
            TextView txtDifficulty = cardViewTrail.findViewById(R.id.txtTrailDifficultyMap);
            ImageView imgTrailMap = cardViewTrail.findViewById(R.id.imgTrailMap);
            txtTitle.setText(item.getTitle());


            String nameTrail = item.getTitle();
            Trail trail = allTrails.get(nameTrail);
            txtLenTime.setText(trail.getLenAndTime());
            txtDifficulty.setText(trail.getDifficulty().toString());

            Glide.with(requireContext())
                    .asBitmap()
                    .load(trail.getImgUrl())
                    .into(imgTrailMap);
            Toast.makeText(getContext(), item.getTitle() + " clicked", Toast.LENGTH_LONG).show();
        });
        // Register the ClusterManager as the click listener for markers
        myMap.setOnInfoWindowClickListener(clusterManager);
        myMap.setOnCameraIdleListener(clusterManager);
        clusterManager.cluster();
    }
    private void markerCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastKnownLocation = location;
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //myMap.addMarker(new MarkerOptions().position(latLng).title("Issaquah"));
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }
                }
            });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


}

