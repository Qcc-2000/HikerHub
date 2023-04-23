package edu.northeastern.hikerhub.hiker.fragment.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.hiker.fragment.Profile.ProfileFragment;
import edu.northeastern.hikerhub.hiker.fragment.Profile.UserProfileActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PostDetailActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postContent;
    private TextView postDate;
    private TextView postCategory;
    private TextView postAuthor;
    private TextView postRecommend;

    private ImageView userProfileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postTitle = findViewById(R.id.post_title);
        postContent = findViewById(R.id.post_content);
        postCategory = findViewById(R.id.post_category);
        postDate = findViewById(R.id.post_date);
        postAuthor = findViewById(R.id.post_author);
        postRecommend = findViewById(R.id.recommendOrNot);
        userProfileIcon = findViewById(R.id.user_profile_icon);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String category = getIntent().getStringExtra("category");
        String date = getIntent().getStringExtra("postDate");
        String author = getIntent().getStringExtra("author");
        String userId = getIntent().getStringExtra("user_id");
        boolean recommend = getIntent().getBooleanExtra("recommended", true);

        postTitle.setText(title);
        postContent.setText(content);
        postCategory.setText(category);
        postDate.setText(date);
        postAuthor.setText(author);

        if (recommend) {
            postRecommend.setText("recommends this trail");
        } else {
            postRecommend.setText("does not recommend this trail");
        }
        userProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId == null)
                {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                intent.putExtra("user_id", userId);

                startActivity(intent);
            }
        });
    }
}
