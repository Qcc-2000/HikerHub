package edu.northeastern.hikerhub.hiker.fragment.post;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.northeastern.hikerhub.R;
import android.content.Intent;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
public class PostsFragment extends Fragment implements BlogPostAdapter.OnBlogPostClickListener {

    private RecyclerView recyclerView;
    private BlogPostAdapter blogPostAdapter;
    private List<BlogPostItem> blogPostItems;
    private FloatingActionButton fabCreatePost;
    private DatabaseReference postsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        // Initialize DatabaseReference
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<BlogPostItem> items = new ArrayList<>();
        blogPostAdapter = new BlogPostAdapter(items,PostsFragment.this);

        recyclerView.setAdapter(blogPostAdapter);

        // Load blog post data from your backend here
        //loadBlogPosts();
        loadUserPosts();
        // Initialize FloatingActionButton
        fabCreatePost = view.findViewById(R.id.fab_create_post);
        fabCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CreatePostActivity here
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onBlogPostClick(int position) {
        BlogPostItem blogPostItem = blogPostItems.get(position);

        System.out.println(blogPostItem.isRecommended() + "IIIIMMMMMMMHHHHEEERRERERERERERERE");
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("user_id", blogPostItem.getUserId());
        intent.putExtra("title", blogPostItem.getTitle());
        intent.putExtra("content", blogPostItem.getContent());
        intent.putExtra("category", blogPostItem.getCategory());
        intent.putExtra("postDate", blogPostItem.getPostDate());
        intent.putExtra("author", blogPostItem.getAuthor());
        intent.putExtra("recommended", blogPostItem.isRecommended());

        startActivity(intent);
    }


    private void loadUserPosts() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
        {
            String userId = currentUser.getUid();
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
            //Query userPostsQuery = postsRef.orderByChild("userId").equalTo(userId);

            postsRef.addValueEventListener(new ValueEventListener() {
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
                    Toast.makeText(getActivity(), "Error loading user posts", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
