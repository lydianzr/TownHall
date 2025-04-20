package com.example.townhall;

import android.net.Uri;

public class Ticket {
    private String type;
    private String category;
    private String receiver;
    private String description;
    private String date;
    private String time;
    private String location;
    private String imageUri;

    private String userId;

    public Ticket() {
    }
    public Ticket(String type, String category, String receiver, String description,
                  String date, String time, String location, String imageUri, String userId) {
        this.type = type;
        this.category = category;
        this.receiver = receiver;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.imageUri = imageUri;
        this.userId = userId;
    }

    // Getters and setters (if needed)

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }



    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    //ni untuk display
    private String id;

    public void setId(String displayId) {
        this.id = displayId;
    }

    public String getId() {
        return id;
    }

    ///////\

    private String uri;
    public String getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = String.valueOf(uri);
    }


    private String status;
    public void setStatus(String status) {
        // Default to "Pending Investigation" for new tickets
        this.status = "Pending Investigation";
    }

    public String getStatus() {
        return status;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
