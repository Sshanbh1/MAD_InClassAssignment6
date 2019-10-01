package com.example.inclass06;

import android.graphics.Bitmap;

public class News {
    String title;
    String published;
    String description;
    String image;

    public News() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", published='" + published + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                '}';
    }
}