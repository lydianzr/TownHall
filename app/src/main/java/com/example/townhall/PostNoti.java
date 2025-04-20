package com.example.townhall;

public class PostNoti {

    private String message;
    private long timestamp;
    private String postId;

    public PostNoti() {
        // Default constructor required for Firebase
    }

    public PostNoti(String message, long timestamp, String postId) {
        this.message = message;
        this.timestamp = timestamp;
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
