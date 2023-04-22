package edu.northeastern.hikerhub.hiker.fragment.Profile;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.northeastern.hikerhub.MainActivity;
import edu.northeastern.hikerhub.R;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_IMAGE_PICKER = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private static final int REQUEST = 1;
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private String userHikingLevel = "Beginner"; // Set a default hiking level
    private String locationProvider = null;
    private ImageView profilePicture;
    private TextView userName;
    private TextView location;

    private CardView profilePictureCard;
    private ImageButton editProfileButton;
    private TextView selectedHikingLevel;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private LocationManager locationManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.user_name);
        location = view.findViewById(R.id.user_location);
        profilePicture = view.findViewById(R.id.profile_picture);
        profilePictureCard = view.findViewById(R.id.profile_picture_card_view);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        selectedHikingLevel = view.findViewById(R.id.selected_hiking_level);
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (savedInstanceState != null) {
            userName.setText(savedInstanceState.getString("user_name"));
            userHikingLevel = savedInstanceState.getString("user_hiking_level");
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Ask for permission
            //request
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST);
        } else {
            //provider
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(requireContext(), "No Available Locator", Toast.LENGTH_SHORT).show();
                return view;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean allGranted = true;
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                if (!entry.getValue()) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                openImagePicker();
            } else {
                Toast.makeText(requireContext(), "Permissions denied. Cannot change profile picture.", Toast.LENGTH_SHORT).show();
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    //profilePicture.setImageURI(selectedImageUri);

                    loadImageFromGallery(selectedImageUri);
                } else {
                    Toast.makeText(requireContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadUserProfileFromFirebase();
        updateSelectedHikingLevel();
        setupProfilePictureImageView();
        setupEditProfileButton();

        return view;
    }

    private void saveUserProfileToFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");


            // Get an instance of Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a reference for the profile picture
            StorageReference profilePicRef = storageRef.child("profile_pictures/" + userId + ".jpg");

            // Get the image from the ImageView as a Bitmap
            Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();

            // Compress the Bitmap to a JPEG format and convert it to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data = baos.toByteArray();

            // Upload the byte array to Firebase Storage
            UploadTask uploadTask = profilePicRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (isAdded())
                    {
                        // Get the download URL of the uploaded image
                        Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Save the download URL to the Firebase Realtime Database
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                                User user = new User(
                                        userName.getText().toString(),
                                        location.getText().toString(),
                                        selectedHikingLevel.getText().toString().replace("Hiking Level: ", ""),
                                        uri.toString() // Save the profile picture URL here
                                );

                                userRef.setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(requireContext(), "Profile information saved", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(requireContext(), "Error saving profile information", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }else {
                        Log.e(TAG, "Fragment not attached to the context.");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors
                    Toast.makeText(requireContext(), "Error uploading profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

    }
    private void loadUserProfileFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isAdded())
                    {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            userName.setText(user.name);
                            //location.setText(user.location);
                            selectedHikingLevel.setText("Hiking Level: " + user.hikingLevel);
                            // Load profile picture from the URL if available
                            if (user.profilePictureUrl != null && !user.profilePictureUrl.isEmpty()) {
                                loadImageFromGallery(user.profilePictureUrl);
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "Fragment not attached to the context.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Error loading profile information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        EditText editUserName = view.findViewById(R.id.edit_user_name);

        MaterialAutoCompleteTextView editHikingLevel = view.findViewById(R.id.edit_hiking_level_spinner);
        Button saveChangesButton = view.findViewById(R.id.save_changes_button);

        // Set the current values of user name and hiking level
        editUserName.setText(userName.getText());
        //editHikingLevel.setSelection(editHikingLevel.getSelectedItemPosition());

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.hiking_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editHikingLevel.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(userHikingLevel);
        if (spinnerPosition >= 0) {
            editHikingLevel.setText(adapter.getItem(spinnerPosition).toString(), false);
        }

        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Save changes button click listener
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update user name and hiking level
                userName.setText(editUserName.getText().toString());
                userHikingLevel = editHikingLevel.getText().toString();
                updateSelectedHikingLevel();
                saveUserProfileToFirebase();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                // allowed
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //location manager
                    locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                    //provider
                    List<String> providers = locationManager.getProviders(true);
                    if (providers.contains(LocationManager.GPS_PROVIDER)) {
                        locationProvider = LocationManager.GPS_PROVIDER;
                    } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                        locationProvider = LocationManager.NETWORK_PROVIDER;
                    } else {
                        Toast.makeText(requireContext(), "No Available Locator", Toast.LENGTH_SHORT).show();
                    }
                    //get location
                    try {
                        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                    } catch (SecurityException e) {
                    }
                }
                break;
            }
        }
    }
    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location locationDetail) {
            if(location!=null) {
                double latitude = locationDetail.getLatitude();
                double longitude = locationDetail.getLongitude();
                // Do something with the latitude and longitude values
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String cityName = address.getLocality();
                        location.setText(cityName);
                    } else {
                        location.setText("Cannot determine city name");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    location.setText("Error in getting city name");
                }

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println(provider);
        }
    };
    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }


    private void updateSelectedHikingLevel() {
       // String selectedLevel = editHikingLevel.getSelectedItem().toString();
        selectedHikingLevel.setText("Hiking Level: " + userHikingLevel);
    }

    private void loadImageFromGallery(Uri selectedImageUri) {
        if (isAdded()) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                profilePicture.setImageBitmap(bitmap);
                saveUserProfileToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Fragment not attached to the context.");
        }
    }

    private void loadImageFromGallery(String imageUri) {
        Glide.with(requireContext())
                .load(imageUri)
                .into(profilePicture);
    }

    private void setupProfilePictureImageView() {
        profilePictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionsAndOpenImagePicker();

            }
        });
    }

    private void requestPermissionsAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA});
        } else {
            openImagePicker();
        }
    }




    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        //startActivityForResult(intent, REQUEST_IMAGE_PICKER);
        imagePickerLauncher.launch(intent);

    }


    private void setupEditProfileButton() {
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit profile button click
                showEditProfileDialog();
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("user_name", userName.getText().toString());
        outState.putString("user_hiking_level", userHikingLevel);
    }

}