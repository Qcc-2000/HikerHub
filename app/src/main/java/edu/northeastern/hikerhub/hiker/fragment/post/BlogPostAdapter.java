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

    private List<BlogPost> blogPosts;
    private OnBlogPostClickListener onBlogPostClickListener;

    public BlogPostAdapter(List<BlogPost> blogPosts, OnBlogPostClickListener onBlogPostClickListener) {
        this.blogPosts = blogPosts;
        this.onBlogPostClickListener = onBlogPostClickListener;
    }

    @NonNull
    @Override
    public BlogPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_post_item, parent, false);
        return new BlogPostViewHolder(view, onBlogPostClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogPostViewHolder holder, int position) {
        BlogPost blogPost = blogPosts.get(position);

        holder.postTitle.setText(blogPost.getTitle());
        holder.postContent.setText(blogPost.getContent());
        holder.postCategory.setText(blogPost.getCategory());
        holder.postDate.setText(blogPost.getPostDate());
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
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
