package edu.northeastern.hikerhub.hiker.fragment.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.hikerhub.R;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.BlogPostViewHolder> {


    private List<BlogPostItem> blogPostItems;
    private OnBlogPostClickListener onBlogPostClickListener;

    public BlogPostAdapter(List<BlogPostItem> blogPostItems, OnBlogPostClickListener onBlogPostClickListener) {
        this.blogPostItems = blogPostItems;
        this.onBlogPostClickListener = onBlogPostClickListener;
    }
    public List<BlogPostItem> getBlogPostItems() {
        return blogPostItems;
    }

    public void setBlogPostItems(List<BlogPostItem> blogPostItems) {
        this.blogPostItems = blogPostItems;
    }

    @NonNull
    @Override
    public BlogPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_blog_post_item, parent, false);
        return new BlogPostViewHolder(view, onBlogPostClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogPostViewHolder holder, int position) {
        BlogPostItem blogPostItem = blogPostItems.get(position);

        if (holder.postTitle != null) {
            holder.postTitle.setText(blogPostItem.getTitle());
        }

        if (holder.postContent != null) {
            holder.postContent.setText(blogPostItem.getContent());
        }

        if (holder.postCategory != null) {
            holder.postCategory.setText(blogPostItem.getCategory());
        }

        if (holder.postDate != null) {
            holder.postDate.setText(blogPostItem.getPostDate());
        }

    }

    @Override
    public int getItemCount() {
        return blogPostItems.size();
    }

    public interface OnBlogPostClickListener {
        void onBlogPostClick(int position);
    }

    public static class BlogPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView postTitle, postContent, postCategory, postDate;
        OnBlogPostClickListener onBlogPostClickListener;

        public BlogPostViewHolder(@NonNull View itemView, OnBlogPostClickListener onBlogPostClickListener) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.post_title);
            postContent = itemView.findViewById(R.id.post_content);
            postCategory = itemView.findViewById(R.id.post_category);
            postDate = itemView.findViewById(R.id.post_date);

            this.onBlogPostClickListener = onBlogPostClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBlogPostClickListener.onBlogPostClick(getAdapterPosition());
        }
    }
}
