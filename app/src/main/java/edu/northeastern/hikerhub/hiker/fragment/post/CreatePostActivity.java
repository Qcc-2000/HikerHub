package edu.northeastern.hikerhub.hiker.fragment.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.hiker.fragment.Profile.User;
import edu.northeastern.hikerhub.hiker.fragment.home.Trail;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private TextInputLayout tilPostTitle;
    private TextInputEditText etPostTitle;
    private TextInputLayout tilPostContent;
    private TextInputEditText etPostContent;
    private MaterialButton btnSubmitPost;
    private Spinner spinnerCategory;
    private CheckBox checkBoxRecommend;
    DatabaseReference postsRef;
    DatabaseReference trailsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        tilPostTitle = findViewById(R.id.til_post_title);
        etPostTitle = findViewById(R.id.et_post_title);
        tilPostContent = findViewById(R.id.til_post_content);
        etPostContent = findViewById(R.id.et_post_content);
        btnSubmitPost = findViewById(R.id.btn_submit_post);
        spinnerCategory = findViewById(R.id.spinner_category);
        checkBoxRecommend = findViewById(R.id.checkbox_recommend);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etPostTitle.getText().toString().trim();
                String content = etPostContent.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString().trim();
                boolean isRecommended = checkBoxRecommend.isChecked();
                String currentDate = LocalDate.now().toString();


                if (validateInput(title, content, category)) {
                    // Save the new post to your backend here
                    saveNewPost(title, content, category, isRecommended, currentDate);
                    //savePostToFirebase(title, content, category, isRecommended, currentDate);
                }
            }
        });
    }

    private boolean validateInput(String title, String content, String category) {
        boolean isValid = true;

        if (title.isEmpty()) {
            tilPostTitle.setError(getString(R.string.error_title_empty));
            isValid = false;
        } else {
            tilPostTitle.setError(null);
        }

        if (content.isEmpty()) {
            tilPostContent.setError(getString(R.string.error_content_empty));
            isValid = false;
        } else {
            tilPostContent.setError(null);
        }

        if (category.isEmpty()) {
            tilPostContent.setError(getString(R.string.error_category_empty));
            isValid = false;
        }

        return isValid;
    }

    private void saveNewPost(String title, String content, String category, boolean isRecommended, String postDate) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String author = currentUser.getEmail();
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
            String postId = postsRef.push().getKey();

            BlogPostItem post = new BlogPostItem(
                    userId,title, content, category, isRecommended, author, postDate
            );

            if (postId != null) {
                postsRef.child(postId).setValue(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Post saved successfully
                                Toast.makeText(getApplicationContext(), "Post saved successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error saving post
                                Toast.makeText(getApplicationContext(), "Error saving post", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                        // After saving, return to MainActivity and update the list of posts
                Toast.makeText(CreatePostActivity.this, getString(R.string.post_created), Toast.LENGTH_SHORT).show();
                finish();
            }

        }

//        // Backend logic
//        String author = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        Trail trail1;
//
//        postsRef = database.getReference("posts");
//        trailsRef = database.getReference("trails");
//
//        // Create a new post object with the input data
//        BlogPostItem post = new BlogPostItem(
//                title, content, category, isRecommended, author, postDate
//        );
//
//        if (isRecommended) {
//            trail1 = new Trail(category, 1);
//        } else {
//            trail1 = new Trail(category, 0);
//        }
//
//
//        // Generate a new unique key for the post
//        String postKey = postsRef.push().getKey();
//
//        // Generate a new unique key for the post
//        String trailKey = trailsRef.push().getKey();
//
//        trailsRef.orderByChild("name").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Handle data
//                System.out.println(dataSnapshot.getValue().toString() + "OOOMMMGGGGGGG !!!!");
////                Trail trail = new Trail(category, 1);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle error
//            }
//        });
//
//
//        // Add the post object to the "trails" reference in the Realtime Database
//        trailsRef.child(trailKey).setValue(trail1);
//
//        // Add the post object to the "posts" reference in the Realtime Database
//        postsRef.child(postKey).setValue(post)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(CreatePostActivity.this, getString(R.string.post_created), Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(CreatePostActivity.this, getString(R.string.post_creation_failed), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//
//        // After saving, return to MainActivity and update the list of posts
//        Toast.makeText(CreatePostActivity.this, getString(R.string.post_created), Toast.LENGTH_SHORT).show();
//        finish();
    }
}

