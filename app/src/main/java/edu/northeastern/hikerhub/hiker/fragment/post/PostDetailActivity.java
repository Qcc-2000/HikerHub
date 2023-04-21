package edu.northeastern.hikerhub.hiker.fragment.post;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.northeastern.hikerhub.R;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PostDetailActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postContent;
    private TextView postDate;
    private TextView postCategory;
    private TextView recommend;
    private TextView author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postTitle = findViewById(R.id.post_title);
        postContent = findViewById(R.id.post_content);
        postCategory = findViewById(R.id.post_category);
        postDate = findViewById(R.id.post_date);
        author = findViewById(R.id.post_author);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String category = getIntent().getStringExtra("category");
        String postDate = getIntent().getStringExtra("postDate");
        String author = getIntent().getStringExtra("author");

        postTitle.setText(title);
        postContent.setText(content);
        postCategory.setText(category);
//        postDate.setText(postDate);
//        author.setText(author);
    }
}
