package edu.northeastern.hikerhub.hiker.fragment.post;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import edu.northeastern.hikerhub.R;

public class BlogPostItemActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postDate;
    private TextView postCategory;
    private TextView recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_item);

        postTitle = findViewById(R.id.post_title);
        postCategory = findViewById(R.id.post_category);
        postDate = findViewById(R.id.post_date);

        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String category = getIntent().getStringExtra("category");

        postTitle.setText(title);
        postDate.setText(date);
        postCategory.setText(category);

    }
}