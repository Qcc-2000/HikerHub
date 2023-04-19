package edu.northeastern.hikerhub.hiker.fragment.post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.northeastern.hikerhub.R;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        // Load blog post data from your backend here
        loadBlogPosts();

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

        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("title", blogPostItem.getTitle());
        intent.putExtra("content", blogPostItem.getContent());
        startActivity(intent);
    }

    private void loadBlogPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogPostItems = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BlogPostItem blogPostItem = postSnapshot.getValue(BlogPostItem.class);
                    blogPostItems.add(blogPostItem);
                }

                blogPostAdapter = new BlogPostAdapter(blogPostItems, PostsFragment.this);
                recyclerView.setAdapter(blogPostAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
