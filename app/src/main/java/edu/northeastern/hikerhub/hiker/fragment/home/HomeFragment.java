package edu.northeastern.hikerhub.hiker.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.northeastern.hikerhub.R;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    private RecyclerView trailsRecView;
    private TrialRecViewAdapter trialRecViewAdapter;
    private FloatingActionButton btnloadMap;
    private SupportMapFragment mapFragment;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        trailsRecView = rootView.findViewById(R.id.trailsRecView);
        Context context = requireContext();
        trialRecViewAdapter = new TrialRecViewAdapter(context);

        trailsRecView.setAdapter(trialRecViewAdapter);
        trailsRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Trail> trails = new ArrayList<>();
        trails.add(new Trail("Rainer", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR3QG3OrPdZnkd-PBMrdWIrhqcqkfMSFdBSAA&usqp=CAU"));
        trails.add(new Trail("Apple", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR3QG3OrPdZnkd-PBMrdWIrhqcqkfMSFdBSAA&usqp=CAU"));
        trails.add(new Trail("PooPoint", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR3QG3OrPdZnkd-PBMrdWIrhqcqkfMSFdBSAA&usqp=CAU"));
        trails.add(new Trail("Mount", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR3QG3OrPdZnkd-PBMrdWIrhqcqkfMSFdBSAA&usqp=CAU"));
        trialRecViewAdapter.setTrails(trails);

        btnloadMap = rootView.findViewById(R.id.floatingLoadMap);
        btnloadMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Check if the map is already added to the container
                mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
                //mapFragment.getMapAsync(this);

                if (mapFragment == null) {
                    // Map fragment is not added, so add it to the container
                    mapFragment = new SupportMapFragment();
                    fragmentTransaction.add(R.id.fragment_container, mapFragment, "MAP_FRAGMENT");
                }

                // Toggle between the RecyclerView and the SupportMapFragment
                if (trailsRecView.getVisibility() == View.VISIBLE) {
                    trailsRecView.setVisibility(View.GONE);
                    btnloadMap.setImageResource(R.drawable.ic_list);
                    fragmentTransaction.show(mapFragment);
                } else {
                    trailsRecView.setVisibility(View.VISIBLE);
                    btnloadMap.setImageResource(R.drawable.ic_map);
                    fragmentTransaction.hide(mapFragment);
                }
                fragmentTransaction.commit();
            }
        });

        //addMakerInMap(1058541.663,632793.8749, "TrailName");

        return rootView;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }
    public void addMakerInMap(double x, double y, String trailName) {
        // Add a marker to the map
        LatLng markerPos = new LatLng(x, y);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.addMarker(new MarkerOptions()
                        .position(markerPos)
                        .title(trailName));
                        //.snippet("One of the most iconic buildings in Australia"));//description
            }
        });
    }


}

