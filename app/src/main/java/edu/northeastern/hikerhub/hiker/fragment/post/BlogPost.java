package edu.northeastern.hikerhub.hiker.fragment.post;

import java.time.LocalDate;

public class BlogPost {
    private String title;
    private String content;
    private String category;
    private boolean recommended;
    private String author;
    private String postDate;

    public BlogPost(String title, String content, String category, boolean recommended, String author, String postDate) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.recommended = recommended;
        this.author = author;
        this.postDate = postDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
