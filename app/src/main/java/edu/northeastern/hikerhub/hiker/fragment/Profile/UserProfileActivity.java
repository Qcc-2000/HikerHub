package edu.northeastern.hikerhub.hiker.fragment.Profile;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.hiker.fragment.post.BlogPostAdapter;
import edu.northeastern.hikerhub.hiker.fragment.post.BlogPostItem;
import edu.northeastern.hikerhub.hiker.fragment.post.PostDetailActivity;

public class UserProfileActivity extends AppCompatActivity implements BlogPostAdapter.OnBlogPostClickListener {
    private String userId;
    private ImageView profilePicture;
    private TextView userName;
    private TextView location;
    private TextView selectedHikingLevel;

    private RecyclerView profilePostsRecyclerView;
    private BlogPostAdapter blogPostAdapter;

    private List<BlogPostItem> blogPostItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        init();
        if (getIntent().hasExtra("user_id")) {
            userId = getIntent().getStringExtra("user_id");
            loadUserProfile(userId);
            loadUserPosts(userId);
        } else {
            Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void init()
    {
        userName = findViewById(R.id.user_name_display);
        location = findViewById(R.id.user_location_display);
        selectedHikingLevel = findViewById(R.id.selected_hiking_level_display);
        profilePicture = findViewById(R.id.profile_picture_display);

        profilePostsRecyclerView = findViewById(R.id.profile_posts_recycler_view_display);
        profilePostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<BlogPostItem> items = new ArrayList<>();
        blogPostAdapter = new BlogPostAdapter(items, UserProfileActivity.this);

        profilePostsRecyclerView.setAdapter(blogPostAdapter);

    }

    private void loadUserProfile(String userId) {
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
                        loadImageFromGallery(user.profilePictureUrl);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error loading profile information", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadUserPosts(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        Query userPostsQuery = postsRef.orderByChild("userId").equalTo(userId);

        userPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blogPostItems = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BlogPostItem post = postSnapshot.getValue(BlogPostItem.class);
                    if (post != null) {
                        blogPostItems.add(post);
                    }
                }
                blogPostAdapter.setBlogPostItems(blogPostItems);
                blogPostAdapter.notifyDataSetChanged();
                // Use the userPosts list to update the UI, e.g., display the posts in a RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while retrieving posts
                Toast.makeText(getApplicationContext(), "Error loading user posts", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadImageFromGallery(String imageUri) {
        Glide.with(this)
                .load(imageUri)
                .into(profilePicture);
    }


    @Override
    public void onBlogPostClick(int position) {
        BlogPostItem blogPostItem = blogPostItems.get(position);

        System.out.println(blogPostItem.isRecommended() + "IIIIMMMMMMMHHHHEEERRERERERERERERE");
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("title", blogPostItem.getTitle());
        intent.putExtra("content", blogPostItem.getContent());
        intent.putExtra("category", blogPostItem.getCategory());
        intent.putExtra("postDate", blogPostItem.getPostDate());
        intent.putExtra("author", blogPostItem.getAuthor());
        intent.putExtra("recommended", blogPostItem.isRecommended());
        intent.putExtra("user_id", blogPostItem.getUserId());
        startActivity(intent);
    }
}