package edu.northeastern.hikerhub.hiker.fragment.Profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import edu.northeastern.hikerhub.R;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_IMAGE_PICKER = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private String userHikingLevel = "Beginner"; // Set a default hiking level

    private ImageView profilePicture;
    private TextView userName;
    private TextView location;
    //private Spinner hikingLevel;

    private Spinner editHikingLevel;
    private CardView profilePictureCard;
    private ImageButton editProfileButton;
    private TextView selectedHikingLevel;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


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
        if (savedInstanceState != null) {
            userName.setText(savedInstanceState.getString("user_name"));
            userHikingLevel = savedInstanceState.getString("user_hiking_level");
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
//        hikingLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                updateSelectedHikingLevel();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        //setupSpinner();
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

            User user = new User(
                    userName.getText().toString(),
                    location.getText().toString(),
                    selectedHikingLevel.getText().toString().replace("Hiking Level: ", ""),
                    profilePicture.toString()
                   // null // You can store the profile picture URL here if you have one
            );

            databaseReference.child(userId).setValue(user)
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
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userName.setText(user.name);
                        location.setText(user.location);
                        selectedHikingLevel.setText("Hiking Level: " + user.hikingLevel);
                        // Load profile picture from the URL if available
                        if (user.profilePictureUrl != null && !user.profilePictureUrl.isEmpty()) {
                            // Replace this with your preferred image loading library, e.g. Glide, Picasso, etc.
                            // Glide.with(requireContext()).load(user.profilePictureUrl).into(profilePicture);
                        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        EditText editUserName = view.findViewById(R.id.edit_user_name);
       // Spinner editHikingLevelSpinner = view.findViewById(R.id.edit_hiking_level_spinner);
        editHikingLevel = view.findViewById(R.id.edit_hiking_level_spinner);
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
        editHikingLevel.setSelection(spinnerPosition);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Save changes button click listener
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update user name and hiking level
                userName.setText(editUserName.getText().toString());
                userHikingLevel = editHikingLevel.getSelectedItem().toString();
                updateSelectedHikingLevel();
                saveUserProfileToFirebase();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateSelectedHikingLevel() {
       // String selectedLevel = editHikingLevel.getSelectedItem().toString();
        selectedHikingLevel.setText("Hiking Level: " + userHikingLevel);
    }

    private void setupSpinner() {
        // Spinner setup code
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.hiking_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editHikingLevel.setAdapter(adapter);
        updateSelectedHikingLevel();
    }


    private void loadImageFromGallery(Uri imageUri) {
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